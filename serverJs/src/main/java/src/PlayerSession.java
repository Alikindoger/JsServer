package src;

import org.json.JSONObject;

public class PlayerSession {
    public int id;
    public String nombre;
    public int x, y;
    public String estado;
    
    public PlayerSession(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("nombre", nombre);
        json.put("x", x);
        json.put("y", y);
        json.put("anim", estado);
        return json;
    }
}