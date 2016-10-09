package br.com.bonysoft.redesocial_iesb.utilitarios;

/**
 * Created by panar on 08/10/2016.
 */

public interface ItemTouchHelperViewHolder {
    /**
     * Called when the {@link android.support.v7.widget.helper.ItemTouchHelper} first registers an item as being moved or swiped.
     * Implementations should update the item view to indicate it's active state.
     */
    void onItemSelected();


    /**
     * Called when the {@link android.support.v7.widget.helper.ItemTouchHelper} has completed the move or swipe, and the active item
     * state should be cleared.
     */
    void onItemClear();
}
