package com.brizzs.a1musicplayer.adapters;

import static com.brizzs.a1musicplayer.ui.playing.PlayActivity.position;
import static com.brizzs.a1musicplayer.ui.playing.PlayActivity.songsList;
import static com.brizzs.a1musicplayer.utils.Common.IMAGE;
import static com.brizzs.a1musicplayer.utils.Common.SPAN_COUNT_ONE;
import static com.brizzs.a1musicplayer.utils.Common.album;
import static com.brizzs.a1musicplayer.utils.Common.artists;
import static com.brizzs.a1musicplayer.utils.Common.current_list;
import static com.brizzs.a1musicplayer.utils.Common.duration;
import static com.brizzs.a1musicplayer.utils.Common.image;
import static com.brizzs.a1musicplayer.utils.Common.playlist;
import static com.brizzs.a1musicplayer.utils.Common.recently;
import static com.brizzs.a1musicplayer.utils.Const.SONG_ARTIST;
import static com.brizzs.a1musicplayer.utils.Const.SONG_IMAGE;
import static com.brizzs.a1musicplayer.utils.Const.SONG_NAME;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.brizzs.a1musicplayer.R;
import com.brizzs.a1musicplayer.dao.SongsDao;
import com.brizzs.a1musicplayer.db.SongsDB;
import com.brizzs.a1musicplayer.model.Album;
import com.brizzs.a1musicplayer.model.Artist;
import com.brizzs.a1musicplayer.service.OnSongAdapterCallback;
import com.brizzs.a1musicplayer.model.Songs;
import com.brizzs.a1musicplayer.ui.main.MainActivity;
import com.brizzs.a1musicplayer.ui.playing.PlayActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class SongsAdapter  extends ListAdapter<Songs, SongsAdapter.ViewHolder> {

    private final OnSongAdapterCallback context;
    private final List<Songs> data;
    private final String type;
    private List<Album> albums;
    private  List<Artist> artistList;
    Animation animation;
    private SongsDao songsDao;
    GridLayoutManager gridLayoutManager;

    public SongsAdapter(OnSongAdapterCallback c, List<Songs> songs, String album, GridLayoutManager layoutManager) {
        super(DIFF_CALLBACK);
        this.context = c;
        this.data = songs;
        this.type = album;
        gridLayoutManager = layoutManager;
    }

    @Override
    public int getItemViewType(int position) {
        int span = gridLayoutManager.getSpanCount();
        if (span == 1){
            return SPAN_COUNT_ONE;
        } else {
            return 2;
        }
    }

    static DiffUtil.ItemCallback<Songs> DIFF_CALLBACK = new DiffUtil.ItemCallback<Songs>() {
        @Override
        public boolean areItemsTheSame(@NonNull Songs oldItem, @NonNull Songs newItem) {
            return oldItem.getName().equals(newItem.getName()) ||
                    oldItem.getArtist().equals(newItem.getArtist()) ||
                    oldItem.getImage().equals(newItem.getImage());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Songs oldItem, @NonNull Songs newItem) {
            return Objects.equals(oldItem, newItem);
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
        else if (type.equals(playlist))
            i = data.size();
        return i;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.anim_item);
        SongsDB db = SongsDB.getDatabase(parent.getContext());
        songsDao = db.songsDao();
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == 2) view = layoutInflater.inflate(R.layout.songs_view, parent, false);
        else view = layoutInflater.inflate(R.layout.view_songs_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {

        if (type.equals(recently) || type.equals(playlist)) {
           recentlyView(pos, holder);
        } else if (type.equals(album)) {
            albumsView(pos, holder);
        } else if (type.equals(artists)) {
            artistsView(pos, holder);
        }

    }

    @SuppressLint("NonConstantResourceId")
    private void recentlyView(int pos, ViewHolder holder) {
        final Songs options = data.get(pos);

        Glide.with(holder.itemView.getContext()).load(options.getImage())
                .placeholder(R.drawable.music_note_24).error(R.drawable.music_note_24)
                .transition(DrawableTransitionOptions.withCrossFade()).into(holder.image);
        holder.name.setText(options.getName());
        holder.singer.setText(options.getArtist());

        if (songsList.size() != 0  && songsList.get(position).getName().equals(options.getName())) {
//            Glide.with(holder.gif.getContext()).load(R.drawable.giphy).into(holder.gif);
            holder.animationView.setVisibility(View.VISIBLE);
            new Handler(Looper.myLooper()).post(() -> {
                notifyItemRangeChanged(pos, data.size());
            });
        } else {
            holder.animationView.setVisibility(View.GONE);
        }

        holder.more.setOnClickListener(v -> {
            v.startAnimation(animation);
            v.getParent().requestDisallowInterceptTouchEvent(true);
            PopupMenu popupMenu = new PopupMenu(holder.more.getContext(), v, Gravity.RIGHT);
            popupMenu.inflate(R.menu.menu_more);

            if (type.equals(playlist)) {
                popupMenu.getMenu().findItem(R.id.action_favourite).setVisible(false);
                popupMenu.getMenu().findItem(R.id.action_remove).setVisible(true);
            } else {
                popupMenu.getMenu().findItem(R.id.action_favourite).setVisible(true);
                popupMenu.getMenu().findItem(R.id.action_remove).setVisible(false);
            }

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

                    case R.id.action_favourite:
                        addFavourites(options);
                        break;

                    case R.id.action_remove:
                        removeFavourites(options);
                        break;
                }

                return false;
            });

            popupMenu.show();

        });


        holder.more.setVisibility(View.VISIBLE);
    }

    private void addToQueue(int pos) {
        final Songs options = data.get(pos);
        songsList.add(options);
        Log.e("addToQueue: ", position+"--"+ songsList.size());
    }

    private void playNext(int pos) {
        final Songs options = data.get(pos);
        songsList.add(position+1, options);
    }

    private void playSong(int pos, ViewHolder holder) {
        Intent intent = new Intent(holder.more.getContext(), PlayActivity.class);
        intent.putExtra(current_list, (Serializable) data);
        intent.putExtra("pos", pos);
        intent.putExtra(duration, "0");

        Pair<View, String> pair1 = Pair.create(holder.image, SONG_IMAGE);
        Pair<View, String> pair2 = Pair.create(holder.name, SONG_NAME);
        Pair<View, String> pair3 = Pair.create(holder.singer, SONG_ARTIST);

        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) holder.more.getContext(), pair1, pair2, pair3);

        ((Context) holder.more.getContext()).startActivity(intent, optionsCompat.toBundle());
    }


    private void addFavourites(Songs options) {
//        options.setAlbumKey(FAVOURITES);
        new InsertTask(songsDao).execute(options);
    }

    private void removeFavourites(Songs options) {
        new DeleteTask(songsDao).execute(options);
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

    /*@Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (type.equals(playlist)) {
            if (fromPosition < toPosition)
                for (int i = fromPosition; i < toPosition; i++)
                    Collections.swap(data, i, i + 1);
            else
                for (int i = fromPosition; i > toPosition; i--)
                    Collections.swap(data, i, i - 1);

            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public void onRowSelected(ViewHolder viewHolder) {
        viewHolder.parent.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(ViewHolder viewHolder) {
        viewHolder.parent.setBackgroundColor(Color.WHITE);
    }*/

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView singer, name;
        ImageView image, gif, more;
        ConstraintLayout parent;
        LottieAnimationView animationView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.song_name);
            image = itemView.findViewById(R.id.img);
            singer = itemView.findViewById(R.id.song_artist);
            more = itemView.findViewById(R.id.more);
            gif = itemView.findViewById(R.id.gif);
            parent = itemView.findViewById(R.id.parent);
            animationView = itemView.findViewById(R.id.animation_view);

            itemView.setOnClickListener(v -> {
                try {
                    if (type.equals(recently) || type.equals(playlist))
                        context.Callback(getAbsoluteAdapterPosition(), data, image, name, singer);
                    else
                    if (type.equals(album))
                        context.AlbumCallback(getAbsoluteAdapterPosition(), albums, image, name, singer);
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
                SongsDB.databaseWriteExecutor.execute(() -> {
                    dao.insert(playLists[0]);
                });
                Log.e("doInBackground0: ", playLists[0].getName());
            } catch (Exception e) {
                Log.i("doInBackground2: ", e.toString());
            }
            return null;
        }
    }

    private static class DeleteTask extends AsyncTask<Songs, Void, Void> {
        private final SongsDao dao;
        public DeleteTask(SongsDao a) {
            this.dao = a;
        }

        @Override
        protected Void doInBackground(Songs... playLists) {
            try {
                SongsDB.databaseWriteExecutor.execute(() -> {
                    dao.delete(playLists[0]);
                });
                Log.e("doInBackground0: ", String.valueOf(playLists[0].getName()));
            } catch (Exception e) {
                Log.i("doInBackground2: ", e.toString());
            }
            return null;
        }
    }

}


