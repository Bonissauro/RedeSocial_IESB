package br.com.bonysoft.redesocial_iesb.modelo;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by carlospanarello on 04/09/16.
 */
public class Contato extends RealmObject {
    @PrimaryKey
    String id;
    String nome;
    String sobreNome;
    String telefone;
    String caminhoFoto;
    String email;
    Date dataNascimento;
    // aqui iremos definir o dono dos contratos
    // assim qdo sera possivel 2 pessoas diferentes logar no app e ter uma lista de contatos propria.
    @Index
    String id_usuario;
    boolean usuarioPrincipal;

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

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isUsuarioPrincipal() {
        return usuarioPrincipal;
    }

    public void setUsuarioPrincipal(boolean usuarioPrincipal) {
        this.usuarioPrincipal = usuarioPrincipal;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
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

    @Override
    public String toString() {
        return "Contato{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", sobreNome='" + sobreNome + '\'' +
                ", telefone='" + telefone + '\'' +
                ", caminhoFoto='" + caminhoFoto + '\'' +
                ", email='" + email + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", id_usuario='" + id_usuario + '\'' +
                ", usuarioPrincipal=" + usuarioPrincipal +
                '}';
    }
}
