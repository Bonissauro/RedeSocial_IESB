package br.com.bonysoft.redesocial_iesb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.ContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.IContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.MensagemLogin;
import br.com.bonysoft.redesocial_iesb.utilitarios.BasicImageDownloader;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;

public class LoginActivity extends AppCompatActivity {

    String TAG_LOG = Constantes.TAG_LOG;

    ProgressDialog dialog;
    CallbackManager callbackManager;
    LoginButton loginFacebookButton;
    EditText loginText;
    EditText senhaText;

    String idUsuarioLogado;
    boolean loginComSucesso;
    boolean novoUsuario;

    Contato contatoObtidoPeloFacebook;
    List<Contato> listaContatosAmigosFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);
        inicializarItens();

        processoLoginFace();
    }

    private void inicializarItens(){
        contatoObtidoPeloFacebook = null;
        listaContatosAmigosFacebook = new ArrayList<>();

        loginComSucesso = false;
        novoUsuario = false;

        loginFacebookButton = (LoginButton) this.findViewById(R.id.login_button);
        loginText = (EditText) this.findViewById(R.id.etxtEmail);
        senhaText = (EditText) this.findViewById(R.id.etxtSenha);

        //TODO remover depois dos testes
        loginText.setText("teste@gmail.com");
        senhaText.setText("12345678");

        idUsuarioLogado = null;
    }

    IContatoRepositorio.OnGetContatoLogin tratamentoLogin = new IContatoRepositorio.OnGetContatoLogin() {
        @Override
        public void onSuccess(MensagemLogin message, Contato contato) {

            idUsuarioLogado = contato.getId();
            Log.i(TAG_LOG,"Id Usuario Logado -->"+idUsuarioLogado);
            Log.i(TAG_LOG,"Mensagem de Sucesso -->"+message.getMsg());
            switch (message){
                case CADASTRAR:
                case CADASTRAR_COM_SUCESSO:
                    novoUsuario = true;
                    break;

                case LOGIN_COM_SUCESSO:
                    loginComSucesso = true;
                    break;
            }
            Log.i(TAG_LOG,"Mensagem de loginComSucesso -->"+loginComSucesso);
            Log.i(TAG_LOG,"Mensagem de novoUsuario -->"+novoUsuario);

            if(contatoObtidoPeloFacebook!= null){
                salvarFotoPerfilFacebook(contato.copy());
            }
        }

        @Override
        public void onError(MensagemLogin message) {
            Log.i(TAG_LOG,"Mensagem de Erro -->"+message.getMsg());
            Toast.makeText(LoginActivity.this,message.getMsg(),Toast.LENGTH_LONG);
        }
    };

    private void realizaNavegacao(){

        if(loginComSucesso){
            Intent it = new Intent(this, PrincipalActivity.class);
            it.putExtra(Constantes.ID_USUARIO_LOGADO, idUsuarioLogado);
            startActivity(it);
        }

        if(novoUsuario){
            Intent it = new Intent(this, ContatoCadastramentoActivity.class);
            it.putExtra(Constantes.ID_USUARIO_LOGADO, idUsuarioLogado);
            it.putExtra(Constantes.ID_CONTATO, idUsuarioLogado);
            startActivity(it);
        }
    }

    private void processoLoginFace() {

        loginFacebookButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_friends"));

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        String accessToken = loginResult.getAccessToken().getToken();
                        Log.i(TAG_LOG,"Token Acesso -->" + accessToken);

                        GraphRequest requestMe = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {

                                    @Override
                                    public void onCompleted(
                                            JSONObject jsonObject,
                                            GraphResponse response) {

                                        Log.i(TAG_LOG, "RespostaJsonUsuario->" + response.toString());

                                        Log.i(TAG_LOG, "Json -->" + jsonObject.toString());

                                        contatoObtidoPeloFacebook = convertFacebookJsonToContato(jsonObject);

                                        //TODO: aqui ele ta colocando a senha como sendo o login do face
                                        contatoObtidoPeloFacebook.setSenha(contatoObtidoPeloFacebook.getIdFacebook());
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

                                        Log.i(TAG_LOG, "Resposta Json Friends -->" + response.toString());
                                        Log.i(TAG_LOG, jsonArray.toString());
                                        listaContatosAmigosFacebook = convertFacebookJsonToContato(jsonArray);

                                    }
                                }
                        );
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location"); // Parametros que pedimos ao facebook
                        requestMe.setParameters(parameters);
                        requestFriends.setParameters(parameters);

                        GraphRequestBatch batch = new GraphRequestBatch(
                                requestMe, requestFriends);

                        batch.addCallback(new GraphRequestBatch.Callback() {
                            @Override
                            public void onBatchCompleted(GraphRequestBatch graphRequests) {
                                //aqui ele chama qdo terminar os dois requests do facebook

                                if(contatoObtidoPeloFacebook!=null){

                                    ContatoRepositorio repo = new ContatoRepositorio();

                                    boolean resultado = repo.validarUsuarioFacebook(contatoObtidoPeloFacebook, tratamentoLogin);
                                    // So adiciona os amigos se o login for feito com sucesso
                                    if(!listaContatosAmigosFacebook.isEmpty() && resultado){
                                        for (Contato contatoAmigo : listaContatosAmigosFacebook) {

                                            repo.addContatoPeloIdFacebookOuEmail(contatoAmigo, new IContatoRepositorio.OnSaveContatoCallback() {
                                                @Override
                                                public void onSuccess(Contato contato) {
                                                    Toast.makeText(getApplicationContext(),"Obtendo amigos pelo Facebook feito com sucesso!",Toast.LENGTH_LONG);
                                                    salvarFotoPerfilFacebook(contato.copy());
                                                }

                                                @Override
                                                public void onError(String message) {
                                                    Toast.makeText(getApplicationContext(),"Erro ao obter os amigos do Facebook!",Toast.LENGTH_LONG);
                                                }
                                            });
                                        }
                                    }
                                    repo.close();
                                    realizaNavegacao();
                                }
                            }
                        });
                        batch.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Log.i(TAG_LOG, "Entrou no onCancel Facebook");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.i(TAG_LOG, "Entrou no onError Facebook");
                        exception.printStackTrace();
                        Log.i(TAG_LOG, exception.toString());
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Caso o cara retorne da ActivityPrincipal para cá "desloga" ele do Facebook e obriga
        // ele a relogar pra desfazer a caca que ele fez
        LoginManager.getInstance().logOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void deleteAll(View v) {
        //TODO remover deposi dos testes
        IContatoRepositorio contatoRepositorio = new ContatoRepositorio();
        contatoRepositorio.deleteContatoById("", new IContatoRepositorio.OnDeleteContatoCallback() {
            @Override
            public void onSuccess() {
                Log.i(TAG_LOG, "Sucesso no delete all");
            }

            @Override
            public void onError(String message) {
                Log.i(TAG_LOG, "Erro delete all ==> " + message);
            }
        });
    }

    private Contato convertFacebookJsonToContato(JSONObject object) {

        try {
            Contato contato = new Contato();

            String id = object.getString("id");

            contato.setIdFacebook(id);

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i(TAG_LOG, profile_pic + "");

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }


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
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                contato.setDataNascimento(sdf.parse(object.getString("birthday")));
            }

            if (object.has("gender")) {
                object.getString("gender");
            }

            if (object.has("location")) {
                object.getJSONObject("location").getString("name");
            }
            Log.i(TAG_LOG, "contato Json =>" + contato.toString());
            return contato;
        } catch (Exception e) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaContato;
    }

    public void esqueciMinhaSenha(View v) {
        Toast.makeText(LoginActivity.this, "Esqueci minha senha nao implementado", Toast.LENGTH_LONG).show();

    }

    public void lembrarSenha(View v) {
        Toast.makeText(LoginActivity.this, "Lembrar senha nao implementado", Toast.LENGTH_LONG).show();
    }

    public void logarComLoginSenha(View v) {
        Log.i(TAG_LOG,"Entrou no logarComLoginSenha");

        //Toast.makeText(LoginActivity.this,"Teste LoginActivity",Toast.LENGTH_SHORT).show();

        dialog = ProgressDialog.show(LoginActivity.this, "Aguarde", "Validando conta...", true);
        dialog.show();
        /*
        new Thread(){
            public void run(){
                try {
                    Thread.sleep(9000);
                    dialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        */
        try {


            ContatoRepositorio repo = new ContatoRepositorio();
            boolean res = repo.validarUsuarioSenha(loginText.getText().toString(), senhaText.getText().toString(), tratamentoLogin);

            Log.i(TAG_LOG,"Resulado do Login" + res);

            repo.close();
            realizaNavegacao();
        } catch ( Exception e){
            e.printStackTrace();
            Toast.makeText(LoginActivity.this,"Erro ao logar",Toast.LENGTH_LONG);
        }

        dialog.dismiss();

    }

    private boolean salvarFotoPerfilFacebook(Contato contato){
        //TODO: implementar uma forma de salvar a foto de perfil do amigo
        Log.i(TAG_LOG, "Salvar Foto Perfil");
        if(contato== null || contato.getId() == null || contato.getId().trim().isEmpty() ){
            return false;
        }

        try {
            File dir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            if(dir == null){
                dir = getApplicationContext().getFilesDir();
            }

            contato.setCaminhoFoto(dir + "/" + contato.getId() + ".jpg");
            Log.i(TAG_LOG, "Caminho da foto-> " + contato.getCaminhoFoto());

            BasicImageDownloader downloader = new BasicImageDownloader(new BasicImageDownloader.OnImageLoaderListener() {
                @Override
                public void onError(BasicImageDownloader.ImageError error) {
                    Log.i(TAG_LOG, "Erro ao baixar Foto do face ");
                }

                @Override
                public void onProgressChange(int percent) {

                }

                @Override
                public void onComplete(Bitmap result) {
                    Log.i(TAG_LOG, "Imagem baixada");
                }
            });
            String url = "https://graph.facebook.com/" + contato.getIdFacebook() + "/picture?width=200&height=150";

            downloader.download(url, false, contato);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public void naoTenhoConta(View v) {
        //TODO acho que nao precisa mais desse metodo pois se o cara nao existe ele ja vai para tela de cadastro
        Toast.makeText(LoginActivity.this, "Ainda nao tenho conta nao implementado", Toast.LENGTH_LONG).show();
    }
}
