package br.com.bonysoft.redesocial_iesb;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import br.com.bonysoft.redesocial_iesb.ContatoFragment.OnListFragmentInteractionListener;

import br.com.bonysoft.redesocial_iesb.modelo.Contato;

public class MyContatoRecyclerViewAdapter extends RecyclerView.Adapter<MyContatoRecyclerViewAdapter.ViewHolder> {

    private final List<Contato> mValues;
    private final OnListFragmentInteractionListener mListener;

    public interface OnItemClickListener {
        public void onItemClicked(int position);
    }

    public interface OnItemLongClickListener {
        public boolean onItemLongClicked(int position);
    }

    public MyContatoRecyclerViewAdapter(List<Contato> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
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

        holder.mContatoNomeView.setText(mValues.get(position).getNome() + " " + mValues.get(position).getSobreNome());
        holder.mContatoEmailView.setText(mValues.get(position).getEmail());

        if(mValues.get(position).getCaminhoFoto() != null
            && !mValues.get(position).getCaminhoFoto().isEmpty() ) {

            Uri uri = Uri.parse("https://raw.githubusercontent.com/facebook/fresco/gh-pages/static/logo.png");
            holder.mImagem.setImageURI(uri);
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

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                int p = position;

                Contato contato = mValues.get(p);

                PrincipalActivity pa = (PrincipalActivity) v.getContext();

                Intent it = new Intent(pa, ContatoCadastramentoActivity.class);
                it.putExtra("id",contato.getId());

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
        public final SimpleDraweeView mImagem;
        public Contato mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContatoNomeView = (TextView) view.findViewById(R.id.contatoItemListaNome);
            mContatoEmailView = (TextView) view.findViewById(R.id.contatoItemListaEmail);

            mImagem = (SimpleDraweeView) view.findViewById(R.id.imgListaContato);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContatoNomeView.getText() + "'";
        }
    }
}
