package br.com.bonysoft.redesocial_iesb.modelo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by carlospanarello on 29/09/16.
 */

public class LocalizacaoContatos  extends RealmObject implements Serializable {

    @PrimaryKey
    public String email;
    public String latitude;
    public String longitude;

    public LocalizacaoContatos(){}

    public LocalizacaoContatos(String email, String latitude, String longitude) {
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
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

        result.put("email", email);
        result.put("latitude", latitude);
        result.put("longitude", longitude);

        return result;
    }

    @Override
    public String toString() {
        return "LocalizacaoContatos{" +
                ", email='" + email + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
