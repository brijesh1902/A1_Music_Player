package com.brizzs.a1musicplayer.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.brizzs.a1musicplayer.adapters.SongsAdapter;

public class ItemMoveCallback extends ItemTouchHelper.Callback {

    private final ItemHelperInterface itemHelperInterface;

    public ItemMoveCallback(ItemHelperInterface helperInterface){
        itemHelperInterface = helperInterface;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int drag = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(drag, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        itemHelperInterface.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            if (viewHolder instanceof SongsAdapter.ViewHolder) {
                SongsAdapter.ViewHolder holder = (SongsAdapter.ViewHolder) viewHolder;
                itemHelperInterface.onRowSelected(holder);
            }
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof  SongsAdapter.ViewHolder){
            SongsAdapter.ViewHolder holder = (SongsAdapter.ViewHolder) viewHolder;
            itemHelperInterface.onRowClear(holder);
        }
    }

    public interface ItemHelperInterface{
        void onRowMoved(int fromPosition, int toPosition);
        void onRowSelected(SongsAdapter.ViewHolder viewHolder);
        void onRowClear(SongsAdapter.ViewHolder viewHolder);
    }

}
