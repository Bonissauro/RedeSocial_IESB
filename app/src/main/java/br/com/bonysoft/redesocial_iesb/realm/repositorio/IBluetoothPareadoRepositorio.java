package br.com.bonysoft.redesocial_iesb.realm.repositorio;

import java.util.List;

import br.com.bonysoft.redesocial_iesb.modelo.BluetoothPareado;
import io.realm.RealmResults;

/**
 * Created by Boni Machado on 17/09/16.
 */
public interface IBluetoothPareadoRepositorio {

    interface OnSaveCallback {
        void onSuccess(BluetoothPareado contato);
        void onError(String message);
    }

    interface OnDeleteCallback {
        void onSuccess();
        void onError(String message);
    }

    interface OnGetByIdCallback {

        void onSuccess(BluetoothPareado contato);
        void onError(String message);
    }

    interface OnGetAllCallback {
        void onSuccess(RealmResults<BluetoothPareado> listaContatos);
        void onError(String message);
    }

    BluetoothPareado add(BluetoothPareado objeto, OnSaveCallback callback);

    BluetoothPareado getById(String id, OnGetByIdCallback callback);

    void edit(BluetoothPareado objeto, OnSaveCallback callback);

    void deleteById(String id, OnDeleteCallback callback);

    void deleteAll(OnDeleteCallback callback);

    List<BluetoothPareado> getAll(OnGetAllCallback callback);

}
