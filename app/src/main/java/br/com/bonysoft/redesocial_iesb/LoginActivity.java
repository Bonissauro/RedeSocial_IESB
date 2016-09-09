package br.com.bonysoft.redesocial_iesb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.modelo.Usuario;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.ContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.IContatoRepositorio;
import io.realm.RealmResults;

public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    String idUsuarioLogado;
    private EditText loginText;
    private EditText senhaText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);

        LoginButton loginButton = (LoginButton) this.findViewById(R.id.login_button);

        EditText loginText = (EditText) this.findViewById(R.id.etxtEmail);
        EditText senhaText = (EditText) this.findViewById(R.id.etxtSenha);

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
                                    Contato c = convertFacebookJsonToContato(jsonObject,null);

                                    setIdUsuarioLogado(c.getId_usuario());

                                    incluirUsuarioFacebookComoContatoPrincipal(c);
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

                                    List<Contato> contatoList = convertFacebookJsonToContato(jsonArray,getIdUsuarioLogado());

                                    for(Contato contato: contatoList){
                                        Log.i("ContatoLogFriendAdd" , contato.toString());
                                        gravarContato(contato,false,true);
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
                            it.putExtra(Constantes.ID_USUARIO_PESQUISA,getIdUsuarioLogado() );
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
                        exception.printStackTrace();
                        Log.i("ContatoLogLoginActivity", exception.toString());
                    }
            });
    }

    @Override
    protected void onStart() {

        super.onStart();
/*
        IContatoRepositorio contatoRepositorio = new ContatoRepositorio();
        contatoRepositorio.getAllContatos(new IContatoRepositorio.OnGetAllContatosCallback() {
            @Override
            public void onSuccess(RealmResults<Contato> itens) {
                Log.i("ContatoLog", "Quantidade Itens => "+itens.size());
            }

            @Override
            public void onError(String message) {
                Log.i("ContatoLogGetAll", message);
            }
        });

*/
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
            gravarContato(contato,true,true);
            return true;
        }

        return false;
    }

    private boolean usuarioJaRegistradoComoContato(Contato contato){
        IContatoRepositorio contatoRepositorio = new ContatoRepositorio();


        Log.i("ContatoLog","IdProfile-"+contato.getId_usuario());
        List<Contato> contatoList = contatoRepositorio.getAllContatosByUsuarioId(contato.getId_usuario(), new IContatoRepositorio.OnGetAllContatosCallback() {
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
        contatoRepositorio.deleteContatoById( "", new IContatoRepositorio.OnDeleteContatoCallback() {
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

    private void gravarContato(Contato contato, boolean isPrincipal,boolean isViaFacebook){
        try {
            Log.i("ContatoLog", "IdUsuarioFace" + contato.getId_usuario());
            contato.setUsuarioPrincipal(isPrincipal);

            IContatoRepositorio contatoRepositorio = new ContatoRepositorio();


            IContatoRepositorio.OnSaveContatoCallback callBackFace =
                    new IContatoRepositorio.OnSaveContatoCallback() {
                        @Override
                        public void onSuccess(Contato contato) {

                            Log.i("ContatoLog", "Sucesso na Gravacao");
                            Log.i("ContatoLog", "Caminho Login" + contato.getCaminhoFoto());

                            BasicImageDownloader download = new BasicImageDownloader(new BasicImageDownloader.OnImageLoaderListener( ) {
                                @Override
                                public void onError(BasicImageDownloader.ImageError error) {

                                }

                                @Override
                                public void onProgressChange(int percent) {

                                }

                                @Override
                                public void onComplete(Bitmap result) {

                                }
                            });
                            String url = "https://graph.facebook.com/" + contato.getId() + "/picture?width=200&height=150";
                            Log.i("ContatoLog","Caminho da foto-> "+ contato.getCaminhoFoto());
                            download.download(url,false,contato);
                        }

                        @Override
                        public void onError(String message) {
                            Log.i("ContatoLog", "Erro ==> " + message);
                        }
                    };

            IContatoRepositorio.OnSaveContatoCallback callBackSemFace =
                    new IContatoRepositorio.OnSaveContatoCallback() {
                        @Override
                        public void onSuccess(Contato contato) {
                            Log.i("ContatoLog", "Sucesso na Gravacao");
                        }

                        @Override
                        public void onError(String message) {
                            Log.i("ContatoLog", "Erro ==> " + message);
                        }
                    };

            if(isViaFacebook) {
                contato = contatoRepositorio.addContato(contato, callBackFace);
            } else {
                contato = contatoRepositorio.addContato(contato, callBackSemFace);
            }

            Log.i("ContatoLog", "contato adicionado =>" + contato.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    private Contato convertFacebookJsonToContato(JSONObject object, String id) {

        try {
            Contato contato = new Contato();

            if(id == null || id.isEmpty()){
                id = object.getString("id");
            }

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("ContatoLogProfilePic", profile_pic + "");

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

    private List<Contato> convertFacebookJsonToContato(JSONArray object,String id) {
        List<Contato> listaContato = new ArrayList<>();

        try {
            if (object.length() > 0) {
                for (int i = 0; i < object.length(); i++) {
                    listaContato.add(convertFacebookJsonToContato(object.optJSONObject(i),id));
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
        EditText loginText = (EditText) this.findViewById(R.id.etxtEmail);
        EditText senhaText = (EditText) this.findViewById(R.id.etxtSenha);

        if(loginText.getText().toString() != null && senhaText.getText().toString() != null){
            Usuario usuarioMock = new Usuario("Nome Mock",loginText.getText().toString(),senhaText.getText().toString());

            Contato c = new Contato();
            c.setNome(usuarioMock.getNome());
            c.setEmail(usuarioMock.getEmail());

            gravarContato(c,true,false);

            Intent it = new Intent(LoginActivity.this, PrincipalActivity.class);
            it.putExtra(Constantes.ID_USUARIO_PESQUISA,c.getId() );
            startActivity(it);
        } else {
            Toast.makeText(LoginActivity.this, "Informe o Login ou senha", Toast.LENGTH_LONG).show();
        }
    }

    public void naoTenhoConta(View v){
        Toast.makeText(LoginActivity.this, "Ainda nao tenho conta nao implementado", Toast.LENGTH_LONG).show();
    }

    public String getIdUsuarioLogado() {
        return idUsuarioLogado;
    }

    public void setIdUsuarioLogado(String idUsuarioLogado) {
        this.idUsuarioLogado = idUsuarioLogado;
    }



}

