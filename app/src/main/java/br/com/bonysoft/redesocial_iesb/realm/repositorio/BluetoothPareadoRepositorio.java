package br.com.bonysoft.redesocial_iesb.realm.repositorio;

import java.util.ArrayList;
import java.util.List;

import br.com.bonysoft.redesocial_iesb.modelo.BluetoothPareado;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Boni Machado on 17/09/16.
 */
public class BluetoothPareadoRepositorio implements IBluetoothPareadoRepositorio {

    @Override
    public BluetoothPareado add(BluetoothPareado objeto, OnSaveCallback callback) {

        Realm realm =  Realm.getDefaultInstance();

        realm.beginTransaction();

        BluetoothPareado realmContato = realm.createObject(BluetoothPareado.class);
        realmContato.setEndereco(objeto.getEndereco());
        realmContato.setNome(objeto.getNome());

        realm.commitTransaction();

        if (callback != null) {
            callback.onSuccess(objeto);
        }

        return realmContato;
    }

    @Override
    public void edit(BluetoothPareado objeto, OnSaveCallback callback) {

        Realm realm =  Realm.getDefaultInstance();

        realm.beginTransaction();
        realm.insertOrUpdate(objeto);
        realm.commitTransaction();

        if (callback != null) {
            callback.onSuccess(objeto);
        }

        return;
    }

    @Override
    public void deleteById(String id, OnDeleteCallback callback) {

        Realm realm =  Realm.getDefaultInstance();

        realm.beginTransaction();
        final RealmResults<BluetoothPareado> results = realm.where(BluetoothPareado.class).equalTo("endereco", id).findAll();
        results.deleteAllFromRealm();
        realm.commitTransaction();

        if (callback != null) {
            callback.onSuccess();
        }
    }

    @Override
    public void deleteAll(OnDeleteCallback callback) {

        Realm realm =  Realm.getDefaultInstance();

        realm.beginTransaction();
        final RealmResults<BluetoothPareado> results = realm.where(BluetoothPareado.class).findAll();
        results.deleteAllFromRealm();
        realm.commitTransaction();

        if (callback != null) {
            callback.onSuccess();
        }

    }

    @Override
    public List<BluetoothPareado> getAll(OnGetAllCallback callback) {

        Realm realm =  Realm.getDefaultInstance();

        RealmResults<BluetoothPareado> results = realm.where(BluetoothPareado.class).findAll().sort("nome");

        if (callback != null) {
            callback.onSuccess(results);
        }

        List<BluetoothPareado> lista = new ArrayList<>();
        for(BluetoothPareado item : results){
            lista.add(item);
        }

        return lista;

    }

    @Override
    public BluetoothPareado getById(String id, OnGetByIdCallback callback) {
        //TODO isso aqui da pau, nao tem essa propriedade id na classe
        Realm realm =  Realm.getDefaultInstance();

        BluetoothPareado objeto = realm.where(BluetoothPareado.class)
                .equalTo("id", id)
                .findFirst();

        if (callback != null) {
            callback.onSuccess(objeto);
        }

        return objeto;

    }

}
