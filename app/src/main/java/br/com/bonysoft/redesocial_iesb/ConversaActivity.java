package br.com.bonysoft.redesocial_iesb;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.modelo.Mensagem;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.ContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;
import io.realm.Realm;
import io.realm.RealmResults;

public class ConversaActivity extends AppCompatActivity implements
        MensagemFragment.OnListFragmentInteractionListener{

    private FloatingActionButton mFab;
    private String emailUsuario;
    private String emailContato;
    private EditText mTextoMensagem;
    private Toolbar mToolbarConversa;
    private Realm mRealm;

    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        mFab = (FloatingActionButton) findViewById(R.id.fbEnviarMensagem);
        mTextoMensagem =(EditText) findViewById(R.id.editTextoConversa);
        mToolbarConversa = (Toolbar) findViewById(R.id.toolbarConversa);

        ref = FirebaseDatabase.getInstance().getReference("mensagens");

        emailContato = getIntent().getStringExtra(Constantes.EMAIL_CONVERSA);

        mRealm = Realm.getDefaultInstance();
        Contato contato = mRealm.where(Contato.class).equalTo("email",emailContato).findFirst();

        if(contato != null) {
            mToolbarConversa.setTitle(contato.getNome() +" " + contato.getSobreNome());
        } else {
            mToolbarConversa.setTitle( emailContato);
        }

        ContatoRepositorio repo = new ContatoRepositorio();
        emailUsuario = repo.buscaEmailUsuarioLogado();
        repo.close();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTextoMensagem != null && mTextoMensagem.getText().toString() != null
                        &&  !mTextoMensagem.getText().toString().trim().isEmpty()) {
                    Mensagem msg = new Mensagem(emailUsuario, emailContato, mTextoMensagem.getText().toString());
                    Log.i(Constantes.TAG_LOG,"Incluindo nova Mensagem -->"+ msg);

                    ref.push().setValue(msg);
                    mTextoMensagem.setText("");
                }
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.lista_conversa_fragment, MensagemFragment.newInstance(emailContato));
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
    }

    @Override
    public void onListFragmentInteraction(Mensagem item) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
