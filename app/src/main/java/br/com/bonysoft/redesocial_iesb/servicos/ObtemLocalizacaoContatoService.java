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

import br.com.bonysoft.redesocial_iesb.modelo.Localizacao;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;
import io.realm.Realm;


/**
 * Created by carlospanarello on 03/10/2016.
 */

public class ObtemLocalizacaoContatoService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
                            Localizacao local =  e.next().getValue(Localizacao.class);
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

    private boolean gravarLocalizacaoRealm(Localizacao local){
        try {
            Realm realm = Realm.getDefaultInstance();
            if (local == null) {
                Log.d(Constantes.TAG_LOG, "gravarLocalizacaoRealm: local null");
                return false;
            }

            realm.beginTransaction();
            realm.copyToRealmOrUpdate(local);
            realm.commitTransaction();

            return true;
        }catch (Exception e){
            e.printStackTrace();
            Log.d(Constantes.TAG_LOG, "gravarLocalizacaoRealm: Erro na busca/update do Realm Localizacao");
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
