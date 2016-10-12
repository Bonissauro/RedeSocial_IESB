package br.com.bonysoft.redesocial_iesb.realm.modulo;

import br.com.bonysoft.redesocial_iesb.modelo.BluetoothPareado;
import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.modelo.Localizacao;
import br.com.bonysoft.redesocial_iesb.modelo.Mensagem;
import io.realm.annotations.RealmModule;

@RealmModule(classes = {Contato.class, BluetoothPareado.class, Localizacao.class, Mensagem.class})
public class RedeSocialRealmModule {
}
