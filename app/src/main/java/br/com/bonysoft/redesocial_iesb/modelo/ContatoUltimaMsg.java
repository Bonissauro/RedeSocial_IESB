package br.com.bonysoft.redesocial_iesb.modelo;

/**
 * Created by panar on 09/10/2016.
 */

public class ContatoUltimaMsg {

    public Contato contato;
    public MensagemRealm msg;

    public ContatoUltimaMsg(Contato c, MensagemRealm m){
        contato = c;
        msg = m;
    }

    public void setUltimaMensagem(MensagemRealm m){
        if(m !=null && m.timestamp!= null && !m.timestamp.trim().isEmpty()){
            if(msg != null &&  msg.timestamp!= null && !msg.timestamp.trim().isEmpty()){
                long lNovo = new Long(m.timestamp.replace(":","").replace(" ","").replace("-",""));
                long lAntigo = new Long(msg.timestamp.replace(":","").replace(" ","").replace("-",""));
                if(lNovo < lAntigo){
                    return;
                }

            }
            msg = m;
        }
    }

    @Override
    public String toString() {
        return "ContatoUltimaMsg{" +
                "contato=" + contato +
                ", msg=" + msg +
                '}';
    }

    public String getTextoUltimaMensagem(){
        if(msg != null &&  msg.texto!= null && !msg.texto.trim().isEmpty()){
            if(msg.texto.length()> 15){
                return msg.texto.substring(0,14)+" ...";
            } else {
                return msg.texto;
            }
        }
        return  "";
    }
}
