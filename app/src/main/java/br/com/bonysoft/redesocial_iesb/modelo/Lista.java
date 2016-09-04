package br.com.bonysoft.redesocial_iesb.modelo;

import java.util.HashMap;
import java.util.Map;

public class Lista {

    // TESTE DE COMMIT Pelo Android Studio
    public String id_lista;
    public String id_lista2;
    public String id_dono;
    public String data_criacao;
    public String data_ultima_atualizacao;
    public String data_compra;

    public Lista(String id_lista, String id_dono, String data_criacao, String data_ultima_atualizacao, String data_compra) {
        this.id_lista = id_lista;
        this.id_dono = id_dono;
        this.data_criacao = data_criacao;
        this.data_ultima_atualizacao = data_ultima_atualizacao;
        this.data_compra = data_compra;
    }

    public String getId_lista() {
        return id_lista;
    }

    public void setId_lista(String id_lista) {
        this.id_lista = id_lista;
    }

    public String getId_dono() {
        return id_dono;
    }

    public void setId_dono(String id_dono) {
        this.id_dono = id_dono;
    }

    public String getData_criacao() {
        return data_criacao;
    }

    public void setData_criacao(String data_criacao) {
        this.data_criacao = data_criacao;
    }

    public String getData_ultima_atualizacao() {
        return data_ultima_atualizacao;
    }

    public void setData_ultima_atualizacao(String data_ultima_atualizacao) {
        this.data_ultima_atualizacao = data_ultima_atualizacao;
    }

    public String getData_compra() {
        return data_compra;
    }

    public void setData_compra(String data_compra) {
        this.data_compra = data_compra;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("id_lista", id_lista);
        result.put("id_dono", id_dono);
        result.put("data_criacao", data_criacao);
        result.put("data_ultima_atualizacao", data_ultima_atualizacao);
        result.put("data_compra", data_compra);

        return result;

    }

}
