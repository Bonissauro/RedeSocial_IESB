package br.com.bonysoft.redesocial_iesb.modelo;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by carlospanarello on 12/10/2016.
 */

public class Localizacao extends RealmObject{
    /*
        rede-social-iesb
            localizacao
                -KT05E-nZGzAmE1q1PS2
                    email: "bonissauro@gmail.com"
                    latitude: "15.6543783"
                    longitude: "47.83906625"
            localizacao
                -GR05E-nZGzAmE1SAs2
                    email: "bonissauro@gmail.com"
                    latitude: "15.6543783"
                    longitude: "47.83906625"
     */
    @PrimaryKey
    private String email;
    private Double latitude;
    private Double longitude;

    public Localizacao(){
        latitude = 0d;
        longitude = 0d;
    }

    public Localizacao(String email, String latitude, String longitude) {
        this.email = email;
        try{
            this.latitude = new Double(latitude);
        }catch (Exception e){}

        try{
            this.longitude  = new Double(longitude);
        }catch (Exception e){}
    }

    public Localizacao(String email, double latitude, double longitude) {
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Localizacao{" +
                "email='" + email + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
