package br.com.bonysoft.redesocial_iesb;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.modelo.Mensagem;
import br.com.bonysoft.redesocial_iesb.modelo.MensagemRealm;
import br.com.bonysoft.redesocial_iesb.modelo.Usuario;
import br.com.bonysoft.redesocial_iesb.realm.modulo.RedeSocialRealmModule;
import br.com.bonysoft.redesocial_iesb.servicos.AlarmeEnvioPosicaoService;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class ApplicationRedeSocial extends Application {

    private static ApplicationRedeSocial instance;

    private Usuario usuarioLogado;

    private String emailRegistro;

    @Override
    public void onCreate() {

        super.onCreate();

        instance = this;

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .modules(new RedeSocialRealmModule())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        Fresco.initialize(this);

        iniciaServico();

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Constantes.CONTA_REGISTRADA, Context.MODE_PRIVATE);
        emailRegistro = sharedPref.getString(Constantes.CONTA_REGISTRADA,"");
        Log.d(Constantes.TAG_LOG,"EmailRegistro-->"+emailRegistro);
        //carregarMensagensNoRealm(emailRegistro);
        //teste();
    }

    private void teste(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<MensagemRealm> results = realm.where(MensagemRealm.class).findAll();

        for(MensagemRealm m: results){
            Log.d(Constantes.TAG_LOG,"Itens Salvos-->"+m);
        }

        realm.close();
    }

    private void iniciaServico(){
        SharedPreferences sharedPref = this.getSharedPreferences(Constantes.SERVICO, Context.MODE_PRIVATE);
        boolean envio = sharedPref.getBoolean(Constantes.SERVICO_ENVIO_EXEC,true);
        boolean rec = sharedPref.getBoolean(Constantes.SERVICO_REC_EXEC,true);

        if(envio || rec){
            Intent startServiceIntent = new Intent(getApplicationContext(), AlarmeEnvioPosicaoService.class);
            getApplicationContext().startService(startServiceIntent);
        }
    }

    public static ApplicationRedeSocial getInstance() {
        return instance;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public boolean isRegistrado(){
        return (emailRegistro != null && !emailRegistro.trim().isEmpty());
    }

    public String emailRegistrado(){
        return emailRegistro;
    }

    public void setUsuarioLogado(Usuario usuarioLogado) {

        this.usuarioLogado = usuarioLogado;

        if(usuarioLogado!=null && usuarioLogado.getEmail()!= null && !usuarioLogado.getEmail().trim().isEmpty()) {
            emailRegistro = usuarioLogado.getEmail();

            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Constantes.CONTA_REGISTRADA, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constantes.CONTA_REGISTRADA, usuarioLogado.getEmail());
            editor.commit();
        }
    }

    public void carregarMensagensNoRealm(final String emailUsuario){
        if(emailUsuario == null || emailUsuario.trim().isEmpty()){
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("mensagens");
        ref.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(Constantes.TAG_LOG,"Achou Mensagens");

                DataSnapshot dsnap = null;
                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();

                Realm realm = Realm.getDefaultInstance();

                while (it.hasNext()){
                    dsnap = it.next();
                    Log.d(Constantes.TAG_LOG,"DataSnapshot para salvar no Realm -->" + dsnap);
                    Mensagem msg = dsnap.getValue(Mensagem.class);
                    Log.d(Constantes.TAG_LOG,"Mensagem para salvar no Realm -->" + msg);
                    if(msg!= null &&
                    (msg.para.equalsIgnoreCase(emailUsuario) || msg.de.equalsIgnoreCase(emailUsuario))){

                        Log.d(Constantes.TAG_LOG,"Id Msg -->" + dsnap.getKey());
                        realm.beginTransaction();
                        realm.insertOrUpdate(new MensagemRealm(dsnap.getKey(),msg));
                        realm.commitTransaction();
                    }
                }
                realm.close();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(Constantes.TAG_LOG,"Erro em salvar msg no realm");
            }
        });
    }
}
