package br.com.bonysoft.redesocial_iesb.modelo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by panar on 03/10/2016.
 */
@IgnoreExtraProperties
public class LocalizacaoFireBase {

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

    public String email;
    public Double latitude;
    public Double longitude;

    public LocalizacaoFireBase(){
        latitude = 0d;
        longitude = 0d;
    }

    public LocalizacaoFireBase(String email, String latitude, String longitude) {
        this.email = email;
        this.latitude = new Double(latitude);
        this.longitude =  new Double(longitude);
    }

    public LocalizacaoFireBase(String email, double latitude, double longitude) {
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Exclude
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
                "email='" + email + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
