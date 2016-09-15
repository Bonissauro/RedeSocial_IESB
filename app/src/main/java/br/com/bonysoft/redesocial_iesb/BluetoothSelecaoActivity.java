package br.com.bonysoft.redesocial_iesb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import br.com.bonysoft.redesocial_iesb.modelo.BluetoothPareado;

public class BluetoothSelecaoActivity extends AppCompatActivity {

    List<BluetoothPareado> listaAparelhos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bluetooth_selecao);

        setTitle("Aparelhos conectados");

        listaAparelhos = new ArrayList<BluetoothPareado>();

        listaAparelhos.add(new BluetoothPareado("iPhone do Boni"       ,"234-435-234-554-677"));
        listaAparelhos.add(new BluetoothPareado("Galaxy da Aninha"     ,"544-235-667-434-678"));
        listaAparelhos.add(new BluetoothPareado("Windows 10 da Bárbara","154-225-912-132-912"));
        listaAparelhos.add(new BluetoothPareado("Sony da Lili"         ,"051-962-922-732-821"));

        final RadioGroup rdgrp = (RadioGroup) findViewById(R.id.idBluetoothSelecao_radiogroup);

        int posicao=0;

        for (BluetoothPareado o : listaAparelhos){

            RadioButton rdbtn = new RadioButton(this);

            rdbtn.setId(posicao++);
            rdbtn.setText(o.getNome());
            rdgrp.addView(rdbtn);

        }

    }

}
