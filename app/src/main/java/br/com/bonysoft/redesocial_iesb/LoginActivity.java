package br.com.bonysoft.redesocial_iesb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.bonysoft.redesocial_iesb.realm.repositorio.ContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.IContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;

public class LoginActivity extends AppCompatActivity {

    String TAG = Constantes.TAG_LOGIN_ABERTURA;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseAuth firebaseAuth;

    private boolean jaChamou = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login_abertura);

        setTitle("Rede do IESB - Login");

        Button btnLoginFacebook = (Button) findViewById(R.id.idLogin_btnLoginFacebook);
        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this, LoginFacebookActivity.class);
                startActivity(it);
            }
        });

        Button btnLoginGoogle = (Button) findViewById(R.id.idLogin_btnLoginGoogle);
        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this, LoginGoogleActivity.class);
                startActivity(it);
            }
        });

        Button btnLoginEmail = (Button) findViewById(R.id.idLogin_btnLoginEmail);
        btnLoginEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this, LoginEmailSenhaActivity.class);
                startActivity(it);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        // LISTENER QUE VERIFICA SE O APARELHO JA ESTA CADASTRADO A UMA CONTA
        // NO FIREBASE, SEJA ELA DO FACEBOOK, GOOGLE OU OUTRO TIPO DE CONTA.
        // CASO ESTEJA CADASTRADO, PULA A TELA DE LOGIN E SALTA DIRETO
        // PARA A ACTIVITY PRINCIPAL

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                // POR ALGUM MOTIVO, ELE CHAMAVA DUAS VEZES ESTE LISTENER.
                // FUI OBRIGADO A COLOCAR ESTE GATO PRA QUE SO PASSASSE UMA VEZ. (GIOVANNI, 07/10)
                if (!jaChamou){

                    jaChamou = true;

                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    if (user != null) {

                        Intent it = new Intent(LoginActivity.this,PrincipalActivity.class);
                        startActivity(it);
                        finish();

                    } else {

                        ApplicationRedeSocial.getInstance().setUsuarioLogado(null);

                    }

                }

            }
        };

    }

    public void deleteAll(View v) {
        //TODO remover deposi dos testes
        IContatoRepositorio contatoRepositorio = new ContatoRepositorio();
        contatoRepositorio.deleteContatoById("", new IContatoRepositorio.OnDeleteContatoCallback() {
            @Override
            public void onSuccess() {

                Log.i(Constantes.TAG_LOG, "Sucesso no delete all");
                Toast.makeText(LoginActivity.this,"Excluido todos os contatos",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String message) {
                Log.i(Constantes.TAG_LOG, "Erro delete all ==> " + message);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
        LoginManager.getInstance().logOut();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
