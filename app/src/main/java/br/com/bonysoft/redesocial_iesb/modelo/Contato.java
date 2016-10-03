package br.com.bonysoft.redesocial_iesb.modelo;

import android.os.Environment;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by carlospanarello on 04/09/16.
 */
public class Contato extends RealmObject implements Serializable{

    @PrimaryKey
    String id;
    String nome;
    String sobreNome;
    String telefone;
    String caminhoFoto;
    String email;
    String nomeSkype;
    String endereco;
    Date dataNascimento;
    String senha;
    String idFacebook;

    // aqui iremos definir o dono dos contatos
    // assim qdo sera possivel 2 pessoas diferentes logar no app e ter uma lista de contatos propria.
    /*@Index
    String id_usuario;
    boolean usuarioPrincipal;
    */
    public Contato() {
    }

    public Contato(boolean gerarID) {
        if(gerarID){
            this.setId(UUID.randomUUID().toString());
        }
    }

    public Contato(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobreNome() {
        return sobreNome;
    }

    public void setSobreNome(String sobreNome) {
        this.sobreNome = sobreNome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCaminhoFoto() {
       return caminhoFoto;
    }

    public void setCaminhoFoto(String caminho){
        caminhoFoto = caminho;
    }


    public String getCaminhoFotoFacebook(){
        return  "https://graph.facebook.com/" + getIdFacebook() + "/picture?width=200&height=150";
    }


    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if(email != null)
        this.email = email.toLowerCase();
        else this.email = null;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeSkype() {
        return nomeSkype;
    }

    public void setNomeSkype(String nomeSkype) {
        this.nomeSkype = nomeSkype;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String nomeCompleto(){
        if(this.sobreNome != null){
            return nome + " "+ sobreNome;
        }
        return nome;
    }

    public String getIdFacebook() {
        return idFacebook;
    }

    public void setIdFacebook(String idFacebook) {
        this.idFacebook = idFacebook;
    }

    public Contato copy(){
        Contato c = new Contato();
        c.setIdFacebook(this.getIdFacebook());
        c.setId(this.getId());
        c.setSenha(this.getSenha());
        c.setSobreNome(this.getSobreNome());
        c.setEmail(this.getEmail());
        c.setNome(this.getNome());
        c.setCaminhoFoto(this.getCaminhoFoto());
        c.setDataNascimento(this.getDataNascimento());
        c.setEndereco(this.getEndereco());
        c.setTelefone(this.getTelefone());
        c.setNomeSkype(this.getNomeSkype());
        return c;
    }

    @Override
    public String toString() {
        return "Contato{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", sobreNome='" + sobreNome + '\'' +
                ", telefone='" + telefone + '\'' +
                ", caminhoFoto='" + caminhoFoto + '\'' +
                ", email='" + email + '\'' +
                ", nomeSkype='" + nomeSkype + '\'' +
                ", endereco='" + endereco + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", senha='" + senha + '\'' +
                ", idFacebook='" + idFacebook + '\'' +
                '}';
    }
}
