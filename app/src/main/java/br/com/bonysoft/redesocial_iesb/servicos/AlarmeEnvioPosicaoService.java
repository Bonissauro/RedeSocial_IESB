package br.com.bonysoft.redesocial_iesb.servicos;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import br.com.bonysoft.redesocial_iesb.receiver.ObtemPosicaoReceiver;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;

/**
 * Created by panar on 03/10/2016.
 */

public class AlarmeEnvioPosicaoService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(Constantes.TAG_LOG,"CRIANDO ALARME");
        int seconds = 5;
        long doisMinutos = 120000;

        Intent intent = new Intent(this, ObtemPosicaoReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (seconds * 1000),doisMinutos, pendingIntent);
        Log.i(Constantes.TAG_LOG,"ALARME CONFIGURADO");
    }
}
