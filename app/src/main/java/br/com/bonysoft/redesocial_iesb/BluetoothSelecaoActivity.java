package br.com.bonysoft.redesocial_iesb;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.bonysoft.redesocial_iesb.modelo.BluetoothPareado;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.BluetoothPareadoRepositorio;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.IBluetoothPareadoRepositorio;
import io.realm.RealmResults;

public class BluetoothSelecaoActivity extends AppCompatActivity {

    final static String VK_MENSAGEM_EXISTE_UM_APARELHO         = "Só existe um aparelho Bluetooth conhecido. Selecione-o agora, se achar conveniente, ou clique em \"Refresh\", logo abaixo, para efetuar uma nova pesquisa.";
    final static String VK_MENSAGEM_EXISTE_MAIS_DE_UM_APARELHO = "Estes são os aparelhos já pareados via bluetooth com o seu! Selecione um deles para conectar ou clique em \"Refresh\", logo abaixo, para efetuar uma nova pesquisa.";
    final static String VK_MENSAGEM_NAO_EXISTE_APARELHO        = "Não há aparelhos Bluetooth conhecidos. Clique no botão \"Refresh\", logo abaixo, para pesquisar os aparelhos próximos neste momento.";

    List<BluetoothPareado> listaAparelhos;

    BluetoothAdapter btAdapter;


    TextView txtTitulo;

    ProgressDialog dialog;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bluetooth_selecao);

        setTitle("Aparelhos conhecidos");

        txtTitulo = (TextView) findViewById(R.id.idBluetoothSelecao_textoTitulo);

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

                montaRadioGroup();

                dialog.dismiss();

            }

            @Override
            public void onError(String message) {

                dialog.dismiss();

                Toast.makeText(getBaseContext(), "Erro: "+message, Toast.LENGTH_LONG).show();

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

            int posicao=0;

            for (BluetoothPareado o : listaAparelhos){

                RadioButton rdbtn = new RadioButton(this);

                rdbtn.setId(posicao++);
                rdbtn.setText(o.getNome());
                rdgrp.addView(rdbtn);

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

        repositorio.add( objeto, new IBluetoothPareadoRepositorio.OnSaveCallback() {

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

}
