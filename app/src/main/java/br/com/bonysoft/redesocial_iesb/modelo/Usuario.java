package br.com.bonysoft.redesocial_iesb.modelo;

import java.util.HashMap;
import java.util.Map;

public class Usuario  {

    public String id_usuario;
    public String nome;
    public String email;
    public String senha;

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("id_usuario", id_usuario);
        result.put("nome", nome);
        result.put("email", email);
        result.put("senha", senha);

        return result;

    }

}
