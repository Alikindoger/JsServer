package src;

import org.json.JSONObject;

public class PlayerSession {
    public int id, userId;
    public String nombre;
    public int x, y;
    public String estado;
    
    public PlayerSession(int id,int userId, String nombre,int x,int y) {
        this.id = id; // LA ID DEL JUGADOR
        this.userId = userId; // LA ID DEL USUARIO EN LA BASE DE DATOS
        this.nombre = nombre;
        this.x = x;
        this.y = y;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("userId", userId);
        json.put("nombre", nombre);
        json.put("x", x);
        json.put("y", y);
        json.put("anim", estado);
        return json;
    }
}