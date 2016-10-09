package br.com.bonysoft.redesocial_iesb;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.modelo.ContatoUltimaMsg;
import br.com.bonysoft.redesocial_iesb.modelo.Mensagem;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.ContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnListFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListaConversasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaConversasFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    String emailUsuario;

    public ListaConversasFragment() {
    }

    public static ListaConversasFragment newInstance() {
        ListaConversasFragment fragment = new ListaConversasFragment();

        return fragment;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contato_list, container, false);

        ContatoRepositorio repo = new ContatoRepositorio();
        emailUsuario = repo.buscaEmailUsuarioLogado();
        repo.close();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            List<ContatoUltimaMsg> lista = buscarContatoMsg(((PrincipalActivity) getActivity()).listaContatos);

           ((PrincipalActivity) getActivity()).contatoMensagemRecyclerViewAdapter = new ContatoMensagemRecyclerViewAdapter
                   (lista, mListener,(PrincipalActivity) getActivity());

           recyclerView.setAdapter(((PrincipalActivity) getActivity()).contatoMensagemRecyclerViewAdapter);
        }

        return view;
    }


    private List<ContatoUltimaMsg> buscarContatoMsg(final List<Contato> contatos){
        final Map<String,ContatoUltimaMsg> retorno = new HashMap<>();

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("mensagens");

        // or(de = emailContato and para = emailUsuario) ???
        ref.orderByChild("timestamp").equalTo(emailUsuario,"para")
                   .addListenerForSingleValueEvent(
                           new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.i(Constantes.TAG_LOG, "Resultado do firebase Chat-->" + dataSnapshot);

                                DataSnapshot dataSnapshotFilho = null;

                                Iterator<DataSnapshot> e = dataSnapshot.getChildren().iterator();

                                if(e.hasNext()){
                                    retorno.clear();
                                }

                                while (e.hasNext()){
                                    dataSnapshotFilho = e.next();

                                    Mensagem m = dataSnapshotFilho.getValue(Mensagem.class);

                                    if(m!=null) {
                                        ContatoUltimaMsg cm;
                                        Contato c=null;

                                        for(Contato cont: contatos){
                                            if(m.de.equalsIgnoreCase(cont.getEmail())){

                                                ContatoUltimaMsg ultima = null;
                                                if(retorno.containsKey(cont.getEmail())){
                                                    ultima = retorno.get(cont.getEmail());
                                                    ultima.setUltimaMensagem(m);
                                                }

                                                if(ultima==null){
                                                    ultima = new ContatoUltimaMsg(c,m);
                                                }
                                                retorno.put(cont.getEmail(),ultima);
                                            }
                                        }
                                    } else {
                                        Log.i(Constantes.TAG_LOG,"Mensagem NULL");
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.i(Constantes.TAG_LOG, "Erro dataBase Chat -->" + databaseError.getMessage());
                                Log.i(Constantes.TAG_LOG, "Erro dataBase Chat -->" + databaseError.getDetails());
                            }
                        }
                );

        return new ArrayList<>( retorno.values());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteractionContato(ContatoUltimaMsg item);
    }
}
