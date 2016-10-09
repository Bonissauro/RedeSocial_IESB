package br.com.bonysoft.redesocial_iesb;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;

public class LoginEmailSenhaActivity extends AppCompatActivity {

    String TAG_LOG = Constantes.TAG_LOGIN_EMAIL;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    TextView txtEmail;
    TextView txtSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_email);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG_LOG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG_LOG, "onAuthStateChanged:signed_out");
                }

            }
        };

        txtEmail = (TextView) findViewById(R.id.idLoginEmail_edtEmail);
        txtSenha = (TextView) findViewById(R.id.idLoginEmail_edtSenha);

        Button btnLogar = (Button) findViewById(R.id.idLoginEmail_btnConfirmaLogin);

        btnLogar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String email = txtEmail.getText().toString();
                String password = txtSenha.getText().toString();

                if (email.equalsIgnoreCase("")){
                    Toast.makeText(LoginEmailSenhaActivity.this, "Informe seu e-mail!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (password.equalsIgnoreCase("")){
                    Toast.makeText(LoginEmailSenhaActivity.this, "Informe sua senha!", Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginEmailSenhaActivity.this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {

                            Toast.makeText(LoginEmailSenhaActivity.this, "Não existe esse e-mail ou senha!", Toast.LENGTH_LONG).show();

                        }else{

                            // ABRE A ACTIVITY PRINCIPAL E FECHA TODAS AS ANTERIORES, DE MODO A BLOQUEAR
                            // A NAVEGAÇAO DO USUARIO PELAS ACTIVITIES DE LOGIN

                            Intent it = new Intent(LoginEmailSenhaActivity.this, PrincipalActivity.class);
                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(it);

                        }

                    }

                });

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
