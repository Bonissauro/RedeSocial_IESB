package br.com.bonysoft.redesocial_iesb.servicos;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;

public class ContatoService {
//TODO essa classe nao esta sendo usada pode ir para o saco?
    public static String gravar(Contato contato){

        try{

            FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference objReferencia = database.getReference("contatos");

            String id = objReferencia.child("contatos").push().getKey();

            objReferencia.child(id).child("nome").setValue(contato.getNome());
            objReferencia.child(id).child("email").setValue(contato.getEmail());
            //objReferencia.child(id).child("senha").setValue(usuario.getSenha());

            return id;

        }catch (Exception e){
            return null;
        }
    }
}
