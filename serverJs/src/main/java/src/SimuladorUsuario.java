package src;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.Scanner;

public class SimuladorUsuario extends WebSocketClient {

    public SimuladorUsuario(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("[SIMULADOR] Conectado al servidor con éxito.");
        // Al conectar, enviamos un mensaje inicial
        send("{\"tipo\": \"conexion\", \"nombre\": \"Bot_Prueba\"}");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("[SERVIDOR DICE]: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("[SIMULADOR] Conexión cerrada.");
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("[ERROR]: " + ex.getMessage());
    }

    public static void main(String[] args) throws Exception {
        // La dirección de tu servidor
        String url = "ws://localhost:8080";
        SimuladorUsuario cliente = new SimuladorUsuario(new URI(url));
        
        cliente.connect();

        // Pequeño bucle para enviar mensajes desde la consola de Java
        Scanner sc = new Scanner(System.in);
        System.out.println("Escribe 'mover' para simular movimiento o 'salir' para cerrar:");

        while (true) {
            String input = sc.nextLine();
            if (input.equalsIgnoreCase("salir")) break;

            if (input.equalsIgnoreCase("mover")) {
                // Simulamos un JSON de movimiento
                String jsonMov = "{\"tipo\": \"movimiento\", \"x\": 250, \"y\": 180, \"dir\": 3}";
                cliente.send(jsonMov);
                System.out.println("-> Enviado: " + jsonMov);
            }
            
            if (input.equalsIgnoreCase("e")) {
                // Simulamos pulsar la tecla E (interacción)
                String jsonInterac = "{\"tipo\": \"INTERACCION_OBJETO\", \"llave\": \"10-5\", \"texto\": \"Hola desde el bot!\"}";
                cliente.send(jsonInterac);
                System.out.println("-> Enviada interacción.");
            }
        }

        cliente.close();
    }
}