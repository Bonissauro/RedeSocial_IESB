package br.com.bonysoft.redesocial_iesb.realm.repositorio;

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

    interface OnGetContato {

        void onSuccess(Contato contato);
        void onError(String message);
    }

    interface OnGetContatoLogin {

        void onSuccess(MensagemLogin message,Contato contato);
        void onError(MensagemLogin message);
    }

    interface OnGetAllContatosCallback {
        void onSuccess(RealmResults<Contato> listaContatos);
        void onError(String message);
    }

    Contato addContato(Contato contato, OnSaveContatoCallback callback);

    String buscaEmailUsuarioLogado();

    boolean validarUsuarioFacebook(Contato contato, OnGetContatoLogin callback);

    boolean validarUsuarioSenha(String email, String senha, OnGetContatoLogin callback);


    Contato getContatosByEmail(String email, OnGetContato callback);

    Contato addContatoPeloIdFacebookOuEmail(Contato contato, OnSaveContatoCallback callback);

    void editContato(Contato contato, OnSaveContatoCallback callback);

    void deleteContatoById(String id, OnDeleteContatoCallback callback);

    List<Contato> getAllContatos( OnGetAllContatosCallback callback);

    List<Contato> getAllContatosByUsuarioId(String id, OnGetAllContatosCallback callback);

    Contato getContatoById(String id, OnGetContato callback);

    void close();

}
