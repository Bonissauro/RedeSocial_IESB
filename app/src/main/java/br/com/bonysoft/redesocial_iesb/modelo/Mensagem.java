package br.com.bonysoft.redesocial_iesb.modelo;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by panar on 08/10/2016.
 */

public class Mensagem implements Serializable{

    public String de;
    public String para;
    public String texto;
    public String timestamp;

    public Mensagem() {
        de = "";
        para = "";
        texto = "";
        timestamp = "";
    }

    public Mensagem(String de, String para, String texto, String timestamp) {
        this.de = de;
        this.para = para;
        this.texto = texto;
        this.timestamp = timestamp;
    }

    public Mensagem(String de, String para, String texto) {
        this(de, para, texto,new Date());
    }

    public Mensagem(String de, String para, String texto, Date date) {
        this.de = de;
        this.para = para;
        this.texto = texto;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.timestamp = sdf.format(date);
    }

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("de", de);
        result.put("para", para);
        result.put("texto", texto);
        result.put("timeStamp", timestamp);

        return result;
    }

    @Override
    public String toString() {
        return "Mensagem{" +
                "de='" + de + '\'' +
                ", para='" + para + '\'' +
                ", texto='" + texto + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
