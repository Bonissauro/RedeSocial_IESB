package br.com.bonysoft.redesocial_iesb.modelo;


import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by panar on 08/10/2016.
 */

public class MensagemRealm extends RealmObject{

    @PrimaryKey
    public String id;

    public String de;
    public String para;
    public String texto;
    public String timestamp;

    public MensagemRealm() {
        de = "";
        para = "";
        texto = "";
        timestamp = "";
    }

    public MensagemRealm(String id_msg,Mensagem m) {
        de = m.de;
        para = m.para;
        texto = m.texto;
        timestamp = m.timestamp;
        id = id_msg;
    }

    public MensagemRealm(String de, String para, String texto, String timestamp) {
        this.de = de;
        this.para = para;
        this.texto = texto;
        this.timestamp = timestamp;
    }

    public MensagemRealm(String de, String para, String texto) {
        this(de, para, texto,new Date());
    }

    public MensagemRealm(String de, String para, String texto, Date date) {
        this.de = de;
        this.para = para;
        this.texto = texto;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.timestamp = sdf.format(date);
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
