package br.com.bonysoft.redesocial_iesb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());


        setContentView(R.layout.activity_login);

        LoginButton loginButton = (LoginButton) this.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
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

                });


        ProfileTracker profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {

                if (currentProfile!=null){

                    Toast.makeText(LoginActivity.this, "Olá, " + currentProfile.getName(), Toast.LENGTH_LONG).show();

                    // :TODO - GRAVAR O USUARIO OBTIDO DO SDK DO FACEBOOK
                    // PANA, GRAVA AQUI NO REALM OS DADOS DA CRIATURA RECEM LOGADA
                    // TO OLHANDO COMO SE OBTEM O TELEFONE DO CARA NO FB

                }else{

                    if (oldProfile!=null) {

                        Toast.makeText(LoginActivity.this, oldProfile.getName()+" desconectou", Toast.LENGTH_LONG).show();

                    }
                }


            }
        };

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

}

