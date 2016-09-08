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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imageformat.ImageFormat;
import com.facebook.imageformat.ImageFormatChecker;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.ContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.IContatoRepositorio;

public class ContatoCadastramentoActivity extends AppCompatActivity {

    private static final int TIRAR_FOTO = 1972;
    private static final int SELECIONAR_FOTO = 1973;

    private Bitmap bitmapSELECIONADO;

    private boolean fotoAlterada;

    Contato contatoSelecionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contato_cadastramento);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                    edtSobrenome.setText(contatoSelecionado.getSobreNome());
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
        final EditText edtSobrenome = (EditText) findViewById(R.id.edtSobrenome);
        final EditText edtTelefone = (EditText) findViewById(R.id.edtTelefone);
        final EditText edtNomeSkype = (EditText) findViewById(R.id.edtNomeSkype);
        final EditText edtEnderecoCompleto = (EditText) findViewById(R.id.edtEndereco);

        IContatoRepositorio contatoRepositorio = new ContatoRepositorio();

        Contato contato = new Contato();

        if (contatoSelecionado!=null) {
            contato.setId(contatoSelecionado.getId());
        }

        if(fotoAlterada){
            String caminho = Environment.getExternalStorageDirectory()
                    + "/img/perfil_"+ contato.getId() +".jpg";

            File file = new File(caminho );

            Toast.makeText(this, "path: " + caminho, Toast.LENGTH_LONG).show();
            SimpleDraweeView imgFresco = (SimpleDraweeView) findViewById(R.id.imgListaContatoCadastro);

            try {
                /*
                ImageRequest downloadRequest = ImageRequest.fromUri("");


                CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(downloadRequest);
                if (ImagePipelineFactory.getInstance().getMainDiskStorageCache().hasKey(cacheKey)) {
                    BinaryResource resource = ImagePipelineFactory.getInstance().getMainDiskStorageCache().getResource(cacheKey);
                    File cacheFile = ((FileBinaryResource) resource).getFile();
                    FileInputStream fis = new FileInputStream(cacheFile);
                    ImageFormat imageFormat = ImageFormatChecker.getImageFormat(fis);

                    InputStream initialStream = fis;
                    byte[] buffer = new byte[initialStream.available()];
                    initialStream.read(buffer);

                    OutputStream outStream = new FileOutputStream(file);
                    outStream.write(buffer);

                }*/
            }catch (Exception e){
                e.printStackTrace();

            }
        }

        contato.setEmail(edtEmail.getText().toString());
        contato.setNome(edtNome.getText().toString());
        contato.setSobreNome(edtSobrenome.getText().toString());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ImageView imagem = (ImageView) findViewById(R.id.imgFoto);
        SimpleDraweeView imgFresco = (SimpleDraweeView) findViewById(R.id.imgListaContatoCadastro);
        fotoAlterada = false;
        if ( ((requestCode == SELECIONAR_FOTO) || (requestCode == TIRAR_FOTO)) && (resultCode == RESULT_OK) ) {

            if (data != null) {

                CropImage.activity(data.getData())
                        .setActivityTitle("Ajustar foto")
                        .setBorderLineColor(Color.RED)
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setGuidelines(CropImageView.Guidelines.ON).start(this);

            }

        }

        /*
        if ((requestCode == SELECIONAR_FOTO) &&(resultCode == RESULT_OK)) {

            if (data != null) {
                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }

        }
        */

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            InputStream stream = null;

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                try {

                    stream = getContentResolver().openInputStream(resultUri);

                    bitmapSELECIONADO = BitmapFactory.decodeStream(stream);

                    imagem.setImageBitmap(bitmapSELECIONADO);

                    fotoAlterada = true;

                    imgFresco.setImageURI(resultUri);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                } finally {
                    if (stream != null)
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

}