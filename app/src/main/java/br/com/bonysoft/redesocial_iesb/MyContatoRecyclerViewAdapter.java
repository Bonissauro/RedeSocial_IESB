package br.com.bonysoft.redesocial_iesb;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.com.bonysoft.redesocial_iesb.ContatoFragment.OnListFragmentInteractionListener;
import br.com.bonysoft.redesocial_iesb.dummy.DummyContent.DummyItem;
import br.com.bonysoft.redesocial_iesb.modelo.Contato;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyContatoRecyclerViewAdapter extends RecyclerView.Adapter<MyContatoRecyclerViewAdapter.ViewHolder> {

    private final List<Contato> mValues;
    private final OnListFragmentInteractionListener mListener;

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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContatoNomeView.setText(mValues.get(position).getNome() + " " + mValues.get(position).getSobreNome());
        holder.mContatoEmailView.setText(mValues.get(position).getEmail());

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
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContatoNomeView;
        public final TextView mContatoEmailView;
        public Contato mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContatoNomeView = (TextView) view.findViewById(R.id.contatoItemListaNome);
            mContatoEmailView = (TextView) view.findViewById(R.id.contatoItemListaEmail);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContatoNomeView.getText() + "'";
        }
    }
}
