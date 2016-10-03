package br.com.bonysoft.redesocial_iesb.realm.repositorio;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by carlospanarello on 04/09/16.
 */
public class ContatoRepositorio implements IContatoRepositorio {

    String TAG_LOG = Constantes.TAG_LOG;
    //TODO colocar o getDefaultInstace no construtor para poder realizar o close.

    @Override
    public Contato addContato(Contato contato, OnSaveContatoCallback callback) {
        Realm realm = Realm.getDefaultInstance();
        if(contato == null){
            if(callback != null){
                callback.equals("Favor informar um contato!");
            }
        }

        realm.beginTransaction();

        Contato result = realm.where(Contato.class)
                .equalTo("email",contato.getEmail())
                .findFirst();

        if(result == null){
            result = realm.createObject(Contato.class);

            if(contato.getId()==null || contato.getId().trim().isEmpty()) {
                result.setId(UUID.randomUUID().toString());
            }else {
                result.setId(contato.getId());
            }
        }

        result.setCaminhoFoto(contato.getCaminhoFoto());
        result.setEndereco(contato.getEndereco());
        result.setEmail(contato.getEmail());
        result.setSenha(contato.getSenha());
        result.setDataNascimento(contato.getDataNascimento());
        result.setIdFacebook(contato.getIdFacebook());
        result.setNome(contato.getNome());
        result.setNomeSkype(contato.getNomeSkype());
        result.setSobreNome(contato.getSobreNome());
        result.setTelefone(contato.getTelefone());

        realm.insertOrUpdate(result);

        realm.commitTransaction();

        if (callback != null) {
            callback.onSuccess(contato);
        }

        return result;
    }

    @Override
    public Contato addContatoPeloIdFacebookOuEmail(Contato contato, OnSaveContatoCallback callback) {

        if(contato == null){
            if(callback != null){
                callback.equals("Favor informar um contato!");
            }
        }
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        Contato result = null;

        if(contato.getEmail() != null && !contato.getEmail().trim().isEmpty()) {
            result = realm.where(Contato.class)
                    .equalTo("email", contato.getEmail())
                    .findFirst();
        }

        if(result == null){
            result = realm.where(Contato.class)
                    .equalTo("idFacebook",contato.getIdFacebook())
                    .findFirst();
        }

        if(result == null){
            result = realm.createObject(Contato.class);
            result.setId(UUID.randomUUID().toString());
        }

        result.setSenha(contato.getSenha());
        result.setCaminhoFoto(contato.getCaminhoFoto());
        result.setEmail(contato.getEmail());
        result.setDataNascimento(contato.getDataNascimento());
        result.setEndereco(contato.getEndereco());
        result.setIdFacebook(contato.getIdFacebook());
        result.setNome(contato.getNome());
        result.setNomeSkype(contato.getNomeSkype());
        result.setSobreNome(contato.getSobreNome());
        result.setTelefone(contato.getTelefone());

        realm.insertOrUpdate(result);

        realm.commitTransaction();
        //Colocar no final para nao criar transacoes aninhadas
        if (callback != null) {
            callback.onSuccess(contato);
        }

        return result;
    }

    @Override
    public void editContato(Contato contato, OnSaveContatoCallback callback) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insertOrUpdate(contato);
        realm.commitTransaction();

        if (callback != null) {
            callback.onSuccess(contato);
        }

        return;
    }

    @Override
    public void deleteContatoById(String id, OnDeleteContatoCallback callback) {
        Realm realm = Realm.getDefaultInstance();
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
    public List<Contato> getAllContatos(OnGetAllContatosCallback callback) {
        Realm realm = Realm.getDefaultInstance();
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
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Contato> results = realm.where(Contato.class)
                .equalTo("id",id)
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

    private boolean verificaSeExisteJaUsuarioCadastrado(){
        Realm realm = Realm.getDefaultInstance();
        Contato usuario = realm.where(Contato.class).isNotEmpty("senha").isNotNull("senha").findFirst();
        if(usuario != null){
            return true;
        }
        return false;
    }



    private boolean validarLogin(Contato contato,boolean gravarSenha, OnGetContatoLogin callback){
        Realm realm = Realm.getDefaultInstance();
        if(contato == null){
            callback.onError(MensagemLogin.INFORME_EMAIL_SENHA);
            return false;
        }

        if(contato.getEmail() == null || contato.getEmail().trim().isEmpty()){
            callback.onError(MensagemLogin.INFORME_EMAIL);
            return false;
        }

        Contato result = realm.where(Contato.class)
                .equalTo("email",contato.getEmail())
                .findFirst();
        Log.i(TAG_LOG,"Resultado-->" +result);
        if(result != null ){

        }
        //Achou alguem com o mesmo email
        if(result != null ){

            if(result.getSenha() == null || result.getSenha().isEmpty()){
                //Verifica se ja exise alguem com senha
                if(verificaSeExisteJaUsuarioCadastrado()){
                    callback.onError(MensagemLogin.JA_EXISTE);

                    return false;
                } else {
                    //Nao existe ninguem cadastrado como usuario principal
                    callback.onSuccess(MensagemLogin.CADASTRAR,result);
                    return true;
                }

            } else {

                if(result.getSenha().equals(contato.getSenha())){
                    callback.onSuccess(MensagemLogin.LOGIN_COM_SUCESSO,result);
                    return true;
                } else {
                    if(contato.getSenha() == null || contato.getSenha().trim().isEmpty()){
                        callback.onError(MensagemLogin.INFORME_SENHA);
                        return false;
                    }

                    //TODO limitar o tamanho da senha para isso dar certo
                    if(result.getSenha().length()>8){
                        callback.onError(MensagemLogin.USOU_FACE);
                        return false;
                    } else {
                        callback.onError(MensagemLogin.SENHA_INVALIDA);
                        return false;
                    }
                }
            }
        } else {
            //Cara nao existe vou fazer o cadastro somente se nao existir mais ninguem com senha
            //Como ele veio do face vou usar todos os dados ja obtidos para ja realizar o cadastro
            if(verificaSeExisteJaUsuarioCadastrado()){
                callback.onError(MensagemLogin.JA_EXISTE);
                return false;
            }

            realm.beginTransaction();

            result = realm.createObject(Contato.class);
            result.setId(UUID.randomUUID().toString());

            //Aqui ele so salva a senha se vier pelo facebook
            //a senha do sera o id do face
            Log.i("LoginActivity","GravarSenha-->" + gravarSenha);
            if(gravarSenha) {
                result.setSenha(contato.getSenha());
            }

            result.setCaminhoFoto(contato.getCaminhoFoto());
            result.setEmail(contato.getEmail());
            result.setDataNascimento(contato.getDataNascimento());
            result.setEndereco(contato.getEndereco());
            result.setIdFacebook(contato.getIdFacebook());
            result.setNome(contato.getNome());
            result.setNomeSkype(contato.getNomeSkype());
            result.setSobreNome(contato.getSobreNome());
            result.setTelefone(contato.getTelefone());

            realm.insertOrUpdate(result);

            realm.commitTransaction();

            callback.onSuccess(MensagemLogin.CADASTRAR_COM_SUCESSO,result);
            return true;
        }

    }

    @Override
    public boolean validarUsuarioFacebook(Contato contato, OnGetContatoLogin callback){
        return validarLogin(contato,true,callback);
    }

    @Override
    public boolean validarUsuarioSenha(String email, String senha, OnGetContatoLogin callback){

        Contato contato = new Contato();
        contato.setSenha(senha);
        contato.setEmail(email);

        return validarLogin(contato,false,callback);
    }

    @Override
    public Contato getContatosByEmail(String email, OnGetContato callback) {
        Realm realm = Realm.getDefaultInstance();
        Contato result = realm.where(Contato.class)
                .equalTo("email",email)
                .findFirst();

        if (callback != null) {
            callback.onSuccess(result);
        }

        return result;
    }

    @Override
    public Contato getContatoById(String id, OnGetContato callback) {
        Realm realm = Realm.getDefaultInstance();
        Contato contato = realm.where(Contato.class)
                .equalTo("id", id)
                .findFirst();

        if (callback != null) {
            callback.onSuccess(contato);
        }

        return contato;
    }

    public void close(){
        /*
        if(!realm.isClosed()){
            realm.close();
        }*/
    }
}
