package br.com.bonysoft.redesocial_iesb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.modelo.Usuario;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.ContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.IContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;

public class ContatoCadastramentoActivity extends AppCompatActivity {

    String TAG_LOG = Constantes.TAG_LOG;

    private static final int TIRAR_FOTO = 1972;
    private static final int SELECIONAR_FOTO = 1973;
    private Bitmap bitmapSELECIONADO;
    private Contato contatoSelecionado = null;
    private boolean novo;
    private String caminhoFoto;
    private FirebaseAuth mAuth;

    private EditText edtEmail;
    private EditText edtNome;
    private EditText edtSobrenome;
    private EditText edtTelefone;
    private EditText edtNomeSkype;
    private EditText edtEnderecoCompleto;
    private EditText edtSenha;
    private SimpleDraweeView  sdvFoto;
    private SimpleDraweeView imgFresco;
    private FloatingActionButton fab;
    private Button btnCamera;
    private Button btnSelecionaFoto;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contato_cadastramento);

        inicializarComponentes();
        montarAcoes();

        String titulo = "";

        //String id_Usuario = getIntent().getStringExtra(Constantes.ID_USUARIO_LOGADO);
        Usuario user = ApplicationRedeSocial.getInstance().getUsuarioLogado();

        Log.i(TAG_LOG,"Entrou no Cadastro-->"+user);

        String id = getIntent().getStringExtra(Constantes.ID_CONTATO);

        String emailLogin = getIntent().getStringExtra(Constantes.CADASTRO_USUARIO);

        //if(id_Usuario!=null&& id_Usuario.equals(id)){
        if(ApplicationRedeSocial.getInstance().isRegistrado() == false){
            titulo = "Cadastro do Usuario";
            if(emailLogin!= null) {
                edtEmail.setText(emailLogin);
            }

            edtSenha.setVisibility(View.VISIBLE);
        } else {
            titulo = "Cadastro de Contato";
            edtSenha.setVisibility(View.GONE);
        }

        if(id != null && !id.isEmpty()){

            IContatoRepositorio repo = new ContatoRepositorio();
            repo.getContatoById(id, new IContatoRepositorio.OnGetContato() {

                @Override
                public void onSuccess(Contato contato) {
                    contatoSelecionado = contato.copy();
                }

                @Override
                public void onError(String message) {
                }
            });

            repo.close();

        }

        // Inicializando campos
        if(contatoSelecionado != null){
            novo = false;
            edtEmail.setText(contatoSelecionado.getEmail());
            edtNome.setText(contatoSelecionado.getNome());
            edtSobrenome.setText(contatoSelecionado.getSobreNome());
            edtTelefone.setText(contatoSelecionado.getTelefone());
            edtNomeSkype.setText(contatoSelecionado.getNomeSkype());
            edtEnderecoCompleto.setText(contatoSelecionado.getEndereco());
            caminhoFoto = contatoSelecionado.getCaminhoFoto();

            if(edtSenha.getVisibility() == View.VISIBLE) {
                edtSenha.setText(contatoSelecionado.getSenha());
            }

            if(contatoSelecionado.getCaminhoFoto()!=null && !contatoSelecionado.getCaminhoFoto().isEmpty()) {
                Uri imageUri = Uri.fromFile(new File(contatoSelecionado.getCaminhoFoto()));
                Log.i("ContatoLog","Caminho do Item na Alteracao " + imageUri.toString() );
                sdvFoto.setImageURI(imageUri);
            }
        } else {
            novo = true;
            titulo = "Novo contato";
            //Criando um contato com novo ID
            contatoSelecionado = new Contato(true);
        }

        setTitle(titulo);
    }

    private void inicializarComponentes(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtNome = (EditText) findViewById(R.id.edtNome);
        edtSobrenome = (EditText) findViewById(R.id.edtSobrenome);
        edtTelefone = (EditText) findViewById(R.id.edtTelefone);
        edtNomeSkype = (EditText) findViewById(R.id.edtNomeSkype);
        edtEnderecoCompleto = (EditText) findViewById(R.id.edtEndereco);
        edtSenha = (EditText) findViewById(R.id.edtSenhaCadastro);

        imgFresco = (SimpleDraweeView) findViewById(R.id.imgListaContatoCadastro);

        sdvFoto = (SimpleDraweeView) findViewById(R.id.imgListaContatoCadastro);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        btnCamera = (Button) findViewById(R.id.btnTiraFoto);
        btnSelecionaFoto = (Button) findViewById(R.id.btnSelecionaFoto);

        contatoSelecionado =null;

    }

    private void montarAcoes(){
        imgFresco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, TIRAR_FOTO);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtSenha.getVisibility() == View.VISIBLE && (edtSenha.getText().toString().trim().isEmpty()
                || edtSenha.getText().toString().trim().length() < 4)){
                    Toast.makeText(ContatoCadastramentoActivity.this,"Informe uma senha com no minimo 4 caracteres.",Toast.LENGTH_LONG).show();
                } else{

                    if(ApplicationRedeSocial.getInstance().isRegistrado() == false){
                        registrarUsuario();
                    } else {
                        salvarContato();
                    }

                }
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, TIRAR_FOTO);

            }
        });

        btnSelecionaFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,SELECIONAR_FOTO);

            }
        });

    }

    private void registrarUsuario(){
        Log.i(TAG_LOG,"Registrar Usuario");
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(),edtSenha.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(Constantes.TAG_LOG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Toast.makeText(ContatoCadastramentoActivity.this, "Não foi possivel realizar o cadastro no firebase",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Usuario u = new Usuario();
                    u.setEmail(edtEmail.getText().toString());
                    ApplicationRedeSocial.getInstance().setUsuarioLogado(u);

                    Toast.makeText(ContatoCadastramentoActivity.this, "Cadastro realizado com sucesso",
                            Toast.LENGTH_SHORT).show();
                    // ABRE A ACTIVITY PRINCIPAL E FECHA TODAS AS ANTERIORES, DE MODO A BLOQUEAR
                    // A NAVEGAÇAO DO USUARIO PELAS ACTIVITIES DE LOGIN
                    Intent it = new Intent(ContatoCadastramentoActivity.this, PrincipalActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(it);
                    finish();
                }
            }
        });

        mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(),edtSenha.getText().toString())
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(Constantes.TAG_LOG, "signInWithEmail:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Toast.makeText(ContatoCadastramentoActivity.this, "Não foi possivel realizar o login ",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void salvarContato(){
        Log.i(TAG_LOG,"Salvar Contato");
        if (contatoSelecionado==null) {
            contatoSelecionado = new Contato(true);
        }

        contatoSelecionado.setEmail(edtEmail.getText().toString());
        contatoSelecionado.setNome(edtNome.getText().toString());
        contatoSelecionado.setSobreNome(edtSobrenome.getText().toString());
        contatoSelecionado.setTelefone(edtTelefone.getText().toString());
        contatoSelecionado.setNomeSkype(edtNomeSkype.getText().toString());
        contatoSelecionado.setEndereco(edtEnderecoCompleto.getText().toString());
        contatoSelecionado.setCaminhoFoto(caminhoFoto);

        if(edtSenha.getVisibility() == View.VISIBLE){
            contatoSelecionado.setSenha(edtSenha.getText().toString());
        }

        // esses campos nao sao usados no cadastro
        // Mas o do face eu uso para pegar a foto do perfil
        //contatoSelecionado.setIdFacebook();
        //contatoSelecionado.setDataNascimento();

        ContatoRepositorio repo = new ContatoRepositorio();

        repo.addContato(contatoSelecionado, new IContatoRepositorio.OnSaveContatoCallback() {
            @Override
            public void onSuccess(Contato contato) {
                Toast.makeText(ContatoCadastramentoActivity.this,"Dados salvos com sucesso!",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(String message) {
                Toast.makeText(ContatoCadastramentoActivity.this, "Erro ao Salvar: " + message, Toast.LENGTH_LONG).show();
            }
        });

        repo.close();

        // So para dar tempo de ver que salvou
        try {
            Thread.sleep(1000);
        }catch (Exception e){

        }

        Intent it = new Intent(ContatoCadastramentoActivity.this, PrincipalActivity.class);
        startActivity(it);

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SimpleDraweeView imgFresco = (SimpleDraweeView) findViewById(R.id.imgListaContatoCadastro);

        if ( ((requestCode == SELECIONAR_FOTO) || (requestCode == TIRAR_FOTO)) && (resultCode == RESULT_OK) ) {

            if (data != null) {
                CropImage.activity(data.getData())
                        .setActivityTitle("Ajustar foto")
                        .setBorderLineColor(Color.RED)
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setGuidelines(CropImageView.Guidelines.ON).start(this);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                imgFresco.setImageURI(resultUri);

                InputStream stream = null;
                FileOutputStream out = null;

                try {

                    Log.i(TAG_LOG,"ID-FOTO-->"+getIntent().getStringExtra(Constantes.ID_CONTATO));
                    File file = createImageFile(getIntent().getStringExtra(Constantes.ID_CONTATO));
                    Log.i(TAG_LOG,"File Gerado-->" + file);
                    if (file == null) {
                        return;
                    }
                    Log.i(TAG_LOG,"File Gerado Caminho-->" + file.getAbsolutePath());

                    out = new FileOutputStream(file);
                    stream = getContentResolver().openInputStream(resultUri);

                    bitmapSELECIONADO = BitmapFactory.decodeStream(stream);

                    bitmapSELECIONADO.compress(Bitmap.CompressFormat.JPEG, 90, out);

                    caminhoFoto = file.getAbsolutePath();

                    out.flush();
                    out.close();

                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Erro ==> " + e, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    private File createImageFile(String idFoto) throws IOException {
        // Create an image file name
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(storageDir == null){
            storageDir = getApplicationContext().getFilesDir();
        }
        File image = File.createTempFile(
                idFoto,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

}