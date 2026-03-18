package src;

import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;



public class GameServer extends WebSocketServer {

    private ConcurrentHashMap<WebSocket, PlayerSession> jugadores = new ConcurrentHashMap<>();

    public GameServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Nuevo jugador conectado");

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Jugador desconectado");
        
        DBConnector.savePlayerInfo(jugadores.get(conn));


        broadcast("{\"tipo\": \"DESCONEXION\", \"id\": \"" + conn.hashCode() + "\"}");
    }

    
    //TO-DO: HACER EL SIGN IN EN EL VSCODE Y QUE SE CREE EL PERSONAJE ASOCIDADA A ESE USERID Y CON POSICION INICIAL
    
    
    @Override
    public void onMessage(WebSocket conn, String message) {
    	
    	JSONObject json = new JSONObject(message);
        String tipo = json.getString("tipo");

    	if(tipo.equals("MOVIMIENTO")) {
    		
    		json.put("id", conn.hashCode());
    		
    		jugadores.get(conn).x = json.getInt("x");
    		jugadores.get(conn).y = json.getInt("y");
    		
    		broadcastExcept(conn,json.toString());
    		
    	}
    	else if (tipo.equals("LOGIN")) {
    		String user = json.getString("usuario");
            String pass = json.getString("password");
            int userId = DBConnector.validateUser(user, pass);
            if ( userId != -1)  { //testUser testPass
				
            	// LOGIN EXITOSO
                int id = conn.hashCode();
                PlayerSession session = DBConnector.loadUserPlayer(userId);
                jugadores.put(conn, session);

                JSONObject exito = new JSONObject();
                exito.put("tipo", "LOGIN_EXITO");
                exito.put("id", id);
                exito.put("userId", session.userId);
                exito.put("nombre", session.nombre);
                exito.put("x", session.x);
                exito.put("y", session.y);
                conn.send(exito.toString());
                
                
            	
        		broadcastExcept(conn,session.toJSON().put("tipo", "NUEVO_JUGADOR").toString());
                
                //enviar resto de jugadores al nuevo jugador
        		for( WebSocket client : jugadores.keySet()) {
        			if(conn != client) {
        				conn.send(jugadores.get(client).toJSON().put("tipo", "NUEVO_JUGADOR").toString());
        			}
        		}
        		System.out.println(" LOGIN | Usuario: " + user + " Contraseña: " + pass);
			} else {
				// LOGIN FALLIDO
				JSONObject fallo = new JSONObject();
				fallo.put("tipo", "LOGIN_FALLIDO");
				conn.send(fallo.toString());
				System.out.println(" LOGIN FALLIDO | Usuario: " + user + " Contraseña: " + pass);
            }
            
    	}
    	else if(tipo.equals("REGISTRO")) {
			String user = json.getString("usuario");
			String pass = json.getString("password");
			String email = json.getString("email");
			
			boolean exito = DBConnector.signInUser(user, pass, email);
			
			JSONObject respuesta = new JSONObject();
			respuesta.put("tipo", "REGISTRO_RESPUESTA");
			respuesta.put("exito", exito);
			conn.send(respuesta.toString());
			
			System.out.println(" REGISTRO | Usuario: " + user + " Contraseña: " + pass + " Email: " + email);
		}
    	
    	
        
    }
    
    public void broadcastExcept(WebSocket conn, String data) {
    	
    	for( WebSocket client : jugadores.keySet()) {
    		if(conn != client) {
    			client.send(data);
    		}
    	}
    	
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Servidor Java iniciado en el puerto: " + getPort());
    }

    public static void main(String[] args) {
        int puerto = 8080;
        GameServer server = new GameServer(puerto);
        server.start();  
    }
}