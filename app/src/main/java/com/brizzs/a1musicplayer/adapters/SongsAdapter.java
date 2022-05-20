package com.brizzs.a1musicplayer.adapters;

import static com.brizzs.a1musicplayer.ui.playing.PlayActivity.position;
import static com.brizzs.a1musicplayer.ui.playing.PlayActivity.songslist;
import static com.brizzs.a1musicplayer.utils.Common.FAVOURITES;
import static com.brizzs.a1musicplayer.utils.Common.album;
import static com.brizzs.a1musicplayer.utils.Common.artists;
import static com.brizzs.a1musicplayer.utils.Common.current_list;
import static com.brizzs.a1musicplayer.utils.Common.duration;
import static com.brizzs.a1musicplayer.utils.Common.recently;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.brizzs.a1musicplayer.R;
import com.brizzs.a1musicplayer.dao.AlbumDao;
import com.brizzs.a1musicplayer.dao.PlayListDao;
import com.brizzs.a1musicplayer.dao.SongsDao;
import com.brizzs.a1musicplayer.db.PlayListDB;
import com.brizzs.a1musicplayer.db.SongsDB;
import com.brizzs.a1musicplayer.model.Album;
import com.brizzs.a1musicplayer.model.Artist;
import com.brizzs.a1musicplayer.model.PlayList;
import com.brizzs.a1musicplayer.service.OnSongAdapterCallback;
import com.brizzs.a1musicplayer.model.Songs;
import com.brizzs.a1musicplayer.ui.main.MainActivity;
import com.brizzs.a1musicplayer.ui.main.MainRepo;
import com.brizzs.a1musicplayer.ui.playing.PlayActivity;
import com.brizzs.a1musicplayer.ui.playlist.PlaylistViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SongsAdapter  extends ListAdapter<Songs, SongsAdapter.ViewHolder> {

    private final OnSongAdapterCallback context;
    private final List<Songs> data;
    private final String type;
    private List<Album> albums;
    private  List<Artist> artistList;
    Animation animation;
    private SongsDao songsDao;

    public SongsAdapter(OnSongAdapterCallback c, List<Songs> songs, String album) {
        super(DIFF_CALLBACK);
        this.context = c;
        this.data = songs;
        this.type = album;
    }

    private static final DiffUtil.ItemCallback<Songs> DIFF_CALLBACK = new DiffUtil.ItemCallback<Songs>() {
        @Override
        public boolean areItemsTheSame(@NonNull Songs oldItem, @NonNull Songs newItem) {
            return oldItem == newItem;
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
        animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.anim_item);
//        SongsDB db = SongsDB.getDatabase(parent.getContext());
//        songsDao = db.songsDao();
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.songs_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {

        if (type.equals(recently)) {
           recentlyView(pos, holder);
        } else if (type.equals(album)) {
            albumsView(pos, holder);
        } else if (type.equals(artists)) {
            artistsView(pos, holder);
        }

    }

    private void recentlyView(int pos, ViewHolder holder) {
        final Songs options = data.get(pos);

        Glide.with(holder.itemView.getContext()).load(options.getImage())
                .placeholder(R.drawable.music_note_24).error(R.drawable.music_note_24)
                .transition(DrawableTransitionOptions.withCrossFade()).into(holder.image);
        holder.name.setText(options.getName());
        holder.singer.setText(options.getArtist());

        if (songslist.size() != 0  && songslist.get(position).getName().equals(options.getName())) {
            Glide.with(holder.gif.getContext()).load(R.drawable.giphy).into(holder.gif);
            holder.gif.setVisibility(View.VISIBLE);
            new Handler(Looper.myLooper()).post(() -> {
                notifyItemRangeChanged(pos, data.size());
            });
        } else {
            holder.gif.setVisibility(View.GONE);
        }

        holder.more.setOnClickListener(v -> {
            v.startAnimation(animation);
            v.getParent().requestDisallowInterceptTouchEvent(true);
            PopupMenu popupMenu = new PopupMenu(holder.more.getContext(), v, Gravity.RIGHT);
            popupMenu.inflate(R.menu.menu_more);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_play:
                        playSong(pos, holder);
                        break;

                    case R.id.action_playNext:
                        playNext(pos);
                        break;

                    case R.id.action_addToQueue:
                        addToQueue(pos);
                        break;

//                    case R.id.action_favourite:
//                        addFavourites(options);
//                        break;
//
//                    case R.id.action_playList:
//                        addToPlaylist(pos, holder);
//                        break;
                }

                return false;
            });

            popupMenu.show();

        });


        holder.more.setVisibility(View.VISIBLE);
    }

    private void addToQueue(int pos) {
        final Songs options = data.get(pos);
        songslist.add(options);
        Log.e("addToQueue: ", position+"--"+songslist.size());
    }

    private void playNext(int pos) {
        final Songs options = data.get(pos);
        songslist.add(position+1, options);
    }

    private void playSong(int pos, ViewHolder holder) {
        Intent intent = new Intent(holder.more.getContext(), PlayActivity.class);
        intent.putExtra(current_list, (Serializable) data);
        intent.putExtra("pos", pos);
        intent.putExtra(duration, "0");

        Pair<View, String> pair1 = Pair.create(holder.image, "image");
        Pair<View, String> pair2 = Pair.create(holder.name, "songname");
        Pair<View, String> pair3 = Pair.create(holder.singer, "singer");

        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                (Activity) holder.more.getContext(), pair1, pair2, pair3);

        ((Context) holder.more.getContext()).startActivity(intent, optionsCompat.toBundle());
    }


    private void addFavourites(Songs options) {
        options.setAlbumKey(FAVOURITES);
        new InsertTask(songsDao).execute(options);
    }

    private void addToPlaylist(int pos, ViewHolder holder) {

    }

    private void setRingtone(Songs songs) {

        Uri uri = Uri.parse("content://media/external/audio/media/"+songs.getAlbumKey());
//        content://media/external/audio/media/
        try {
            RingtoneManager.setActualDefaultRingtoneUri((Context) context, RingtoneManager.TYPE_RINGTONE, uri);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void albumsView(int pos, ViewHolder holder) {
        Album options = albums.get(pos);
        Glide.with(holder.itemView.getContext()).load(options.getImage())
                .placeholder(R.drawable.music_note_24).error(R.drawable.music_note_24)
                .transition(DrawableTransitionOptions.withCrossFade()).into(holder.image);
        holder.name.setText(options.getAlbum());
        holder.singer.setText(options.getArtist());
    }

    private void artistsView(int pos, ViewHolder holder) {
        Artist options = artistList.get(pos);
        Glide.with(holder.itemView.getContext()).load(options.getImage())
                .placeholder(R.drawable.music_note_24).error(R.drawable.music_note_24)
                .transition(DrawableTransitionOptions.withCrossFade()).into(holder.image);
        holder.name.setText(options.getArtist());
        holder.singer.setVisibility(View.INVISIBLE);
    }

    public void setList(List<Album> list) {
        this.albums = list;
    }

    public void setArtistList(List<Artist> list) {
        this.artistList = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView singer, name;
        ImageView image, gif, more;
        ConstraintLayout parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.song_name);
            image = itemView.findViewById(R.id.img);
            singer = itemView.findViewById(R.id.song_artist);
            more = itemView.findViewById(R.id.more);
            gif = itemView.findViewById(R.id.gif);
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

    private static class InsertTask extends AsyncTask<Songs, Void, Void> {
        private final SongsDao dao;
        public InsertTask(SongsDao a) {
            this.dao = a;
        }

        @Override
        protected Void doInBackground(Songs... playLists) {
            try {
//                dao.insert(playLists[0]);
            } catch (Exception e) {
                Log.i("doInBackground: ", e.toString());
            }
            return null;
        }
    }

}


