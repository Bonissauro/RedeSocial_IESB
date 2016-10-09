package br.com.bonysoft.redesocial_iesb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;

public class LoginFacebookActivity extends AppCompatActivity {

    String TAG = Constantes.TAG_LOGIN_FACEBOOK;
    String TAG_LOG = Constantes.TAG_LOGIN_FACEBOOK;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseAuth firebaseAuth;

    LoginResult loginResultFacebook;

    CallbackManager mCallbackManager;

    LoginButton loginButton;
    TextView idLoginFacebook_mensagem;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login_facebook);

        idLoginFacebook_mensagem = (TextView) findViewById(R.id.idLoginFacebook_mensagem);

        firebaseAuth = FirebaseAuth.getInstance();

        mCallbackManager = CallbackManager.Factory.create();

        /*
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        */

        loginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        loginButton.setReadPermissions("email", "public_profile");

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult loginResult) {

                dialog = ProgressDialog.show(LoginFacebookActivity.this, "Aguarde", "Conectando ao Facebook...", true);

                idLoginFacebook_mensagem.setVisibility(View.VISIBLE);
                idLoginFacebook_mensagem.setText("Concluindo seu registro...\nAguarde");
                loginButton.setVisibility(View.GONE);

                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(LoginFacebookActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (!task.isSuccessful()) {

                                    Toast.makeText(LoginFacebookActivity.this, "Authentication failed./n"+task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                }else{


                                                    String accessToken = loginResult.getAccessToken().getToken();

                                                    GraphRequest requestMe = GraphRequest.newMeRequest(
                                                            loginResult.getAccessToken(),
                                                            new GraphRequest.GraphJSONObjectCallback() {

                                                                @Override
                                                                public void onCompleted(
                                                                        JSONObject jsonObject,
                                                                        GraphResponse response) {
                                                                    Log.i(TAG_LOG, "Json com os dados pessoais -->" + jsonObject.toString());
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
                                                                    Log.i(TAG_LOG, "Json com os amigos localizados"+jsonArray.toString());
                                                                }
                                                            }
                                                    );

                                                    Bundle parameters = new Bundle();
                                                    parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location"); // Parametros que pedimos ao facebook
                                                    requestMe.setParameters(parameters);
                                                    requestFriends.setParameters(parameters);

                                                    GraphRequestBatch batch = new GraphRequestBatch(
                                                            requestMe, requestFriends);

                                                    dialog.setMessage("Buscando suas informações de perfil...");

                                                    batch.addCallback(new GraphRequestBatch.Callback() {
                                                        @Override
                                                        public void onBatchCompleted(GraphRequestBatch graphRequests) {

                                                            // CONCLUIU AMBOS OS PROCESSOS (BUSCA PERFIL PESSOAL E AMIGOS)

                                                            // ABRE A ACTIVITY PRINCIPAL E FECHA TODAS AS ANTERIORES, DE MODO A BLOQUEAR
                                                            // A NAVEGAÇAO DO USUARIO PELAS ACTIVITIES DE LOGIN

                                                            Intent it = new Intent(LoginFacebookActivity.this, PrincipalActivity.class);
                                                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(it);

                                                            dialog.dismiss();

                                                        }
                                                    });
                                                    batch.executeAsync();

                                }
                            }
                        });

            }

            @Override
            public void onCancel() { // CASO O USUARIO TENHA CANCELADO O PROCESSO

            }

            @Override
            public void onError(FacebookException error) {

                idLoginFacebook_mensagem.setVisibility(View.VISIBLE);
                idLoginFacebook_mensagem.setText("Houve um erro na validação\ndo Facebook. Pode ser que sua\ninternet esteja com alguma\ninstabilidade.\n\nVerifique e tente de novo.");
                loginButton.setVisibility(View.VISIBLE);

            }
        });

    }

    @Override
    protected void onStart() {

        super.onStart();

        // FORÇA O LOGOFF DO CARA PRA EVITAR AQUELE BOTAO "SAIR" DO FACEBOOK
        LoginManager.getInstance().logOut();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

}
