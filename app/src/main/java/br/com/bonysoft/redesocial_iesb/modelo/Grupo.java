package br.com.bonysoft.redesocial_iesb.modelo;

import java.util.HashMap;
import java.util.Map;

public class Grupo {
 
    // TESTE 1
    public String id_dono;
    public String id_grupo;
    public String nome;

    public Grupo(String id_dono, String id_grupo, String nome) {
        this.id_dono = id_dono;
        this.id_grupo = id_grupo;
        this.nome = nome;
    }

    public String getId_dono() {
        return id_dono;
    }

    public void setId_dono(String id_dono) {
        this.id_dono = id_dono;
    }

    public String getId_grupo() {
        return id_grupo;
    }

    public void setId_grupo(String id_grupo) {
        this.id_grupo = id_grupo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("id_dono", id_dono);
        result.put("id_grupo", id_grupo);
        result.put("nome", nome);

        return result;

    }
}
