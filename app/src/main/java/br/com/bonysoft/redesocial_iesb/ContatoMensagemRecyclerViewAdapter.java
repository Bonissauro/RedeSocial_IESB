package br.com.bonysoft.redesocial_iesb;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.com.bonysoft.redesocial_iesb.ListaConversasFragment.OnListFragmentInteractionListener;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.List;


import br.com.bonysoft.redesocial_iesb.modelo.ContatoUltimaMsg;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.ContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;


public class ContatoMensagemRecyclerViewAdapter
        extends RecyclerView.Adapter<ContatoMensagemRecyclerViewAdapter.ContatoMensagemViewHolder>{

    private final static String TAG = Constantes.TAG_LOG;

    private final List<ContatoUltimaMsg> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Activity mActivity;
    private String emailUsuario;

    public interface OnItemClickListener {
        public void onItemClicked(int position);
    }

    public interface OnItemLongClickListener {
        public boolean onItemLongClicked(int position);
    }

    public ContatoMensagemRecyclerViewAdapter(List<ContatoUltimaMsg> items, OnListFragmentInteractionListener listener, Activity activity) {
        mValues = items;
        mListener = listener;
        mActivity = activity;

        emailUsuario = ApplicationRedeSocial.getInstance().emailRegistrado();
    }

    @Override
    public ContatoMensagemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contato_msg, parent, false);
        return new ContatoMensagemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContatoMensagemViewHolder holder, final int position) {

        holder.mItem = mValues.get(position);
        Log.d(TAG, "onBindViewHolder: mValues("+position+")-->" + holder.mItem);
        Log.d(TAG, "onBindViewHolder: holder.mItem.contato.nomeCompleto() -->"+holder.mItem.contato.nomeCompleto());
        Log.d(TAG, "onBindViewHolder: mContatoNomeView --> "+ holder.mContatoNomeView );

        holder.mContatoNomeView.setText(holder.mItem.contato.nomeCompleto());
        holder.mTextoUltimoMsg.setText(holder.mItem.getTextoUltimaMensagem());

        if (holder.mItem.contato.getCaminhoFoto() != null && !holder.mItem.contato.getCaminhoFoto().isEmpty()) {
            try {

                Uri imageUri = Uri.fromFile(new File(holder.mItem.contato.getCaminhoFoto()));
                Log.i(TAG, "Caminho do Item no List " + imageUri.toString());
                holder.mSimpleImagem.setImageURI(imageUri);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Nao encontrou a imagem " + holder.mItem.contato.getCaminhoFoto());
            String imageUri = "drawable://" + R.drawable.ic_foto_padrao;
            holder.mSimpleImagem.setImageURI(imageUri);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteractionContato(holder.mItem);
                }

                ContatoUltimaMsg c = mValues.get(position);

                Intent it = new Intent(mActivity, ConversaActivity.class);
                it.putExtra(Constantes.EMAIL_CONVERSA, c.contato.getEmail());
                mActivity.startActivity(it);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ContatoMensagemViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContatoNomeView;
        public final TextView mTextoUltimoMsg;
        public final SimpleDraweeView mSimpleImagem;

        public ContatoUltimaMsg mItem;

        public ContatoMensagemViewHolder(View view) {
            super(view);
            mView = view;
            mContatoNomeView = (TextView) view.findViewById(R.id.contatoItemListaNomeMsg);
            mTextoUltimoMsg = (TextView) view.findViewById(R.id.textoUltimoMensagem);
            mSimpleImagem = (SimpleDraweeView) view.findViewById(R.id.imgListaContatoMsg);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContatoNomeView.getText() + "'";
        }
    }
}
