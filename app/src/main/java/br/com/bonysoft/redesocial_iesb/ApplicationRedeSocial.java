package br.com.bonysoft.redesocial_iesb;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;

import br.com.bonysoft.redesocial_iesb.modelo.Usuario;
import br.com.bonysoft.redesocial_iesb.realm.modulo.RedeSocialRealmModule;
import br.com.bonysoft.redesocial_iesb.servicos.AlarmeEnvioPosicaoService;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;
import io.realm.Realm;
import io.realm.RealmConfiguration;


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
}
