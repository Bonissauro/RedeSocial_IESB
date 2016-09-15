package br.com.bonysoft.redesocial_iesb.realm.modulo;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import io.realm.Realm;
import io.realm.RealmConfiguration;

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
