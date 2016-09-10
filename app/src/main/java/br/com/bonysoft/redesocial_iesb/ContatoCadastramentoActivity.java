package br.com.bonysoft.redesocial_iesb;

        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Color;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Environment;
        import android.provider.MediaStore;
        import android.support.design.widget.FloatingActionButton;
        import android.support.v4.content.FileProvider;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import com.facebook.drawee.view.SimpleDraweeView;
        import com.theartofdev.edmodo.cropper.CropImage;
        import com.theartofdev.edmodo.cropper.CropImageView;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.text.SimpleDateFormat;
        import java.util.Date;

        import br.com.bonysoft.redesocial_iesb.modelo.Contato;
        import br.com.bonysoft.redesocial_iesb.realm.repositorio.ContatoRepositorio;
        import br.com.bonysoft.redesocial_iesb.realm.repositorio.IContatoRepositorio;

public class ContatoCadastramentoActivity extends AppCompatActivity {

    private static final int TIRAR_FOTO = 1972;
    private static final int SELECIONAR_FOTO = 1973;
    private Bitmap bitmapSELECIONADO;
    private Contato contatoSelecionado = null;
    private boolean novo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contato_cadastramento);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SimpleDraweeView imgFresco = (SimpleDraweeView) findViewById(R.id.imgListaContatoCadastro);

        imgFresco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, TIRAR_FOTO);
            }
        });

        Button btnCamera = (Button) findViewById(R.id.btnTiraFoto);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, TIRAR_FOTO);

            }
        });

        Button btnSelecionaFoto = (Button) findViewById(R.id.btnSelecionaFoto);
        btnSelecionaFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,SELECIONAR_FOTO);

            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gravaContato();
            }
        });

        final EditText edtEmail = (EditText) findViewById(R.id.edtEmail);
        final EditText edtNome = (EditText) findViewById(R.id.edtNome);
        final EditText edtSobrenome = (EditText) findViewById(R.id.edtSobrenome);
        final EditText edtTelefone = (EditText) findViewById(R.id.edtTelefone);
        final EditText edtNomeSkype = (EditText) findViewById(R.id.edtNomeSkype);
        final EditText edtEnderecoCompleto = (EditText) findViewById(R.id.edtEndereco);

        final SimpleDraweeView  sdvFoto = (SimpleDraweeView) findViewById(R.id.imgListaContatoCadastro);

        contatoSelecionado = (Contato) getIntent().getSerializableExtra(Constantes.NOVO);
        String id = getIntent().getStringExtra(Constantes.id);

        if(contatoSelecionado == null){
            novo = false;
        } else {
            novo = true;
        }

        if(id != null && !id.isEmpty()){
            setTitle("Alteração de contato");

            IContatoRepositorio contatoRepositorio = new ContatoRepositorio();
            contatoRepositorio.getContatoById(id, new IContatoRepositorio.OnGetContatoByIdCallback() {

                @Override
                public void onSuccess(Contato contato) {
                    contatoSelecionado = contato;
                }

                @Override
                public void onError(String message) {
                }
            });
        }

        if(contatoSelecionado != null){
            edtEmail.setText(contatoSelecionado.getEmail());
            edtNome.setText(contatoSelecionado.getNome());
            edtSobrenome.setText(contatoSelecionado.getSobreNome());
            edtTelefone.setText(contatoSelecionado.getTelefone());
            edtNomeSkype.setText(contatoSelecionado.getNomeSkype());
            edtEnderecoCompleto.setText(contatoSelecionado.getEndereco());
            if(contatoSelecionado.getCaminhoFoto()!=null && !contatoSelecionado.getCaminhoFoto().isEmpty()) {
                Uri imageUri = Uri.parse(contatoSelecionado.getCaminhoFoto());
                Log.i("ContatoLog","Caminho do Item na Alteracao " + imageUri.toString() );

                sdvFoto.setImageURI(imageUri);
            }
        } else {
            setTitle("Novo contato");
            contatoSelecionado = new Contato();
        }
    }

    private void gravaContato() {

        final EditText edtEmail = (EditText) findViewById(R.id.edtEmail);
        final EditText edtNome = (EditText) findViewById(R.id.edtNome);
        final EditText edtSobrenome = (EditText) findViewById(R.id.edtSobrenome);
        final EditText edtTelefone = (EditText) findViewById(R.id.edtTelefone);
        final EditText edtNomeSkype = (EditText) findViewById(R.id.edtNomeSkype);
        final EditText edtEnderecoCompleto = (EditText) findViewById(R.id.edtEndereco);

        IContatoRepositorio contatoRepositorio = new ContatoRepositorio();

        Contato contato = new Contato();

        if (contatoSelecionado!=null) {
            contato.setId(contatoSelecionado.getId());
            contato.setCaminhoFoto(contatoSelecionado.getCaminhoFoto());
        }

        Log.i("ContatoLog","Caminho do Item na Salva " +  contato.getCaminhoFoto() );

        contato.setEmail(edtEmail.getText().toString());
        contato.setNome(edtNome.getText().toString());
        contato.setSobreNome(edtSobrenome.getText().toString());
        contato.setTelefone(edtTelefone.getText().toString());
        contato.setNomeSkype(edtNomeSkype.getText().toString());
        contato.setEndereco(edtEnderecoCompleto.getText().toString());

        if (contato.getId() == null){

            contatoRepositorio.addContato( contato, new IContatoRepositorio.OnSaveContatoCallback() {

                @Override
                public void onSuccess(Contato contato) {

                    Toast.makeText(getBaseContext(), "Sucesso na Gravacao "
                            , Toast.LENGTH_LONG).show();
                    if(novo){
                        Intent it = new Intent(ContatoCadastramentoActivity.this, PrincipalActivity.class);
                        startActivity(it);
                    }
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
                public void onSuccess(Contato contato) {
                    Toast.makeText(getBaseContext(), "Sucesso na alteração " , Toast.LENGTH_LONG).show();
                    if(novo){
                        Intent it = new Intent(ContatoCadastramentoActivity.this, PrincipalActivity.class);
                        startActivity(it);
                    }
                    finish();
                }


                @Override
                public void onError(String message) {
                    Toast.makeText(getBaseContext(), "Erro ==> " + message, Toast.LENGTH_LONG).show();
                }

            });
        }
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
                    File file = createImageFile();

                    if (file == null) {
                        return;
                    }
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            file);

                    out = new FileOutputStream(file);
                    stream = getContentResolver().openInputStream(resultUri);
                    bitmapSELECIONADO = BitmapFactory.decodeStream(stream);

                    bitmapSELECIONADO.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    contatoSelecionado.setCaminhoFoto(file.getAbsolutePath());

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


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


}