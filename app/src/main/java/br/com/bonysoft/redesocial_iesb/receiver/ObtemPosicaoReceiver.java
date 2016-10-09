package br.com.bonysoft.redesocial_iesb.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by panar on 03/10/2016.
 */

public class ObtemPosicaoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        /*
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

        */

    }
}
