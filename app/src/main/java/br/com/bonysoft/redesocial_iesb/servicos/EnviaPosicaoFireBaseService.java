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

import br.com.bonysoft.redesocial_iesb.R;
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

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Constantes.SERVICO,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean (Constantes.SERVICO_ENVIO_EXEC, true);
        editor.commit();

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
        /* Estrutura do firebase
        rede-social-iesb
          localizacao
            -KT_oOkDWKyAQzzKU1Gx
                email: "carlos@gmail.com"
                latitude: -15.7571411
                longitude: -47.8779739
            -KR_oOkASKCKSezKEWIs
                email: "outro@gmail.com"
                latitude: -15.7571411
                longitude: -47.8779739
        */

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

                            Log.i(Constantes.TAG_LOG,"Incluindo um novo local -->"+ localFire);

                            objReferencia.push().setValue(localFire);

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

    @Override
    public void onDestroy() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Constantes.SERVICO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean (Constantes.SERVICO_ENVIO_EXEC, false);
        editor.commit();

        super.onDestroy();
    }
}