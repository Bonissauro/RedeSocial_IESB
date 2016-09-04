package br.com.bonysoft.redesocial_iesb.servicos;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.bonysoft.redesocial_iesb.modelo.Usuario;

public class UsuarioService {

    public static String gravar(Usuario usuario){

        try{

            FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference objReferencia = database.getReference("usuarios");

            String id = objReferencia.child("usuarios").push().getKey();

            objReferencia.child(id).child("nome").setValue(usuario.getNome());
            objReferencia.child(id).child("email").setValue(usuario.getEmail());
            objReferencia.child(id).child("senha").setValue(usuario.getSenha());

            return id;

        }catch (Exception e){
            return null;

        }

    }

}
