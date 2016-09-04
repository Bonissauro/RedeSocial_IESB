package br.com.bonysoft.redesocial_iesb.modelo;

import java.util.HashMap;
import java.util.Map;

public class Necessidade {

    public String id_dono;
    public String id_produto;
    public String nome;
    public String unidade;
    public Grupo grupo;

    public Necessidade(String id_dono, String id_produto, String nome, String unidade, Grupo grupo) {
        this.id_dono = id_dono;
        this.id_produto = id_produto;
        this.nome = nome;
        this.unidade = unidade;
        this.grupo = grupo;
    }

    public String getId_dono() {
        return id_dono;
    }

    public void setId_dono(String id_dono) {
        this.id_dono = id_dono;
    }

    public String getId_produto() {
        return id_produto;
    }

    public void setId_produto(String id_produto) {
        this.id_produto = id_produto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("id_dono", id_dono);
        result.put("id_produto", id_produto);
        result.put("nome", nome);
        result.put("unidade", unidade);
        result.put("grupo", grupo);

        return result;

    }

}
