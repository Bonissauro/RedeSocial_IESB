package br.com.bonysoft.redesocial_iesb;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

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

    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }
}
