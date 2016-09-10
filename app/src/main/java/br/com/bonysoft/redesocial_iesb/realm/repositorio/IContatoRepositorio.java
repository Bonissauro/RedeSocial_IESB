package br.com.bonysoft.redesocial_iesb.realm.repositorio;

import android.content.Context;

import java.util.List;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import io.realm.RealmResults;

/**
 * Created by carlospanarello on 04/09/16.
 */
public interface IContatoRepositorio {

    interface OnSaveContatoCallback {
        void onSuccess(Contato contato);
        void onError(String message);
    }

    interface OnDeleteContatoCallback {
        void onSuccess();
        void onError(String message);
    }

    interface OnGetContatoByIdCallback {

        void onSuccess(Contato contato);
        void onError(String message);
    }

    interface OnGetAllContatosCallback {
        void onSuccess(RealmResults<Contato> listaContatos);
        void onError(String message);
    }

    Contato addContato(Contato contato, OnSaveContatoCallback callback);

    Contato getContatosByEmail(String email, OnGetContatoByIdCallback callback);

    void editContato(Contato contato, OnSaveContatoCallback callback);

    void deleteContatoById(String id, OnDeleteContatoCallback callback);

    List<Contato> getAllContatos( OnGetAllContatosCallback callback);

    List<Contato> getAllContatosByUsuarioId(String id, OnGetAllContatosCallback callback);

    Contato getContatoById(String id, OnGetContatoByIdCallback callback);

}
