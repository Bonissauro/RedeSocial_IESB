package br.com.bonysoft.redesocial_iesb.realm.modulo;

import br.com.bonysoft.redesocial_iesb.modelo.BluetoothPareado;
import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.modelo.LocalizacaoContatos;
import br.com.bonysoft.redesocial_iesb.modelo.MensagemRealm;
import io.realm.annotations.RealmModule;

@RealmModule(classes = {Contato.class, BluetoothPareado.class, LocalizacaoContatos.class, MensagemRealm.class})
public class RedeSocialRealmModule {
}
