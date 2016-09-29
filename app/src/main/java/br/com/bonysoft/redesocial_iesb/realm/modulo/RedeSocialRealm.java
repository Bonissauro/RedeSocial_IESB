package br.com.bonysoft.redesocial_iesb.realm.modulo;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.facebook.drawee.backends.pipeline.Fresco;

import br.com.bonysoft.redesocial_iesb.receiver.LocalizacaoReceiver;
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

        //TODO ver se ta certo esse metodo de alarme
        //agendarAlarme()

    }

    private boolean possuiPermissao(){
        if(Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
         && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            //pedir permissao

            /*
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);
            */
        }

        return false;
    }

    public void agendarAlarme() {
        Intent intent = new Intent(getApplicationContext(), LocalizacaoReceiver.class);

        final PendingIntent pIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // 1s is only for testing
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 1000, pIntent);
    }

    public static RedeSocialRealm getInstance() {
        return instance;
    }
}
