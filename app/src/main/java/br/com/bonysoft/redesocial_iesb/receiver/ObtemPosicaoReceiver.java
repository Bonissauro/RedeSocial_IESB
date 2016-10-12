package br.com.bonysoft.redesocial_iesb.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import br.com.bonysoft.redesocial_iesb.servicos.EnviaPosicaoFireBaseService;
import br.com.bonysoft.redesocial_iesb.servicos.ObtemLocalizacaoContatoService;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;

/**
 * Created by panar on 03/10/2016.
 */

public class ObtemPosicaoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constantes.TAG_LOG,"onReceive:  ObtemPosicaoReceiver");
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            Log.i(Constantes.TAG_LOG,"onReceive:  ObtemPosicaoReceiver Possui Permissao de Localizacao");

            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            LatLng loc = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

            Log.i(Constantes.TAG_LOG,"onReceive: Localizacao Long--> " + loc.longitude + " Lat--> " + loc.latitude);

            SharedPreferences sharedPref = context.getSharedPreferences(Constantes.SERVICO,Context.MODE_PRIVATE);
            boolean envio = sharedPref.getBoolean(Constantes.SERVICO_ENVIO_EXEC,true);
            boolean rec = sharedPref.getBoolean(Constantes.SERVICO_REC_EXEC,true);
            Log.i(Constantes.TAG_LOG, "onReceive: Servico Envio->" + envio + " Servico de Receber-->" + rec);
            //Servico de Envio de Posicao do Usuario
            if(envio) {
                Intent startServiceIntent = new Intent(context, EnviaPosicaoFireBaseService.class);
                startServiceIntent.putExtra(Constantes.ENVIO_POSICAO, loc);
                context.startService(startServiceIntent);
            }
            //Servico de Recebimento de Posicao dos Contatos
            if(rec) {
                Intent startServiceRecebimento = new Intent(context, ObtemLocalizacaoContatoService.class);
                context.startService(startServiceRecebimento);
            }
        }
    }
}
