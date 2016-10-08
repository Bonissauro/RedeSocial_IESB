package br.com.bonysoft.redesocial_iesb.servicos;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import br.com.bonysoft.redesocial_iesb.modelo.LocalizacaoContatos;
import br.com.bonysoft.redesocial_iesb.modelo.LocalizacaoFireBase;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.ContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;

/**
 * Created by panar on 03/10/2016.
 */

public class EnviaPosicaoFireBaseService extends Service {

    private LatLng localizacao;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        localizacao = intent.getParcelableExtra(Constantes.ENVIO_POSICAO);
        Log.i(Constantes.TAG_LOG,"OnStartCommand-->" + localizacao);

        ContatoRepositorio repo = new ContatoRepositorio();
        String email = repo.buscaEmailUsuarioLogado();

        Log.i(Constantes.TAG_LOG,"Email de consulta de usuario-->"+ email);
        if(!email.isEmpty()){
            Log.i(Constantes.TAG_LOG,"Entrou para chamar gravacao");
            gravaDadosFirebase(email,localizacao, FirebaseDatabase.getInstance().getReference("localizacao"));
        }
        repo.close();

        return super.onStartCommand(intent, flags, startId);
    }

    private void gravaDadosFirebase(final String email, final LatLng location, final DatabaseReference objReferencia){
        Log.i(Constantes.TAG_LOG,"gravaDadosFirebase");
        Log.i(Constantes.TAG_LOG,"gravaDadosFirebase-->" + localizacao);

        objReferencia.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i(Constantes.TAG_LOG, "Resultado do firebase -->" + dataSnapshot);
                        Log.i(Constantes.TAG_LOG,"onDataChange-->" + localizacao);

                        LocalizacaoFireBase local = null;
                        String chave = "";
                        DataSnapshot dataSnapshotFilho = null;
                        if(dataSnapshot.getChildren().iterator().hasNext()) {
                            dataSnapshotFilho =  dataSnapshot.getChildren().iterator().next();

                            local = dataSnapshotFilho.getValue(LocalizacaoFireBase.class);
                            chave = dataSnapshotFilho.getKey();
                        }

                        Log.i(Constantes.TAG_LOG,"Resultado do firebase convertido -->"+ local);
                        if(local == null){
                            Log.i(Constantes.TAG_LOG,"Inserindo Novo");
                            LocalizacaoFireBase localFire =
                                    new LocalizacaoFireBase(email,location.latitude,location.longitude);

                            //O que tem de errado nisso?
                           //objReferencia.push().setValue(localFire);

                            Log.i(Constantes.TAG_LOG,"Incluindo um novo local -->"+ localFire);

                            objReferencia.push().setValue(localFire);
                            /*
                            String mId=objReferencia.child("localizacao").push().getKey();
                            //Log.d("SERVICE","Local -->" + location.getLongitude() + "-" + location.getLatitude());
                            //47.83906625--15.6543783

                            objReferencia.child(mId).child("email").setValue(localFire.email);
                            objReferencia.child(mId).child("longitude").setValue(localFire.longitude);
                            objReferencia.child(mId).child("latitude").setValue(localFire.latitude);
                            */
                        } else {

                            Log.i(Constantes.TAG_LOG,"Update-->"+local.toString());
                            Log.i(Constantes.TAG_LOG,"Id-->"+chave);

                            if(!chave.isEmpty() && location != null) {
                                objReferencia.child(chave).child("longitude").setValue(location.longitude);
                                objReferencia.child(chave).child("latitude").setValue(location.latitude);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i(Constantes.TAG_LOG, "Erro dataBase -->" + databaseError.getMessage());
                        Log.i(Constantes.TAG_LOG, "Erro dataBase -->" + databaseError.getDetails());
                    }
                }
        );
    }

    public void testeFirebase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference objReferencia = database.getReference("localizacao");

        //String mId = "-KQmhU9QQRJ6nO2Wb9qr";
        String mId=objReferencia.child("localizacao").push().getKey();
        //Log.d("SERVICE","Local -->" + location.getLongitude() + "-" + location.getLatitude());
        //47.83906625--15.6543783

        objReferencia.child(mId).child("email").setValue("bonissauro@gmail.com");
        objReferencia.child(mId).child("longitude").setValue("47.83906625");
        objReferencia.child(mId).child("latitude").setValue("15.6543783");
    }

    //TODO nao sei se ta certo isso aqui
    private void gravarLocalizacao(final String uid, final String email,
                                   final LatLng location, final DatabaseReference objReferencia) {
        String key = objReferencia.child("posts").push().getKey();

        LocalizacaoFireBase local = new LocalizacaoFireBase(email,  location.latitude +"" ,  location.longitude +"");
        Map<String, Object> localValues = local.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/localizacao/" + key, localValues);


        objReferencia.updateChildren(childUpdates);
    }

    @Override
    public void onDestroy() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("servico_executando", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean ("servico_executando", false);
        editor.commit();

        super.onDestroy();
    }
}
