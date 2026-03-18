package src;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

public class DBConnector {
	
	private static String URL = "jdbc:mariadb://localhost:3306/mygamedb";
	private static String USER = "gameDev";
	private static String PASSWORD = "gameDev";
	
	public static int validateUser(String username, String password) {
		
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
			System.out.println("Conexión exitosa a la base de datos");
			String comando = "SELECT contrasena_hash, id FROM usuarios WHERE nombre_usuario = '" + username + "'";
			
			try (Statement stmt = conn.createStatement()) {
				ResultSet rs = stmt.executeQuery(comando);
				
				if(rs.next()) {
					String storedHash = rs.getString("contrasena_hash");
					if( BCrypt.checkpw(password, storedHash)) {
						return rs.getInt("id");
					}
					
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	
	public static boolean signInUser(String username, String password, String email) {
		
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
			
			String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
			
			System.out.println("Conexión exitosa a la base de datos");
			String comando = "INSERT INTO usuarios (nombre_usuario, contrasena_hash, email) VALUES ('" + username + "', '" + hashedPassword + "', '" + email + "')";
			
			try (Statement stmt = conn.createStatement()) {
				int rowsAffected = stmt.executeUpdate(comando);
				
			}
			
			comando = "INSERT INTO players (usuarioId, nombre, x, y) VALUES ((SELECT id FROM usuarios WHERE nombre_usuario = '" + username + "'), '" + username + "', 352, 816)";
			try (Statement stmt = conn.createStatement()) {
				int rowsAffected = stmt.executeUpdate(comando);
				System.out.println("Usuario registrado: " + username);
				return rowsAffected > 0;
				
			}
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	public static PlayerSession loadUserPlayer(int userId) {
		
		
		
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
			System.out.println("Conexión exitosa a la base de datos");
			String comando = "SELECT * FROM players WHERE usuarioId = " + userId + " LIMIT 1";
			
			try (Statement stmt = conn.createStatement()) {
				ResultSet rs = stmt.executeQuery(comando);
				
				if(rs.next()) {
					PlayerSession session = new PlayerSession(rs.getInt("id"),userId, rs.getString("nombre"),rs.getInt("x"),rs.getInt("y"));
					return session;
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static boolean savePlayerInfo(PlayerSession session) { //DE MOMENTO SOLO GUARDA LA POSICION, PERO SE PUEDE AMPLIAR PARA GUARDAR MAS INFO COMO NIVEL, INVENTARIO, ETC.
		
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
			
			System.out.println("Conexión exitosa a la base de datos");
			String comando = "UPDATE players SET x = " + session.x + ", y = " + session.y + " WHERE id = '" + session.id + "'";
			
			try (Statement stmt = conn.createStatement()) {
				stmt.executeUpdate(comando);
				return true;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
}
