package com.brizzs.a1musicplayer.adapters;

import static com.brizzs.a1musicplayer.utils.Common.album;
import static com.brizzs.a1musicplayer.utils.Common.artist;
import static com.brizzs.a1musicplayer.utils.Common.artists;
import static com.brizzs.a1musicplayer.utils.Common.recently;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.brizzs.a1musicplayer.R;
import com.brizzs.a1musicplayer.model.Album;
import com.brizzs.a1musicplayer.model.Artist;
import com.brizzs.a1musicplayer.service.OnSongAdapterCallback;
import com.brizzs.a1musicplayer.model.Songs;
import com.brizzs.a1musicplayer.ui.main.MainActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

public class SongsAdapter  extends ListAdapter<Songs, SongsAdapter.ViewHolder> {

    private OnSongAdapterCallback context;
    private List<Songs> data;
    private String type;
    private List<Album> albums;
    private  List<Artist> artistList;

    public SongsAdapter(OnSongAdapterCallback c, List<Songs> songs, String album) {
        super(DIFF_CALLBACK);
        this.context = c;
        this.data = songs;
        this.type = album;
    }

    private static final DiffUtil.ItemCallback<Songs> DIFF_CALLBACK = new DiffUtil.ItemCallback<Songs>() {
        @Override
        public boolean areItemsTheSame(@NonNull Songs oldItem, @NonNull Songs newItem) {
            return oldItem.getData().equals(newItem.getData());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Songs oldItem, @NonNull Songs newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getArtist().equals(newItem.getArtist()) &&
                    oldItem.getImage().equals(newItem.getImage());
        }
    };

    @Override
    public int getItemCount() {
        int i = 1;
        if (type.equals(recently))
            i = data.size();
        else if (type.equals(album))
            i = albums.size();
        else if (type.equals(artists))
            i = artistList.size();
        return i;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.songs_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (type.equals(recently)) {
            final Songs options = data.get(position);

            Glide.with(holder.itemView.getContext()).load(options.getImage())
                    .placeholder(R.drawable.music_note_24).error(R.drawable.music_note_24)
                    .transition(DrawableTransitionOptions.withCrossFade()).into(holder.image);
            holder.name.setText(options.getName());
            holder.singer.setText(options.getArtist());

            Log.e("onBindViewHolder: ", options.getName()+"\n"+options.getImage()+"\n"+options.getArtist());

        } else if (type.equals(album)) {
            Album options = albums.get(position);
            Glide.with(holder.itemView.getContext()).load(options.getImage())
                    .placeholder(R.drawable.music_note_24).error(R.drawable.music_note_24)
                    .transition(DrawableTransitionOptions.withCrossFade()).into(holder.image);
            holder.name.setText(options.getAlbum());
            holder.singer.setText(options.getArtist());
        } else if (type.equals(artists)) {
            Artist options = artistList.get(position);
            Glide.with(holder.itemView.getContext()).load(options.getImage())
                    .placeholder(R.drawable.music_note_24).error(R.drawable.music_note_24)
                    .transition(DrawableTransitionOptions.withCrossFade()).into(holder.image);
            holder.name.setText(options.getArtist());
            holder.singer.setVisibility(View.INVISIBLE);

//            holder.singer.setText(options.getArtist());
        }

    }

    public void setList(List<Album> list) {
        this.albums = list;
    }

    public void setArtistList(List<Artist> list) {
        this.artistList = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView singer, name;
        ImageView image;
        LinearLayout more;
        ConstraintLayout parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.song_name);
            image = itemView.findViewById(R.id.img);
            singer = itemView.findViewById(R.id.song_artist);
            more = itemView.findViewById(R.id.more);
            parent = itemView.findViewById(R.id.parent);

            itemView.setOnClickListener(v -> {
                try {
                    if (type.equals(recently))
                        context.Callback(getAdapterPosition(), data, image, name, singer);
                    else
                    if (type.equals(album))
                        context.AlbumCallback(getAdapterPosition(), albums, image, name, singer);
                } catch (Exception e) {
                    v.getContext().startActivity(new Intent(v.getContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            });

        }
    }
}
