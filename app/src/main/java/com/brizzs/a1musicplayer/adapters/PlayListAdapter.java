package com.brizzs.a1musicplayer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.brizzs.a1musicplayer.R;
import com.brizzs.a1musicplayer.model.PlayList;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class PlayListAdapter extends ListAdapter<PlayList, PlayListAdapter.ViewHolder> {

    public PlayListAdapter() {
        super(diffCallback);
    }

    private static DiffUtil.ItemCallback<PlayList> diffCallback = new DiffUtil.ItemCallback<PlayList>() {
        @Override
        public boolean areItemsTheSame(@NonNull PlayList oldItem, @NonNull PlayList newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull PlayList oldItem, @NonNull PlayList newItem) {
            return oldItem.getId().equals(newItem.getId());
        }
    };

    @NonNull
    @Override
    public PlayListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListAdapter.ViewHolder holder, int position) {
        PlayList playList = getItem(position);

        holder.name.setText(playList.getName());

        if (playList.getImage1() != null) {
            Glide.with(holder.itemView.getContext()).load(playList.getImage1())
                    .placeholder(R.drawable.music_note_24).error(R.drawable.music_note_24)
                    .transition(DrawableTransitionOptions.withCrossFade()).into(holder.image1);
        }

        if (playList.getImage2() != null) {
            Glide.with(holder.itemView.getContext()).load(playList.getImage2())
                    .placeholder(R.drawable.music_note_24).error(R.drawable.music_note_24)
                    .transition(DrawableTransitionOptions.withCrossFade()).into(holder.image2);
        }
        if (playList.getImage3() != null) {
            Glide.with(holder.itemView.getContext()).load(playList.getImage3())
                    .placeholder(R.drawable.music_note_24).error(R.drawable.music_note_24)
                    .transition(DrawableTransitionOptions.withCrossFade()).into(holder.image3);
        }
        if (playList.getImage4() != null) {
            Glide.with(holder.itemView.getContext()).load(playList.getImage4())
                    .placeholder(R.drawable.music_note_24).error(R.drawable.music_note_24)
                    .transition(DrawableTransitionOptions.withCrossFade()).into(holder.image4);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image1, image2, image3, image4;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.playlist_name);
            image1 = itemView.findViewById(R.id.img1);
            image2 = itemView.findViewById(R.id.img2);
            image3 = itemView.findViewById(R.id.img3);
            image4 = itemView.findViewById(R.id.img4);
        }
    }
}
