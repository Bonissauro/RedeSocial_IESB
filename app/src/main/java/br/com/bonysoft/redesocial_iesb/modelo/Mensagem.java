package br.com.bonysoft.redesocial_iesb.modelo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by panar on 08/10/2016.
 */

@IgnoreExtraProperties
public class Mensagem extends RealmObject {
    @PrimaryKey
    public String id;

    private String de;
    private String para;
    private String texto;
    private String timestamp;

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

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Exclude
    public void setTimestampByDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            this.timestamp = sdf.format(date);
        }catch (Exception e){}
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    @Override
    @Exclude
    public String toString() {
        return "Mensagem{" +
                "id='" + id + '\'' +
                ", de='" + de + '\'' +
                ", para='" + para + '\'' +
                ", texto='" + texto + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
