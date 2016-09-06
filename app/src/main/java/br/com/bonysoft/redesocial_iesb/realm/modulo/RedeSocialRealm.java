package br.com.bonysoft.redesocial_iesb.realm.modulo;

import android.app.Application;
import android.provider.Settings;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by carlospanarello on 04/09/16.
 */
public class RedeSocialRealm extends Application {

    private static RedeSocialRealm instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        /*
        RealmConfiguration config = new RealmConfiguration.Builder(getApplicationContext())
                .modules(new RedeSocialRealmModule()).build();


        Realm.setDefaultConfiguration(config);
        */

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .modules(new RedeSocialRealmModule())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

    }

    public static RedeSocialRealm getInstance() {
        return instance;
    }
}
