package br.com.bonysoft.redesocial_iesb;

import android.content.Context;
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
import java.util.Iterator;
import java.util.List;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.modelo.ContatoUltimaMsg;
import br.com.bonysoft.redesocial_iesb.modelo.Mensagem;

import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;
import io.realm.Realm;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnListFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListaConversasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaConversasFragment extends Fragment {

    final static String TAG = Constantes.TAG_LOG;
    private Realm mRealm;

    private OnListFragmentInteractionListener mListener;

    public ListaConversasFragment() {
    }

    public static ListaConversasFragment newInstance() {
        ListaConversasFragment fragment = new ListaConversasFragment();

        return fragment;
    }

    @Override
    public void onResume(){
        super.onResume();
        carregarMensagensNoRealm(ApplicationRedeSocial.getInstance().emailRegistrado());

        ((PrincipalActivity) getActivity()).contatoMensagemRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
    }

    public void carregarMensagensNoRealm(final String emailUsuario){
        if(emailUsuario == null || emailUsuario.trim().isEmpty()){
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("mensagens");
        ref.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Achou mensagens na lista de conversas");
                DataSnapshot dsnap = null;
                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();

                while (it.hasNext()){
                    dsnap = it.next();
                    Log.d(TAG, "onDataChange:DataSnapshot para salvar no Realm -->" + dsnap);
                    Mensagem msg = dsnap.getValue(Mensagem.class);
                    Log.d(TAG, "onDataChange:Mensagem para salvar no Realm -->" + msg);
                    if(msg!= null &&
                            (msg.getPara().equalsIgnoreCase(emailUsuario) || msg.getDe().equalsIgnoreCase(emailUsuario))){

                        Log.d(TAG, "onDataChange:Id Msg -->" + dsnap.getKey());
                        mRealm.beginTransaction();

                        msg.setId(dsnap.getKey());

                        mRealm.copyToRealmOrUpdate(msg);
                        mRealm.commitTransaction();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(Constantes.TAG_LOG,"Erro em salvar msg no realm");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lista_conversas, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();

            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            List<ContatoUltimaMsg> lista = buscaListaConversas(ApplicationRedeSocial.getInstance().emailRegistrado(), ((PrincipalActivity) getActivity()).listaContatos);

            ((PrincipalActivity) getActivity()).contatoMensagemRecyclerViewAdapter = new ContatoMensagemRecyclerViewAdapter
                    (lista, mListener,(PrincipalActivity) getActivity());

            recyclerView.setAdapter(((PrincipalActivity) getActivity()).contatoMensagemRecyclerViewAdapter);

        }
        return view;
    }

    private List<ContatoUltimaMsg> buscaListaConversas(final String emailUsuario,final List<Contato> contatos) {
        List<ContatoUltimaMsg> listaRetorno = new ArrayList<>();

        if(contatos != null) {
            for (Contato cont : contatos) {
                Log.d(TAG, "buscaListaConversas: Contato-->" + cont);

                Mensagem msg = mRealm.where(Mensagem.class)
                        .beginGroup()
                        .equalTo("de", emailUsuario).or().equalTo("para", emailUsuario)
                        .endGroup()
                        .beginGroup()
                        .equalTo("de", cont.getEmail()).or().equalTo("para", cont.getEmail())
                        .endGroup().findAllSorted("timestamp").last();

                Log.d(TAG, "buscaListaConversas: Mensagem-->" + msg);
                listaRetorno.add(new ContatoUltimaMsg(cont, msg));
            }
        }

        for(ContatoUltimaMsg c:listaRetorno){
            Log.d(TAG, "buscaListaConversas: ContatoUltimaMsg-->" + c);
        }

        return listaRetorno;
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

    @Override
    public void onDestroy() {
        mRealm.close();
        super.onDestroy();
    }
}
