package br.com.bonysoft.redesocial_iesb.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import br.com.bonysoft.redesocial_iesb.servicos.EnviaPosicaoFireBaseService;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;

/**
 * Created by panar on 03/10/2016.
 */

public class ObtemPosicaoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constantes.TAG_LOG,"onReceive ObtemPosicaoReceiver");
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            Log.i(Constantes.TAG_LOG,"onReceive ObtemPosicaoReceiver Possui Permissao de LocalizacaoContatos");

            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            LatLng loc = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

            Intent startServiceIntent = new Intent(context, EnviaPosicaoFireBaseService.class);

            startServiceIntent.putExtra(Constantes.ENVIO_POSICAO, loc);
            context.startService(startServiceIntent);


        }
    }
}
