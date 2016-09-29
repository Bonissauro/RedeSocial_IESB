package br.com.bonysoft.redesocial_iesb.modelo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by carlospanarello on 29/09/16.
 */

public class Localizacao {
    public String id_localizacao;

    public String email;
    public String latitude;
    public String longitude;

    public Localizacao(String email, String latitude, String longitude) {
           this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId_localizacao() {
        return id_localizacao;
    }

    public void setId_localizacao(String id_localizacao) {
        this.id_localizacao = id_localizacao;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("id_localizacao", id_localizacao);
        result.put("email", email);
        result.put("latitude", latitude);
        result.put("longitude", longitude);

        return result;

    }
}
