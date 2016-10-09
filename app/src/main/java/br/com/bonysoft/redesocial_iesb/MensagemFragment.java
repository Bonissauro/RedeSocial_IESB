package br.com.bonysoft.redesocial_iesb;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.bonysoft.redesocial_iesb.modelo.Mensagem;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.ContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MensagemFragment extends Fragment {

    public static class MensagemViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTextoView;

        public MensagemViewHolder(View view) {
            super(view);
            mView = view;
            mTextoView = (TextView) view.findViewById(R.id.textoMensagem);
        }

        @Override
        public String toString() {
            if(null != mTextoView) {
                return super.toString() + " '" + mTextoView.getText() + "'";
            }
            return super.toString() + " '" + "null" + "'";
        }
    }

    private LinearLayoutManager mLinearLayoutManager;
    private DatabaseReference mFirebaseDatabaseReference;
    private RecyclerView mMessageRecyclerView;


    private String emailContato;
    private String emailUsuario;

    private FirebaseRecyclerAdapter<Mensagem, MensagemViewHolder> mFirebaseAdapter;

    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MensagemFragment() {
    }

    public static MensagemFragment newInstance(String contato) {
        MensagemFragment fragment = new MensagemFragment();

        Bundle args = new Bundle();
        args.putString(Constantes.LISTA_MENS_CONTATO, contato);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            emailContato = getArguments().getString(Constantes.LISTA_MENS_CONTATO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_mensagem_list, container, false);

        ContatoRepositorio repo = new ContatoRepositorio();
        emailUsuario = repo.buscaEmailUsuarioLogado();
        repo.close();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mMessageRecyclerView = (RecyclerView) view;

            mLinearLayoutManager = new LinearLayoutManager(getContext());
            mLinearLayoutManager.setStackFromEnd(true);

            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mFirebaseAdapter  = new FirebaseRecyclerAdapter<Mensagem, MensagemViewHolder> (
                    Mensagem.class,
                    R.layout.fragment_mensagem,
                    MensagemViewHolder.class,
                    //TODO como fazer a consulta select * from mensagens where (de = emailUsuario and para = email contato)
                    mFirebaseDatabaseReference.child("mensagens").orderByChild("timestamp")) {

                @Override
                protected void populateViewHolder(MensagemViewHolder viewHolder, Mensagem msg, int position) {
                    //mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    //Tenho que pegar todas as conversas do FireBase para depois filtar aqui
                    //uma merda ne, mas nao conheco um jeito de colocar um OR na query do Firebase
                    Log.d(Constantes.TAG_LOG , "Populando o View Holder TextoView--> " + viewHolder.mTextoView);
                    Log.d(Constantes.TAG_LOG , "Populando o View Holder MSG --> " + msg);
                    if(msg!= null && viewHolder != null &&  viewHolder.mTextoView != null && (
                        (msg.de.equalsIgnoreCase(emailContato) && msg.para.equalsIgnoreCase(emailUsuario) )
                     || (msg.de.equalsIgnoreCase(emailUsuario) && msg.para.equalsIgnoreCase(emailContato) ) )){
                        viewHolder.mTextoView.setText(msg.texto);

                        //LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        RelativeLayout.LayoutParams params =
                                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT);

                        int color;
                        if(msg.de.equalsIgnoreCase(emailUsuario)) {
                            //llp.setMargins(10, 10, 10, 10); // llp.setMargins(left, top, right, bottom);
                            color = R.color.backGroundRece;
                            //viewHolder.mTextoView.setGravity(Gravity.RIGHT);
                            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                            params.setMargins(10, 10,10, 10);
                        }
                        else {
                            //llp.setMargins(10, 10,10, 10); // llp.setMargins(left, top, right, bottom);
                            color = R.color.backGroundEnvio;
                            //viewHolder.mTextoView.setGravity(Gravity.LEFT);
                            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                            params.setMargins(10, 10,10, 10);
                        }

                        viewHolder.mTextoView.setBackgroundResource(color);
                        viewHolder.mTextoView.setLayoutParams(params);
                    }else if(viewHolder != null &&  viewHolder.mTextoView != null){
                        viewHolder.mTextoView.setVisibility(View.GONE);
                    }

                }
            };

            mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                    int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                    // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                    // to the bottom of the list to show the newly added message.
                    if (lastVisiblePosition == -1 ||
                            (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                        mMessageRecyclerView.scrollToPosition(positionStart);
                    }
                }
            });

            mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
            mMessageRecyclerView.setAdapter(mFirebaseAdapter);
        }
       return view;
    }
/*
    private String obterEmailUsuarioLogado(){

        return email;
    }

    private List<Mensagem> recuperarMensagensNoFirebase(final String emailUsuario,final String emailContato){
        final List<Mensagem> retorno = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("mensagens");


        // or(de = emailContato and para = emailUsuario) ???
        ref.orderByChild("timestamp")
                .startAt(emailUsuario,"de").endAt(emailContato,"de")
                .startAt(emailContato,"de").endAt(emailUsuario,"de")
                .startAt(emailUsuario,"para").endAt(emailContato,"para")
                .startAt(emailContato,"para").endAt(emailUsuario,"para")
                .addValueEventListener(
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
                                    Log.i(Constantes.TAG_LOG,"Inserindo Mensagem");
                                    retorno.add(m);
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

        return retorno;
    }
*/
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
        void onListFragmentInteraction(Mensagem item);
    }
}
