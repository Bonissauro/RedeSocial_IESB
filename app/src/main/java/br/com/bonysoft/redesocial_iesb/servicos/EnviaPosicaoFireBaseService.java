package br.com.bonysoft.redesocial_iesb.servicos;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        ContatoRepositorio repo = new ContatoRepositorio();
        String email = repo.buscaEmailUsuarioLogado();
        Log.i(Constantes.TAG_LOG,"Email de consulta de usuario-->"+ email);
        if(!email.isEmpty()){
            gravaDadosFirebase(email,localizacao, FirebaseDatabase.getInstance().getReference("localizacao"));
        }
        repo.close();
    }

    private void gravaDadosFirebase(final String email, final LatLng location, final DatabaseReference objReferencia){
        objReferencia.child("email").equalTo(email).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        LocalizacaoFireBase local = dataSnapshot.getValue(LocalizacaoFireBase.class);
                        Log.i(Constantes.TAG_LOG,"Resultado do firebase-->"+ local);
                        if(local == null){
                            Log.i(Constantes.TAG_LOG,"Inserindo Novo");

                            String mId=objReferencia.child("localizacao").push().getKey();

                            //gravarLocalizacao(mId,email,location,objReferencia);

                            Log.i(Constantes.TAG_LOG,"Id-->"+mId);

                            objReferencia.child(mId).child("email").setValue(email);
                            objReferencia.child(mId).child("longitude").setValue(location.longitude);
                            objReferencia.child(mId).child("latitude").setValue(location.latitude);
                        } else {
                            Log.i(Constantes.TAG_LOG,"Update-->"+local.toString());
                            String chave = dataSnapshot.getKey();
                            Log.i(Constantes.TAG_LOG,"Id-->"+chave);

                            objReferencia.child(chave).child("longitude").setValue(location.longitude);
                            objReferencia.child(chave).child("latitude").setValue(location.latitude);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }

        );
    }

    //TODO nao sei se ta certo isso aqui
    private void gravarLocalizacao(final String uid, final String email,
                                   final LatLng location, final DatabaseReference objReferencia) {
        String key = objReferencia.child("posts").push().getKey();

        LocalizacaoFireBase local = new LocalizacaoFireBase(uid,email,  location.latitude +"" ,  location.longitude +"");
        Map<String, Object> localValues = local.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/localizacao/" + key, localValues);


        objReferencia.updateChildren(childUpdates);
    }

}
