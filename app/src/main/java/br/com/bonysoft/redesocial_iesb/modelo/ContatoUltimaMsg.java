package br.com.bonysoft.redesocial_iesb.modelo;

/**
 * Created by panar on 09/10/2016.
 */

public class ContatoUltimaMsg {

    public Contato contato;
    public Mensagem msg;

    public ContatoUltimaMsg(Contato c, Mensagem m){
        contato = c;
        msg = m;
    }

    public void setUltimaMensagem(Mensagem m){
        if(m !=null && m.getTimestamp()!= null && !m.getTimestamp().trim().isEmpty()){
            if(msg != null &&  msg.getTimestamp()!= null && !msg.getTimestamp().trim().isEmpty()){
                long lNovo = new Long(m.getTimestamp().replace(":","").replace(" ","").replace("-",""));
                long lAntigo = new Long(msg.getTimestamp().replace(":","").replace(" ","").replace("-",""));
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
        if(msg != null &&  msg.getTexto()!= null && !msg.getTexto().trim().isEmpty()){
            if(msg.getTexto().length()> 15){
                return msg.getTexto().substring(0,14)+" ...";
            } else {
                return msg.getTexto();
            }
        }
        return  "";
    }
}
