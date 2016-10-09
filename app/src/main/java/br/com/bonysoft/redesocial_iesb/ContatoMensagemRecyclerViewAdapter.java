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
        extends RecyclerView.Adapter<ContatoMensagemRecyclerViewAdapter.ViewHolder>{

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

        ContatoRepositorio repo = new ContatoRepositorio();
        emailUsuario = repo.buscaEmailUsuarioLogado();
        repo.close();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_lista_conversas, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.mItem = mValues.get(position);
        holder.mContatoNomeView.setText(mValues.get(position).contato.nomeCompleto());
        holder.mTextoUltimoMsg.setText(mValues.get(position).getTextoUltimaMensagem());

        if(mValues.get(position).contato .getCaminhoFoto()!= null && !mValues.get(position).contato.getCaminhoFoto().isEmpty()){
            try {

                Uri imageUri = Uri.fromFile(new File(mValues.get(position).contato.getCaminhoFoto()));
                Log.i("ContatoLog","Caminho do Item no List " + imageUri.toString() );
                holder.mSimpleImagem.setImageURI(imageUri);

            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            Log.i("ContatoLog","Nao encontrou a imagem "+ mValues.get(position).contato.getCaminhoFoto());
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

                int p = position;
                ContatoUltimaMsg c = mValues.get(p);

                Intent it = new Intent(mActivity, ConversaActivity.class);
                it.putExtra(Constantes.EMAIL_CONVERSA,c.contato.getEmail());
                mActivity.startActivity(it);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContatoNomeView;
        public final TextView mTextoUltimoMsg;
        public final SimpleDraweeView mSimpleImagem;

        public ContatoUltimaMsg mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContatoNomeView = (TextView) view.findViewById(R.id.contatoItemListaNomeMsg);
            mTextoUltimoMsg = (TextView) view.findViewById(R.id.textoUltimoMensagem);
            mSimpleImagem = (SimpleDraweeView) view.findViewById(R.id.imgListaContato);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContatoNomeView.getText() + "'";
        }
    }
}
