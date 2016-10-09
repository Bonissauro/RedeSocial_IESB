package br.com.bonysoft.redesocial_iesb.servicos;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import br.com.bonysoft.redesocial_iesb.modelo.LocalizacaoContatos;
import br.com.bonysoft.redesocial_iesb.modelo.LocalizacaoFireBase;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;
import io.realm.Realm;


/**
 * Created by panar on 03/10/2016.
 */

public class ObtemLocalizacaoContatoService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Constantes.SERVICO,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean (Constantes.SERVICO_REC_EXEC, true);
        editor.commit();

        buscarLocalizacaoGPSContatos(FirebaseDatabase.getInstance().getReference("localizacao"));
        return super.onStartCommand(intent, flags, startId);
    }

    private void buscarLocalizacaoGPSContatos(final DatabaseReference objReferencia){
        objReferencia.orderByChild("email").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i(Constantes.TAG_LOG, "ObtemLocalizacaoContatoService Resultado -->" + dataSnapshot);

                        Iterator<DataSnapshot> e = dataSnapshot.getChildren().iterator();

                        while (e.hasNext()){
                            LocalizacaoFireBase local =  e.next().getValue(LocalizacaoFireBase.class);
                            if(local != null){
                                if(gravarLocalizacaoRealm(local)){
                                    Log.i(Constantes.TAG_LOG,"ObtemLocalizacaoContatoService Item salvo com sucesso -->"+ local);
                                } else {
                                    Log.i(Constantes.TAG_LOG,"ObtemLocalizacaoContatoService Erro ao salvar -->"+ local);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i(Constantes.TAG_LOG, "ObtemLocalizacaoContatoService Erro dataBase Consulta -->" + databaseError.getMessage());
                        Log.i(Constantes.TAG_LOG, "ObtemLocalizacaoContatoService Erro dataBase Consulta -->" + databaseError.getDetails());
                    }
                }
        );
    }

    private boolean gravarLocalizacaoRealm(LocalizacaoFireBase local){
        try {
            Realm realm = Realm.getDefaultInstance();
            if (local == null) {
                Log.d(Constantes.TAG_LOG,"ObtemLocalizacaoContatoService gravarLocalizacaoRealm - local fire = null");
                return false;
            }
            realm.beginTransaction();

            LocalizacaoContatos result = realm.where(LocalizacaoContatos.class)
                    .equalTo("email", local.email)
                    .findFirst();

            if (result == null) {
                Log.d(Constantes.TAG_LOG,"ObtemLocalizacaoContatoService Nova Localizacao Amigo");
                result = realm.createObject(LocalizacaoContatos.class);
                result.setEmail(local.email);
            } else {
                Log.d(Constantes.TAG_LOG,"ObtemLocalizacaoContatoService Update Localizacao Amigo");
            }
            result.setLongitude(local.longitude.toString());
            result.setLatitude(local.latitude.toString());

            realm.insertOrUpdate(result);

            realm.commitTransaction();

            return true;
        }catch (Exception e){
            e.printStackTrace();
            Log.d(Constantes.TAG_LOG,"ObtemLocalizacaoContatoService Erro na busca/update do Realm Localizacao");
            return false;
        }
    }


    @Override
    public void onDestroy() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Constantes.SERVICO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean (Constantes.SERVICO_REC_EXEC, false);
        editor.commit();

        super.onDestroy();
    }
}
