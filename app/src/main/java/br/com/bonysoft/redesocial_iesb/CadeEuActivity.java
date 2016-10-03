package br.com.bonysoft.redesocial_iesb;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import br.com.bonysoft.redesocial_iesb.servicos.EnviaPosicaoService;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;

public class CadeEuActivity extends FragmentActivity
        implements
            OnMapReadyCallback,
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener,
            LocationListener
{

    String TAG_LOG = Constantes.TAG_LOG;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private LatLng latLngAtual;
    private MarkerOptions marcadorDaPosicao;
    private MarkerOptions marcadorDaPosicaoAtualDoUsuario;

    private Marker marcador;

    private int quantos=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cade_eu);

        Intent startServiceIntent = new Intent(getApplicationContext(), EnviaPosicaoService.class);
        getApplicationContext().startService(startServiceIntent);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.i(TAG_LOG, "Location services connected.");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {

        } else {

            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
            CameraUpdate zoom   = CameraUpdateFactory.zoomTo(80);

            mMap.moveCamera(center);
            mMap.animateCamera(zoom);

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            handleNewLocation(location);

        }

    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {

        Log.d(TAG_LOG, location.toString());

        double currentLatitude  = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        if (marcadorDaPosicao==null) {

            marcadorDaPosicao = new MarkerOptions()
                    .position(latLng)
                    .title("Tô aqui! "+(quantos++));

            mMap.addMarker(marcadorDaPosicao);

        }else{

            marcadorDaPosicao.title("Tô aqui de novo! "+(quantos++));
            marcadorDaPosicao.position(latLng);

        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG_LOG, "Location services disconnected.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG_LOG, "Location services failed.");
    }

    private void marcarLocalizacaoUsuario(Location location) {

        double currentLatitude  = location.getLatitude();
        double currentLongitude = location.getLongitude();

        latLngAtual = new LatLng(currentLatitude, currentLongitude);

        CameraUpdate center = CameraUpdateFactory.newLatLng(latLngAtual);

        if (marcadorDaPosicaoAtualDoUsuario == null) {
/*
            LocalDescarteServicos servico = new LocalDescarteServicos();

            servico.listarTodosRegistros(new ILocalDescarteServicos.OnListarTodosRegistrosCallback(){

                @Override
                public void onSuccess(RealmResults<LocalDescarte> lista) {

                    for (LocalDescarte localDescarte:lista){

                        LatLng ll = new LatLng(localDescarte.getLatitude(), localDescarte.getLongitude());

                        mMap.addMarker(new MarkerOptions()
                                .position(ll)
                                .title(localDescarte.getNome())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

                    }

                }

                @Override
                public void onError(String message) {

                }

            });

*/
            marcadorDaPosicaoAtualDoUsuario = new MarkerOptions()
                    .position(latLngAtual)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title("Você");

            marcador = mMap.addMarker(marcadorDaPosicaoAtualDoUsuario);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngAtual));


        }else{
/*
            String lat1 = Ferramentas.arredondarCoordenada(marcadorDaPosicaoAtualDoUsuario.getPosition().latitude);
            String lat2 = Ferramentas.arredondarCoordenada(latLngAtual.latitude);

            String long1 = Ferramentas.arredondarCoordenada(marcadorDaPosicaoAtualDoUsuario.getPosition().longitude);
            String long2 = Ferramentas.arredondarCoordenada(latLngAtual.longitude);

            if ((!lat1.equalsIgnoreCase(lat2))||(!long1.equalsIgnoreCase(long2))){

                marcador.remove();

                marcadorDaPosicaoAtualDoUsuario = new MarkerOptions()
                        .position(latLngAtual)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .title("Você");

                marcador = mMap.addMarker(marcadorDaPosicaoAtualDoUsuario);

                LocalDescarte objeto = new LocalDescarte(currentLatitude, currentLongitude, "Ponto cadastrado automaticamente!");

                new LocalDescarteServicos().gravar(objeto, new LocalDescarteServicos.OnGravarCallback() {

                    @Override
                    public void onSuccess(LocalDescarte objeto) {

                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(getBaseContext(), "Erro ==> " + message, Toast.LENGTH_LONG).show();
                    }

                });

            }
*/
        }

    }

}
