package br.com.bonysoft.redesocial_iesb.modelo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BluetoothPareado extends RealmObject {

    @PrimaryKey
    public String endereco;

    public String nome;

    public BluetoothPareado() {
    }

    public BluetoothPareado(String nome, String endereco) {
        this.endereco = endereco;
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public BluetoothPareado copy(){
        BluetoothPareado b = new BluetoothPareado();

        b.setEndereco(this.getEndereco());
        b.setNome(this.getNome());

        return b;
    }
}
