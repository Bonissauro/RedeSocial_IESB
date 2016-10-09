package br.com.bonysoft.redesocial_iesb;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.Collections;


import br.com.bonysoft.redesocial_iesb.ContatoFragment.OnListFragmentInteractionListener;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;
import br.com.bonysoft.redesocial_iesb.realm.repositorio.ContatoRepositorio;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;
import br.com.bonysoft.redesocial_iesb.utilitarios.ItemTouchHelperAdapter;
import io.realm.Realm;
import io.realm.RealmResults;

public class MyContatoRecyclerViewAdapter
        extends RecyclerView.Adapter<MyContatoRecyclerViewAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    private final RealmResults<Contato> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Activity mActivity;
    private String emailUsuario;

    public interface OnItemClickListener {
        public void onItemClicked(int position);
    }

    public interface OnItemLongClickListener {
        public boolean onItemLongClicked(int position);
    }

    @Override
    public void onItemDismiss(int position) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        mValues.deleteFromRealm(position);
        realm.commitTransaction();

        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mValues, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mValues, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
       //return true;
    }

    public MyContatoRecyclerViewAdapter(RealmResults<Contato> items, OnListFragmentInteractionListener listener, Activity activity) {
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
                .inflate(R.layout.fragment_contato, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.mItem = mValues.get(position);
        holder.mContatoNomeView.setText(mValues.get(position).nomeCompleto());
        holder.mContatoEmailView.setText(mValues.get(position).getEmail());

        if(mValues.get(position).getCaminhoFoto()!= null && !mValues.get(position).getCaminhoFoto().isEmpty()){
            try {

                Uri imageUri = Uri.fromFile(new File(mValues.get(position).getCaminhoFoto()));
                Log.i("ContatoLog","Caminho do Item no List " + imageUri.toString() );
                holder.mSimpleImagem.setImageURI(imageUri);

            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            Log.i("ContatoLog","Nao encontrou a imagem "+ mValues.get(position).getCaminhoFoto());
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
            }
        });

        if( holder.mItem != null && holder.mItem.getEmail() != null && holder.mItem.getEmail().equalsIgnoreCase(emailUsuario) ){
            holder.mImgBotao.setVisibility(View.INVISIBLE);
        } else {
            holder.mImgBotao.setVisibility(View.VISIBLE);
        }

        holder.mImgBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                int p = position;
                Contato contato = mValues.get(p);

                Intent it = new Intent(mActivity, ConversaActivity.class);
                it.putExtra(Constantes.EMAIL_CONVERSA,contato.getEmail());
                mActivity.startActivity(it);
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                int p = position;

                Contato contato = mValues.get(p);

                PrincipalActivity pa = (PrincipalActivity) v.getContext();

                Intent it = new Intent(pa, ContatoCadastramentoActivity.class);
                it.putExtra(Constantes.ID_CONTATO,contato.getId());
                pa.startActivityForResult(it,111);

                return false;

            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
/*
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //public final TextView mIdView;
        public final TextView mContentView;
        public Contato mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            //mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

    }*/

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContatoNomeView;
        public final TextView mContatoEmailView;
        public final SimpleDraweeView mSimpleImagem;
        public final ImageButton mImgBotao;

        public Contato mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContatoNomeView = (TextView) view.findViewById(R.id.contatoItemListaNome);
            mContatoEmailView = (TextView) view.findViewById(R.id.contatoItemListaEmail);
            mImgBotao = (ImageButton) view.findViewById(R.id.btnItemConversa);
            mSimpleImagem = (SimpleDraweeView) view.findViewById(R.id.imgListaContato);


        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContatoNomeView.getText() + "'";
        }
    }
}
