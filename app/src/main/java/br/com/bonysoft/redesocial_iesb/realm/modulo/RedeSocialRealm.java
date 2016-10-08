package br.com.bonysoft.redesocial_iesb.realm.modulo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.bonysoft.redesocial_iesb.modelo.LocalizacaoFireBase;
import br.com.bonysoft.redesocial_iesb.servicos.AlarmeEnvioPosicaoService;
import br.com.bonysoft.redesocial_iesb.servicos.ObtemLocalizacaoContatoService;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RedeSocialRealm extends Application {

    private static RedeSocialRealm instance;

    @Override
    public void onCreate() {

        super.onCreate();

        instance = this;

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .modules(new RedeSocialRealmModule())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        Fresco.initialize(this);

        iniciaServico();
        //TODO voltar isso aqui qdo habilitar o servico
        /*
        //Cria um alarme para enviar a posicao a cada 2 min para o firebase
        Intent envioPosicao = new Intent(getApplicationContext(), AlarmeEnvioPosicaoService.class);
        getApplicationContext().startService(envioPosicao);

        //Criar um servico para atualizar as localizaçoes dos amigos
        Intent obtemPosicoes = new Intent(getApplicationContext(), ObtemLocalizacaoContatoService.class);
        getApplicationContext().startService(obtemPosicoes);
        */


    }

    private void iniciaServico(){
        SharedPreferences sharedPref = this.getSharedPreferences("servico_executando",Context.MODE_PRIVATE);
        boolean executando = sharedPref.getBoolean("servico_executando",false);

        if(!executando){
            Intent startServiceIntent = new Intent(getApplicationContext(), AlarmeEnvioPosicaoService.class);
            getApplicationContext().startService(startServiceIntent);
        }

    }


    //Como eu posso colocar a propriedade email como clausula da consulta se eu nao sei
    // a porra do id do elemento ???
    private void getTeste() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("localizacao");
        ref.orderByChild("email").equalTo("bonissauro1@gmail.com").addListenerForSingleValueEvent(new ValueEventListener() {
        //ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot c : dataSnapshot.getChildren()) {
                    //Contact conctact = c.getValue(Contact.class);
                    Log.i(Constantes.TAG_LOG, "Itens -->" + c);
                    LocalizacaoFireBase l = c.getValue(LocalizacaoFireBase.class);
                    Log.i(Constantes.TAG_LOG, "Itens 1 -->" + l);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.i(Constantes.TAG_LOG, "Erro dataBase -->" + error.getMessage());
                Log.i(Constantes.TAG_LOG, "Erro dataBase -->" + error.getDetails());
            }
        });
/*
        DatabaseReference objReferencia = FirebaseDatabase.getInstance().getReference("localizacao");

        objReferencia.child("email").equalTo("bonissauro1@gmail.com").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(Constantes.TAG_LOG, "Resultado do firebase -->" + dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        objReferencia.child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    LocalizacaoFireBase note = noteDataSnapshot.getValue(LocalizacaoFireBase.class);
                    Log.i(Constantes.TAG_LOG, "Resultado do firebase Itens-->" + note);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */
    }

    public static RedeSocialRealm getInstance() {
        return instance;
    }
}
