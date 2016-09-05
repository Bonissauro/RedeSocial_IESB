package br.com.bonysoft.redesocial_iesb.realm.repositorio;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.realm.modulo.RedeSocialRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


/**
 * Created by carlospanarello on 04/09/16.
 */
public class ContatoRepositorio implements IContatoRepositorio {

    private Realm carrega(Context context){
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context)
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(realmConfig);
        return Realm.getDefaultInstance();
    }

    @Override
    public Contato addContato(Context context, Contato contato, OnSaveContatoCallback callback) {

        Realm realm = carrega(context);

        realm.beginTransaction();
        Contato realmContato = realm.createObject(Contato.class);
        realmContato.setId(UUID.randomUUID().toString());

        realmContato.setId_usuario(contato.getId_usuario());
        realmContato.setNome(contato.getNome());
        realmContato.setEmail(contato.getEmail());
        realmContato.setSobreNome(contato.getSobreNome());
        realmContato.setUsuarioPrincipal(contato.isUsuarioPrincipal());
        realmContato.setCaminhoFoto(contato.getCaminhoFoto());
        realmContato.setDataNascimento(contato.getDataNascimento());
        realm.commitTransaction();

        if (callback != null) {
            callback.onSuccess();
        }

        return realmContato;
    }

    @Override
    public void deleteContatoById(Context context,String id, OnDeleteContatoCallback callback) {
        Realm realm = carrega(context);
        realm.beginTransaction();
        final RealmResults<Contato> results = realm.where(Contato.class)
                //.equalTo("id_usuario", id)
                .findAll();
        results.deleteAllFromRealm();
        realm.commitTransaction();

        if (callback != null) {
            callback.onSuccess();
        }
    }

    @Override
    public List<Contato>  getAllContatos(Context context,OnGetAllContatosCallback callback) {
        Realm realm = carrega(context);

        RealmResults<Contato> results = realm.where(Contato.class)
                .findAll();

        if (callback != null) {
            callback.onSuccess(results);
        }
        List<Contato> contatoList = new ArrayList<>();
        for(Contato item : results){
            contatoList.add(item);
        }
        return contatoList;
    }

    @Override
    public List<Contato> getAllContatosByUsuarioId(Context context, String id, OnGetAllContatosCallback callback) {

        Realm realm = carrega(context);
        RealmResults<Contato> results = realm.where(Contato.class)
                .equalTo("id_usuario",id)
                .findAll();

        if (callback != null) {
            callback.onSuccess(results);
        }

        List<Contato> contatoList = new ArrayList<>();

        for(Contato item : results){
            contatoList.add(item);
        }
        return contatoList;
    }

    @Override
    public Contato getContatoById(Context context,String id, OnGetContatoByIdCallback callback) {
        Realm realm = carrega(context);
        Contato contato = realm.where(Contato.class)
                .equalTo("id", id)
                .findFirst();

        if (callback != null) {
            callback.onSuccess(contato);
        }

        return contato;
    }
}
