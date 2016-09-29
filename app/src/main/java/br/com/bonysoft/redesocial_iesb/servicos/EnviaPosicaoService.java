package br.com.bonysoft.redesocial_iesb.servicos;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.modelo.Localizacao;
import io.realm.Realm;

/**
 * Created by carlospanarello on 28/09/16.
 */

public class EnviaPosicaoService extends Service {


    private LocationListener listener;
    private LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        Realm realm = Realm.getDefaultInstance();

        //TODO temos que colocar uma forma de gravar o usuario logado no celular
        // e buscar somente ele para enviar a posicao no firebase
        // usuarioPrincipal .equalTo("usuarioPrincipal",true)
        Contato contatoResultado = realm.where(Contato.class)
                .equalTo("email","bonissauro@gmail.com")
                .findFirst();

        String id = "KQmhU9QQRJ6nO2Wb9qr";

        listener = new LocalizacaoListenner(id,contatoResultado.getEmail());

        realm.close();



        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000*60*5,0,listener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }

    class LocalizacaoListenner implements LocationListener{

        String mEmail;
        String mId;
        public LocalizacaoListenner(String id,String email){
            mEmail = email;
            mId = id;

        }

        @Override
        public void onLocationChanged(Location location) {
            // Realiza a gravacao no fireBase do resultado da latitude e longitude
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference objReferencia = database.getReference("localizacao");
            objReferencia.child(mId).child("email").setValue(mEmail);
            objReferencia.child(mId).child("longitude").setValue(location.getLongitude());
            objReferencia.child(mId).child("latitude").setValue(location.getLatitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }



    /*

private GoogleApiClient mGoogleApiClient;
    // Obtem os dados do usuario
    Realm realm = Realm.getDefaultInstance();

    //TODO temos que colocar uma forma de gravar o usuario logado no celular
    // e buscar somente ele para enviar a posicao no firebase
    // usuarioPrincipal .equalTo("usuarioPrincipal",true)
    Contato contatoResultado = realm.where(Contato.class)
            .equalTo("email","bonissauro@gmail.com")
            .findFirst();

    //







    realm.close();
    */

}


