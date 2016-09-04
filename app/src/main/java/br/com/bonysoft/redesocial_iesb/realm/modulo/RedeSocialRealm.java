package br.com.bonysoft.redesocial_iesb.realm.modulo;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by carlospanarello on 04/09/16.
 */
public class RedeSocialRealm extends Application {

    private static RealmConfiguration instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance  = new RealmConfiguration.Builder(getApplicationContext())
                .modules(new RedeSocialRealmModule()).deleteRealmIfMigrationNeeded().build();
    }

    public static RealmConfiguration getInstance() {
        return instance;
    }
}
