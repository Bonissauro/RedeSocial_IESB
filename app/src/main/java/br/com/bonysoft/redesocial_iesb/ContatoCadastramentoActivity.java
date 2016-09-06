package br.com.bonysoft.redesocial_iesb;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.ContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.IContatoRepositorio;

public class ContatoCadastramentoActivity extends AppCompatActivity {

    Contato contatoSelecionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contato_cadastramento);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gravaContato();

            }
        });

        final EditText edtEmail = (EditText) findViewById(R.id.edtEmail);
        final EditText edtNome = (EditText) findViewById(R.id.edtNome);
        final EditText edtTelefone = (EditText) findViewById(R.id.edtTelefone);
        final EditText edtNomeSkype = (EditText) findViewById(R.id.edtNomeSkype);
        final EditText edtEnderecoCompleto = (EditText) findViewById(R.id.edtEndereco);

        contatoSelecionado = null;

        String id = getIntent().getStringExtra("id");

        if (id!=null) {

            setTitle("Alteração de contato");

            IContatoRepositorio contatoRepositorio = new ContatoRepositorio();
            contatoRepositorio.getContatoById( id, new IContatoRepositorio.OnGetContatoByIdCallback() {

                @Override
                public void onSuccess(Contato contato) {

                    contatoSelecionado = contato;

                    edtEmail.setText(getIntent().getStringExtra("nome"));
                    edtNome.setText(contatoSelecionado.getNome());
                    edtTelefone.setText(contatoSelecionado.getTelefone());
                    edtNomeSkype.setText(contatoSelecionado.getNomeSkype());
                    edtEnderecoCompleto.setText(contatoSelecionado.getEndereco());

                }

                @Override
                public void onError(String message) {

                }

            });

        }

    }

    private void gravaContato() {

        final EditText edtEmail = (EditText) findViewById(R.id.edtEmail);
        final EditText edtNome = (EditText) findViewById(R.id.edtNome);
        final EditText edtTelefone = (EditText) findViewById(R.id.edtTelefone);
        final EditText edtNomeSkype = (EditText) findViewById(R.id.edtNomeSkype);
        final EditText edtEnderecoCompleto = (EditText) findViewById(R.id.edtEndereco);

        IContatoRepositorio contatoRepositorio = new ContatoRepositorio();

        Contato contato = new Contato();

        if (contatoSelecionado!=null) {
            contato.setId(contatoSelecionado.getId());
        }

        contato.setEmail(edtEmail.getText().toString());
        contato.setNome(edtNome.getText().toString());
        contato.setTelefone(edtTelefone.getText().toString());
        contato.setNomeSkype(edtNomeSkype.getText().toString());
        contato.setEndereco(edtEnderecoCompleto.getText().toString());

        if (contato.getId() == null){

            contato.setUsuarioPrincipal(true);

            contatoRepositorio.addContato( contato, new IContatoRepositorio.OnSaveContatoCallback() {

                @Override
                public void onSuccess() {

                    Toast.makeText(getBaseContext(), "Sucesso na Gravacao", Toast.LENGTH_LONG).show();
                    finish();

                }

                @Override
                public void onError(String message) {

                    Toast.makeText(getBaseContext(), "Erro ==> " + message, Toast.LENGTH_LONG).show();

                }

            });

        }else{

            contatoRepositorio.editContato(contato, new IContatoRepositorio.OnSaveContatoCallback() {

                @Override
                public void onSuccess() {

                    Toast.makeText(getBaseContext(), "Sucesso na alteração", Toast.LENGTH_LONG).show();
                    finish();

                }

                @Override
                public void onError(String message) {

                    Toast.makeText(getBaseContext(), "Erro ==> " + message, Toast.LENGTH_LONG).show();

                }

            });


        }

    }

}
