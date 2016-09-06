package br.com.bonysoft.redesocial_iesb.realm.repositorio;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by carlospanarello on 04/09/16.
 */
public class ContatoRepositorio implements IContatoRepositorio {

    @Override
    public Contato addContato(Contato contato, OnSaveContatoCallback callback) {

        Realm realm =  Realm.getDefaultInstance();

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
    public void editContato(Contato contato, OnSaveContatoCallback callback) {

        Realm realm =  Realm.getDefaultInstance();

        realm.beginTransaction();
        realm.insertOrUpdate(contato);
        realm.commitTransaction();

        if (callback != null) {
            callback.onSuccess();
        }

        return;
    }

    @Override
    public void deleteContatoById(String id, OnDeleteContatoCallback callback) {
        Realm realm =  Realm.getDefaultInstance();

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
    public List<Contato>  getAllContatos(OnGetAllContatosCallback callback) {

        Realm realm =  Realm.getDefaultInstance();

        RealmResults<Contato> results = realm.where(Contato.class)
                .findAll().sort("nome");

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
    public List<Contato> getAllContatosByUsuarioId(String id, OnGetAllContatosCallback callback) {

        Realm realm =  Realm.getDefaultInstance();

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
    public Contato getContatoById(String id, OnGetContatoByIdCallback callback) {
        Realm realm =  Realm.getDefaultInstance();

        Contato contato = realm.where(Contato.class)
                .equalTo("id", id)
                .findFirst();

        if (callback != null) {
            callback.onSuccess(contato);
        }

        return contato;
    }
}
