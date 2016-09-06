package br.com.bonysoft.redesocial_iesb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.modelo.Usuario;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.ContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.IContatoRepositorio;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);

        LoginButton loginButton = (LoginButton) this.findViewById(R.id.login_button);

        loginButton.setReadPermissions(Arrays.asList(
                    "public_profile", "email", "user_friends"));

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
            new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    String accessToken = loginResult.getAccessToken().getToken();
                    Log.i("ContatosLogAcessToken", accessToken);


                    GraphRequest requestMe = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject jsonObject,
                                        GraphResponse response) {
                                    Log.i("ContatoLog", "RespostaJsonUsuario->" + response.toString());
                                    // Get facebook data from login

                                    Log.i("ContatoLogJUser", jsonObject.toString());
                                    Log.i("ContatoLogRUser" , response.toString());

                                    incluirUsuarioFacebookComoContatoPrincipal(convertFacebookJsonToContato(jsonObject));
                                }
                            }
                    );

                    GraphRequest requestFriends = GraphRequest.newMyFriendsRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONArrayCallback() {
                                @Override
                                public void onCompleted(
                                        JSONArray jsonArray,
                                        GraphResponse response) {
                                    Log.i("ContatoLog", "RespostaJsonFriends->" + response.toString());
                                    // Get facebook data from login
                                    Log.i("ContatoLogJFriend", jsonArray.toString());
                                    Log.i("ContatoLogRFriend" , response.toString());

                                    List<Contato> contatoList = convertFacebookJsonToContato(jsonArray);

                                    for(Contato contato: contatoList){
                                        Log.i("ContatoLogFriendAdd" , contato.toString());
                                        gravarContato(contato,false);
                                    }
                                }
                            }
                    );
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
                    requestMe.setParameters(parameters);
                    requestFriends.setParameters(parameters);

                    GraphRequestBatch batch = new GraphRequestBatch(
                            requestMe,requestFriends);

                    batch.addCallback(new GraphRequestBatch.Callback() {
                        @Override
                        public void onBatchCompleted(GraphRequestBatch graphRequests) {
                            //TODO: aqui ele chama qdo terminar os dois requests do facebook
                            Intent it = new Intent(LoginActivity.this, PrincipalActivity.class);
                            startActivity(it);
                        }
                    });

                    batch.executeAsync();
                }

                    @Override
                    public void onCancel() {
                        Log.i("ContatoLogCancel", "Entrou no onCancel Facebook");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.i("ContatoLogLoginActivity", "Entrou no onError Facebook");
                        Log.i("ContatoLogLoginActivity", exception.getCause().toString());
                    }
            });
    }

    @Override
    protected void onStart() {

        super.onStart();

        IContatoRepositorio contatoRepositorio = new ContatoRepositorio();
        contatoRepositorio.getAllContatos(this, new IContatoRepositorio.OnGetAllContatosCallback() {
            @Override
            public void onSuccess(RealmResults<Contato> itens) {
                Log.i("ContatoLog", "Quantidade Itens => "+itens.size());
            }

            @Override
            public void onError(String message) {
                Log.i("ContatoLogGetAll", message);
            }
        });


        //
        // Caso o cara retorne da ActivityPrincipal para cá "desloga" ele do Facebook e obriga
        // ele a relogar pra desfazer a caca que ele fez
        //

        LoginManager.getInstance().logOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private boolean incluirUsuarioFacebookComoContatoPrincipal(Contato contato){
        if(!usuarioJaRegistradoComoContato(contato)){
            gravarContato(contato,true);
            return true;
        }

        return false;
    }

    private boolean usuarioJaRegistradoComoContato(Contato contato){
        IContatoRepositorio contatoRepositorio = new ContatoRepositorio();

        Log.i("ContatoLog","IdProfile-"+contato.getId_usuario());
        List<Contato> contatoList = contatoRepositorio.getAllContatosByUsuarioId(this,contato.getId_usuario(), new IContatoRepositorio.OnGetAllContatosCallback() {
            @Override
            public void onSuccess(RealmResults<Contato> itens) {
                Log.i("ContatoLog","Sucesso na Consulta Usuario Id->" + itens.size() );
            }

            @Override
            public void onError(String message) {
                Log.i("ContatoLog","Erro Consulta Usuario Id ==> "+message);
            }
        });
        contato = null;
        Log.i("ContatoLog","Quantidade Por Id Usuario->"+contatoList.size());
        for(Contato item : contatoList){
            Log.i("ContatoLog","Item-"+item.getNome() + " ID_Usuario-" + item.getId_usuario() + " Id-" + item.getId());
            if(item.isUsuarioPrincipal()){
                Log.i("ContatoLog","E usuario Principal");
                contato = item;
                break;
            }
        }

        return (contato != null && contato.getId_usuario()!=null && !contato.getId_usuario().trim().isEmpty());
    }

    public void deleteAll(View v){
        IContatoRepositorio contatoRepositorio = new ContatoRepositorio();
        contatoRepositorio.deleteContatoById(this, "", new IContatoRepositorio.OnDeleteContatoCallback() {
            @Override
            public void onSuccess() {
                Log.i("ContatoLog","Sucesso no delete all");
            }

            @Override
            public void onError(String message) {
                Log.i("ContatoLog","Erro delete all ==> "+message);
            }
        });
    }

    private void gravarContato(Contato contato,boolean isPrincipal){
        IContatoRepositorio contatoRepositorio = new ContatoRepositorio();

        Log.i("ContatoLog", "IdUsuarioFace"+contato.getId_usuario());
        contato.setUsuarioPrincipal(isPrincipal);

        contato = contatoRepositorio.addContato(this,contato, new IContatoRepositorio.OnSaveContatoCallback() {
            @Override
            public void onSuccess() {
                Log.i("ContatoLog","Sucesso na Gravacao");
            }

            @Override
            public void onError(String message) {
                Log.i("ContatoLog","Erro ==> "+message);
            }
        });
        Log.i("ContatoLog","contato adicionado =>"+ contato.toString());
    }

    private Contato convertFacebookJsonToContato(JSONObject object) {

        try {
            Contato contato = new Contato();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("ContatoLogProfilePic", profile_pic + "");
                contato.setCaminhoFoto(profile_pic.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            contato.setId_usuario(id);
            if (object.has("first_name")) {
                contato.setNome(object.getString("first_name"));
            }

            if (object.has("last_name")) {
                contato.setSobreNome(object.getString("last_name"));
            }

            if (object.has("email")) {
                contato.setEmail(object.getString("email"));
            }

            if (object.has("birthday")) {
                SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");
                contato.setDataNascimento( sdf.parse(object.getString("birthday")));
            }

            if (object.has("gender")) {
                object.getString("gender");
            }

            if (object.has("location")) {
                object.getJSONObject("location").getString("name");
            }
            Log.i("ContatoLog","contato Json =>"+ contato.toString());
            return contato;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private List<Contato> convertFacebookJsonToContato(JSONArray object) {
        List<Contato> listaContato = new ArrayList<>();

        try {
            if (object.length() > 0) {
                for (int i = 0; i < object.length(); i++) {
                    listaContato.add(convertFacebookJsonToContato(object.optJSONObject(i)));
                }
            }

            return listaContato;
        } catch (Exception e){
            e.printStackTrace();
        }
        return listaContato;
    }

    public void esqueciMinhaSenha(View v){
        Toast.makeText(LoginActivity.this, "Esqueci minha senha nao implementado", Toast.LENGTH_LONG).show();

    }

    public void lembrarSenha(View v){
        Toast.makeText(LoginActivity.this, "Lembrar senha nao implementado", Toast.LENGTH_LONG).show();
    }

    public void logarComLoginSenha(View v){
        Toast.makeText(LoginActivity.this, "Logar por login e senha nao implementado", Toast.LENGTH_LONG).show();
    }

    public void naoTenhoConta(View v){
        Toast.makeText(LoginActivity.this, "Ainda nao tenho conta nao implementado", Toast.LENGTH_LONG).show();
    }


}

