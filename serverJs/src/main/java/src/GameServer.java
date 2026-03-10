package src;

import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;



public class GameServer extends WebSocketServer {

    private ConcurrentHashMap<WebSocket, String> jugadores = new ConcurrentHashMap<>();

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
        jugadores.remove(conn);
        removeConnection(conn);

        broadcast("{\"tipo\": \"desconexion\", \"id\": \"" + conn.hashCode() + "\"}");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
    	
    	JSONObject json = new JSONObject(message);
        String tipo = json.getString("tipo");
    	
    	if(tipo.equals("NUEVO_JUGADOR")) {
    		
    		int id = conn.hashCode();
    		json.put("id", id);
    		
    		jugadores.put(conn, json.toString());
    		
    		JSONObject bienvenida = new JSONObject();
            bienvenida.put("tipo", "BIENVENIDA");
            bienvenida.put("id", id);
            conn.send(bienvenida.toString());
    		
    		
    		System.out.println("Registrado: " + json.getString("nombre"));
    		
    		broadcast(json.toString());
    		
    		for( WebSocket client : jugadores.keySet()) {
    			if(conn != client) {
        			conn.send(jugadores.get(client));
    			}
    		}
    	}
    	else if(tipo.equals("MOVIMIENTO")) {
    		
    		json.put("id", conn.hashCode());
    		
    		broadcastExcept(conn,json.toString());
    		
    	}
    	
    	
        
    }
    
    public void broadcastExcept(WebSocket conn, String data) {
    	
    	for( WebSocket client : jugadores.keySet()) {
    		if(conn != client) {
    			broadcast(data);
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