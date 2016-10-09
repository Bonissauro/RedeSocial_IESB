package br.com.bonysoft.redesocial_iesb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;

public class LoginGoogleActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    String TAG_LOG = Constantes.TAG_LOGIN_GOOGLE;

    public static int RC_SIGN_IN=9199;

    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("407632591322-ufg1ta34lfup35oeacijibuf9vb0hhsv.apps.googleusercontent.com")
            .requestEmail()
            .build();

    GoogleApiClient mGoogleApiClient;

    private FirebaseAuth firebaseAuth;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_google);

        firebaseAuth = FirebaseAuth.getInstance();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.sign_in_button:

                        dialog = ProgressDialog.show(LoginGoogleActivity.this, "Aguarde", "Conectando ao Google...", true);

                        signIn();
                        break;

                }
            }
        });

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {

                GoogleSignInAccount account = result.getSignInAccount();

                dialog.setMessage("Registrando sua conta...");

                firebaseAuthWithGoogle(account);

            } else {

                dialog.dismiss();

                Toast.makeText(LoginGoogleActivity.this, "Autenticação do Google falhou.", Toast.LENGTH_LONG).show();

            }

        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {

                            dialog.dismiss();

                            Toast.makeText(LoginGoogleActivity.this, "Authentication failed: "+task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();

                        }else{

                            // ABRE A ACTIVITY PRINCIPAL E FECHA TODAS AS ANTERIORES, DE MODO A BLOQUEAR
                            // A NAVEGAÇAO DO USUARIO PELAS ACTIVITIES DE LOGIN

                            Intent it = new Intent(LoginGoogleActivity.this, PrincipalActivity.class);
                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(it);

                            dialog.dismiss();

                        }

                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}