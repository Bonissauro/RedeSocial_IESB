package br.com.bonysoft.redesocial_iesb.realm.modulo;

import android.app.Application;
import android.content.Intent;
import com.facebook.drawee.backends.pipeline.Fresco;

import br.com.bonysoft.redesocial_iesb.servicos.AlarmeEnvioPosicaoService;
import br.com.bonysoft.redesocial_iesb.servicos.ObtemLocalizacaoContatoService;
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

    public static RedeSocialRealm getInstance() {
        return instance;
    }
}
