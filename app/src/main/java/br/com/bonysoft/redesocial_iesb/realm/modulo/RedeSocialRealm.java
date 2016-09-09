package br.com.bonysoft.redesocial_iesb.realm.modulo;

import android.app.Application;
import android.graphics.drawable.Icon;
import android.provider.Settings;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.ContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.IContatoRepositorio;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by carlospanarello on 04/09/16.
 */
public class RedeSocialRealm extends Application {

    private static RedeSocialRealm instance;

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
    }

    public static RedeSocialRealm getInstance() {
        return instance;
    }
}
