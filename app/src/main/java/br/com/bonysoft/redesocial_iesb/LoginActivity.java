package br.com.bonysoft.redesocial_iesb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
                 "public_profile", "email", "user_birthday", "user_friends"));

        /*loginButton.setReadPermissions(Arrays.asList(
               // "public_profile", "email", "user_birthday", "user_friends"));
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(realmConfig);*/

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    /*

                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        Intent it = new Intent(LoginActivity.this, PrincipalActivity.class);

                        startActivity(it);

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Operação cancelada.", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onError(FacebookException exception) {

                        Toast.makeText(LoginActivity.this, exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                    }
*/


                    @Override
                    public void onSuccess(LoginResult loginResult) {

                       // progressDialog = new ProgressDialog(LoginActivity.this);
                       // progressDialog.setMessage("Procesando datos...");
                       // progressDialog.show();
                        String accessToken = loginResult.getAccessToken().getToken();
                        Log.i("ContatosLogAcessToken", accessToken);
                        /*
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.i("ContatoLog", "RespostaJson->" + response.toString());
                                // Get facebook data from login
                                Bundle bFacebookData = getFacebookData(object);
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email,gender, user_birthday, location, user_friends"); // Parámetros que pedimos a facebook
                        request.setParameters(parameters);
                        request.executeAsync(); */

                        GraphRequestBatch batch = new GraphRequestBatch(
                                GraphRequest.newMeRequest(
                                        loginResult.getAccessToken(),
                                        new GraphRequest.GraphJSONObjectCallback() {
                                            @Override
                                            public void onCompleted(
                                                    JSONObject jsonObject,
                                                    GraphResponse response) {
                                                Log.i("ContatoLog", "RespostaJsonUsuario->" + response.toString());
                                                // Get facebook data from login
                                                Bundle bFacebookData = getFacebookData(jsonObject);
                                                Log.i("ContatoLogJUser", jsonObject.toString());
                                                Log.i("ContatoLogRUSer" , response.toString());
                                            }
                                        }),
                                GraphRequest.newMyFriendsRequest(
                                        loginResult.getAccessToken(),
                                        new GraphRequest.GraphJSONArrayCallback() {
                                            @Override
                                            public void onCompleted(
                                                    JSONArray jsonArray,
                                                    GraphResponse response) {
                                                Log.i("ContatoLog", "RespostaJsonFriends->" + response.toString());
                                                // Get facebook data from login
                                                //Bundle bFacebookData = getFacebookData(jsonArray);
                                                Log.i("ContatoLogJFriend", jsonArray.toString());
                                                Log.i("ContatoLogRFriend" , response.toString());
                                            }
                                        })
                        );
                        batch.addCallback(new GraphRequestBatch.Callback() {
                            @Override
                            public void onBatchCompleted(GraphRequestBatch graphRequests) {
                                // Application code for when the batch finishes
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

/*
        final ProfileTracker profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {

                if (currentProfile!=null){

                    Toast.makeText(LoginActivity.this, "Olá, " + currentProfile.getName(), Toast.LENGTH_LONG).show();
                    // Gravacao dos dados do Login no Contato.
                    if(usuarioJaRegistradoComoContato(currentProfile)){
                        Log.i("ContatoLog","Entrou");
                        gravarProfileNoContato(currentProfile);
                    } else {
                        Log.i("ContatoLog","Novo");
                    }

                }else{

                    if (oldProfile!=null) {

                        Toast.makeText(LoginActivity.this, oldProfile.getName()+" desconectou", Toast.LENGTH_LONG).show();

                    }
                }


            }
        };*/

    }

    @Override
    protected void onStart() {

        super.onStart();

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

    private boolean usuarioJaRegistradoComoContato(Profile profileFace){
        IContatoRepositorio contatoRepositorio = new ContatoRepositorio();

        Log.i("ContatoLog","IdProfile-"+profileFace.getId());
       // List<Contato> contatoList = contatoRepositorio.getAllContatosByUsuarioId(this,profileFace.getId(), new IContatoRepositorio.OnGetAllContatosCallback() {
        List<Contato> contatoList = contatoRepositorio.getAllContatos(this, new IContatoRepositorio.OnGetAllContatosCallback() {
            @Override
            public void onSuccess(RealmResults<Contato> students) {

            }

            @Override
            public void onError(String message) {

            }
        });

        Log.i("ContatoLog","Quantidade-"+contatoList.size());
        Contato contato= null;
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

    private void delete(String id){

    }

    private void gravarContatosListAmigos(){
        IContatoRepositorio contatoRepositorio = new ContatoRepositorio();
    }

    private void gravarProfileNoContato(Profile profileFace){
        IContatoRepositorio contatoRepositorio = new ContatoRepositorio();

        Contato contato = new Contato();
        contato.setNome(profileFace.getFirstName());
        contato.setSobreNome(profileFace.getLastName());
        contato.setUsuarioPrincipal(true);
        contato.setDataNascimento(new Date());
        contato.setId_usuario(profileFace.getId());
        contato.setEmail("meuemail@gmail.com");

        Log.i("ContatoLog",profileFace.getId());

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
        Log.i("ContatoLog","IdContato-"+ contato.getId());

    }

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("ContatoLogProfilePic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    private List<Bundle> getFacebookData(JSONArray object) {
       /*
        try {
            List<Bundle> myList = new ArrayList<>();
            if (object.length() > 0) {

                // Ensure the user has at least one friend ...
                for (int i = 0; i < object.length(); i++) {
                    Bundle bundle = new Bundle();
                    JSONObject jsonObject = object.optJSONObject(i);
                    FacebookFriend facebookFriend = new FacebookFriend(jsonObject, pickType[0]);

                    if (facebookFriend.isValid()) {
                        numberOfRecords++;

                        myList.add(facebookFriend);
                    }
                }
            }

            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("ContatoLog", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        } catch (Exception e){
            e.printStackTrace();
        }*/
        return null;
    }
/*
    public List<Contato> getAllContatosByUsuarioId( String id, IContatoRepositorio.OnGetAllContatosCallback callback) {

        Realm realm = Realm.getDefaultInstance();
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
    */
}

