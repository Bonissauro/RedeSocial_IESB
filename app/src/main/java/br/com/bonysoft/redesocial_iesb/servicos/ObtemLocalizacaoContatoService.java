package br.com.bonysoft.redesocial_iesb.servicos;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.modelo.LocalizacaoContatos;
import br.com.bonysoft.redesocial_iesb.modelo.LocalizacaoFireBase;
import io.realm.Realm;
import io.realm.RealmResults;

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
    public void onCreate() {
        DatabaseReference objReferencia = FirebaseDatabase.getInstance().getReference("localizacao");

        objReferencia.getRoot().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<LocalizacaoFireBase>  localFire = (List<LocalizacaoFireBase>) dataSnapshot.getValue();
                Realm realm = Realm.getDefaultInstance();

                for(LocalizacaoFireBase local : localFire){
                    realm.beginTransaction();
                    LocalizacaoContatos result = realm.where(LocalizacaoContatos.class)
                        .equalTo("email", local.email).findFirst();

                    if(result == null){
                        //gravarLocalizacao();
                        result =(LocalizacaoContatos) realm.createObject(LocalizacaoContatos.class);
                        result.setId_localizacao(UUID.randomUUID().toString());
                    }

                    result.setLatitude(local.latitude);
                    result.setLongitude(local.longitude);
                    realm.insertOrUpdate(result);

                    realm.commitTransaction();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
