package br.com.bonysoft.redesocial_iesb;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.bonysoft.redesocial_iesb.modelo.BluetoothPareado;
import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.BluetoothPareadoRepositorio;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.ContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.IBluetoothPareadoRepositorio;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.IContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.utilitarios.ComunicadorBluethooth;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;
import io.realm.RealmResults;

public class BluetoothSelecaoActivity extends AppCompatActivity {

    final static String VK_MENSAGEM_EXISTE_UM_APARELHO         = "Só existe um aparelho Bluetooth conhecido. Selecione-o agora, se achar conveniente, ou clique em \"Refresh\", logo abaixo, para efetuar uma nova pesquisa.";
    final static String VK_MENSAGEM_EXISTE_MAIS_DE_UM_APARELHO = "Estes são os aparelhos já pareados via bluetooth com o seu! Selecione um deles para conectar ou clique em \"Refresh\", logo abaixo, para efetuar uma nova pesquisa.";
    final static String VK_MENSAGEM_NAO_EXISTE_APARELHO        = "Não há aparelhos Bluetooth conhecidos. Clique no botão \"Refresh\", logo abaixo, para pesquisar os aparelhos próximos neste momento.";

    String TAG_LOG = "BLUETOOTH1";//Constantes.TAG_LOG;

    List<BluetoothPareado> listaAparelhos;

    BluetoothAdapter btAdapter;
    Button btnEnvioCartao ;
    Contato enviarContato;
    TextView txtTitulo;
    String endereco;

    ProgressDialog dialog;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bluetooth_selecao);

        setTitle("Aparelhos conhecidos");

        txtTitulo = (TextView) findViewById(R.id.idBluetoothSelecao_textoTitulo);
        btnEnvioCartao = (Button)  findViewById(R.id.btnEnviarCartao);
        //btnEnvioCartao.setEnabled(false);

        listaAparelhos = new ArrayList<BluetoothPareado>();

        /*
        listaAparelhos.add(new BluetoothPareado("iPhone do Boni"       ,"234-435-234-554-677"));
        listaAparelhos.add(new BluetoothPareado("Galaxy da Aninha"     ,"544-235-667-434-678"));
        listaAparelhos.add(new BluetoothPareado("Windows 10 da Bárbara","154-225-912-132-912"));
        listaAparelhos.add(new BluetoothPareado("Sony da Lili"         ,"051-962-922-732-821"));
        */

        fab = (FloatingActionButton) findViewById(R.id.fab_bluetooth_selecao);

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog = ProgressDialog.show(v.getContext(), "Aguarde", "Buscando aparelhos...", true);

                buscarNovosAparelhosProximos();

            }

        });

        if(!checkPermissaoBluetooth()){
           Log.i(TAG_LOG,"SEM PERMISSAO");
           Toast.makeText(getApplicationContext(),"Sem permissao concedida nao e possivel acessar essa tela",Toast.LENGTH_LONG).show();
        }else{

        }
    }
    //TODO rever essa logica de acesso aqui
    private boolean checkPermissaoBluetooth(){
        int hasPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(hasPermission == PackageManager.PERMISSION_GRANTED){
           // discoveryBluetooth();
            continueProcessoBluetooth();
            return true;
        } else {
            ActivityCompat.requestPermissions(BluetoothSelecaoActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, Constantes.REQUEST_COARSE_LOCATION_PERMISSIONS);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == Constantes.REQUEST_COARSE_LOCATION_PERMISSIONS){
            boolean resposta = true;
            for(int r:grantResults){
                if(r == PackageManager.PERMISSION_DENIED){
                    resposta = false;
                    break;
                }
            }

            if(resposta){
                continueProcessoBluetooth();
            }
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void continueProcessoBluetooth(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        for (BluetoothDevice btd:pairedDevices){

            BluetoothPareado btp = new BluetoothPareado(btd.getName(), btd.getAddress());

            listaAparelhos.add(btp);

        }

        IntentFilter it = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        it.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        it.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(receiver, it);

        dialog = ProgressDialog.show(this, "Aguarde", "Buscando aparelhos...", true);

        BluetoothPareadoRepositorio repositorio = new BluetoothPareadoRepositorio();

        repositorio.getAll(new IBluetoothPareadoRepositorio.OnGetAllCallback() {

            @Override
            public void onSuccess(RealmResults<BluetoothPareado> lista) {

                listaAparelhos = lista;
                //Ja cria um socket para comunicacao
                ComunicadorBluethooth.getInstance().start();
                montaRadioGroup();

                btnEnvioCartao.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        envioCartao();
                    }
                });

                dialog.dismiss();

            }

            @Override
            public void onError(String message) {

                dialog.dismiss();

                Toast.makeText(getBaseContext(), "Erro: "+message, Toast.LENGTH_LONG).show();

            }

        });
    }

    public void apagar(View v){
        BluetoothPareadoRepositorio repo = new BluetoothPareadoRepositorio();

        repo.deleteAll(new IBluetoothPareadoRepositorio.OnDeleteCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(BluetoothSelecaoActivity.this,"Excluidos",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(BluetoothSelecaoActivity.this,"ERRO-->" + message,Toast.LENGTH_LONG).show();
            }
        });


    }

    private void montaRadioGroup() {

        final RadioGroup rdgrp = (RadioGroup) findViewById(R.id.idBluetoothSelecao_radiogroup);
        rdgrp.removeAllViews();

        if (listaAparelhos.size()==0){

            rdgrp.setVisibility(View.GONE);
            txtTitulo.setText(VK_MENSAGEM_NAO_EXISTE_APARELHO);

        }else{
            Log.i(TAG_LOG,"Tamanho"+ listaAparelhos.size());

            RadioButton rdbtnNenhum = new RadioButton(this);
            rdbtnNenhum.setId(0);
            rdbtnNenhum.setText("Nenhum");
            rdgrp.addView(rdbtnNenhum);
            rdbtnNenhum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.i(TAG_LOG,"Clicou no option Nenhum -->"+isChecked);
                    if(isChecked){
                        //ComunicadorBluethooth.getInstance().disconnect();
                        //btnEnvioCartao.setEnabled(false);
                    }
                }
            });

            for(int idx = 1 ;idx < listaAparelhos.size()+1;idx++ ){

                RadioButton rdbtn = new RadioButton(this);

                Log.i(TAG_LOG,"Indice"+ (idx-1));
                //Não sei pq so sei que eh assim funciona :0 , estranho né
                rdbtn.setId(idx-(-1+1));
                rdbtn.setText(listaAparelhos.get(idx-1).getNome());
                rdgrp.addView(rdbtn);
                Log.i(TAG_LOG,"Item"+ listaAparelhos.get(idx-1).getNome());
                rdbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Log.i(TAG_LOG,"Clicou no option -->"+isChecked);
                        if(isChecked){
                            endereco = listaAparelhos.get(buttonView.getId()-1).getEndereco();

                            Log.i(TAG_LOG,"Indice --> "+ buttonView.getId());
                            Log.i(TAG_LOG,"Nome --> "+ listaAparelhos.get(buttonView.getId()-1).getNome());
                            Log.i(TAG_LOG,"Endereco --> "+ listaAparelhos.get(buttonView.getId()-1).getEndereco());
                            
                            
                        }else{
                            //ComunicadorBluethooth.getInstance().disconnect();
                        }

                    //btnEnvioCartao.setEnabled(isChecked);

                    }
                });

            }

            rdgrp.setVisibility(View.VISIBLE);

            if (listaAparelhos.size()==1){
                txtTitulo.setText(VK_MENSAGEM_EXISTE_UM_APARELHO);
            }else {
                txtTitulo.setText(VK_MENSAGEM_EXISTE_MAIS_DE_UM_APARELHO);
            }

        }

        dialog.dismiss();

    }

    private void envioCartao(){
        //String id = getIntent().getStringExtra(Constantes.ID_USUARIO_LOGADO);
        Log.i(TAG_LOG,"Envio de Cartao");
        
        Log.i(TAG_LOG,"Endereco --> "+ endereco);
        if(endereco != null && !endereco.trim().isEmpty()){
            ComunicadorBluethooth.getInstance().connect(endereco);
            ComunicadorBluethooth.getInstance().send("ID_USUARIO-->"+ 234123 +"<--"+ Constantes.FIM_TRANSMISSAO);
        }
        
        /*
        ContatoRepositorio repo = new ContatoRepositorio();
        repo.getContatoById(id, new IContatoRepositorio.OnGetContato() {
            @Override
            public void onSuccess(Contato contato) {

                ComunicadorBluethooth.getInstance().send(contato.nomeCompleto()+ Constantes.FIM_TRANSMISSAO);
            }

            @Override
            public void onError(String message) {

            }
        });
*/

    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){

                listaAparelhos = new ArrayList<BluetoothPareado>();

            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){

                montaRadioGroup();

            }else if (BluetoothDevice.ACTION_FOUND.equals(action)){

                BluetoothDevice btd = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                BluetoothPareado btp = new BluetoothPareado(btd.getName(), btd.getAddress());

                adicionaNovoBluetoothPareado(btp);

                listaAparelhos.add(btp);

            }

        }

    };

    private void buscarNovosAparelhosProximos() {

        BluetoothPareadoRepositorio repositorio = new BluetoothPareadoRepositorio();

        repositorio.deleteAll(new IBluetoothPareadoRepositorio.OnDeleteCallback() {

            @Override
            public void onSuccess() {

                btAdapter = BluetoothAdapter.getDefaultAdapter();
                btAdapter.startDiscovery();

            }

            @Override
            public void onError(String message) {

                dialog.dismiss();

                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();

            }

        });

    }

    private void adicionaNovoBluetoothPareado(BluetoothPareado objeto){

        BluetoothPareadoRepositorio repositorio = new BluetoothPareadoRepositorio();

        repositorio.edit( objeto, new IBluetoothPareadoRepositorio.OnSaveCallback() {

            @Override
            public void onSuccess(BluetoothPareado objeto) {

                Toast.makeText(getBaseContext(), "Aparelho "+objeto.getNome()+" adicionado!", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(String message) {
                Toast.makeText(getBaseContext(), "Erro ==> " + message, Toast.LENGTH_LONG).show();
            }

        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}
