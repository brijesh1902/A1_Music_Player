package com.brizzs.a1musicplayer.ui.main;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.brizzs.a1musicplayer.dao.AlbumDao;
import com.brizzs.a1musicplayer.dao.ArtistDao;
import com.brizzs.a1musicplayer.db.AlbumDB;
import com.brizzs.a1musicplayer.db.ArtistDB;
import com.brizzs.a1musicplayer.model.Album;
import com.brizzs.a1musicplayer.model.Artist;
import com.brizzs.a1musicplayer.model.Songs;
import com.brizzs.a1musicplayer.utils.Common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MainRepo {

    private final AlbumDao albumDao;
    private final ArtistDao artistDao;
    private String title, artist, data, date, duration, id, album, key;
    private final List<Songs> list = new ArrayList<>();
    private final MutableLiveData<List<Songs>> liveData = new MutableLiveData<>();

    private final LiveData<List<Album>> albumliveData;
    Context context;

    private final LiveData<List<Artist>> artistliveData;

    public MainRepo(Application application) {
        this.context = application;
        AlbumDB db = AlbumDB.getInstance(application);
        albumDao = db.albumDao();
        albumliveData = albumDao.getAllAlbum();

        ArtistDB artistDB = ArtistDB.getInstance(application);
        artistDao = artistDB.artistDao();
        artistliveData = artistDao.getAllArtists();
    }

    @SuppressLint("Range")
    public LiveData<List<Songs>> getSongs() {

        Uri[] uri = {MediaStore.Audio.Media.EXTERNAL_CONTENT_URI};
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        String sortRecently = MediaStore.Audio.Media.DATE_ADDED + ">" + (System.currentTimeMillis() / 1000);

        Cursor cursor = context.getContentResolver().query(uri[0], null, selection, null, sortRecently);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                date = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
                duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                key = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY));

                Songs songs = new Songs();
                if (data.endsWith(".mp3")) {
                    songs.setArtist(artist);
                    songs.setName(title);
                    Uri uriImage = ContentUris.withAppendedId(Common.alb_Uri, Long.parseLong(id));
                    songs.setImage(String.valueOf(uriImage));
                    songs.setData(data);
                    songs.setDate(date);
                    songs.setDuration(duration);
                    songs.setAlbum(album);
                    songs.setAlbumKey(key);
                    list.add(songs);

                    liveData.postValue(list);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return liveData;
    }

    @SuppressLint("Range")
    public LiveData<List<Album>> getAlbumSongs() {

        Uri[] uri = {MediaStore.Audio.Media.EXTERNAL_CONTENT_URI};

        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";

        String sort = MediaStore.Audio.Media.ALBUM + " COLLATE NOCASE ASC";

        Cursor cursor = context.getContentResolver().query(uri[0],
                null,
                selection,
                null,
                sort);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ARTIST));
                } else {
                    artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                }
                album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                key = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY));

                Uri uriImage = ContentUris.withAppendedId(Common.alb_Uri, Long.parseLong(id));

                Album albums = new Album();
                if (data.endsWith(".mp3")) {
                    albums.setId(key);
                    albums.setAlbum(album);
                    albums.setArtist(artist);
                    albums.setImage(uriImage.toString());

                    new InsertTask(albumDao).execute(albums);

                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return albumliveData;
    }

    private static class InsertTask extends AsyncTask<Album, Void, Void> {
        private final AlbumDao dao;
        public InsertTask(AlbumDao a) {
            this.dao = a;
        }

        @Override
        protected Void doInBackground(Album... albums) {
            try {
                dao.insert(albums[0]);
            } catch (SQLiteConstraintException e) {
                Log.i("doInBackground: ", e.toString());
            }
            return null;
        }
    }

    @SuppressLint("Range")
    public LiveData<List<Artist>> getArtistSongs() {

        Uri[] uri = {MediaStore.Audio.Media.EXTERNAL_CONTENT_URI};

        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";

        String sort = MediaStore.Audio.Media.ALBUM + " COLLATE NOCASE ASC";

        Cursor cursor = context.getContentResolver().query(uri[0],
                null,
                selection,
                null,
                sort);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                key = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                Log.e("getArtistSongs: ", artist+"     "+id+"    "+key);

                Uri uriImage = ContentUris.withAppendedId(Common.art_Uri, Long.parseLong(key));

                Artist art = new Artist();
                if (data.endsWith(".mp3")) {
                    art.setId(id);
                    art.setArtist(artist);
                    art.setSongName(data);
                    art.setImage(uriImage.toString());

                    new InsertArtistTask(artistDao).execute(art);

                }
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        return artistliveData;
    }

    private static class InsertArtistTask extends AsyncTask<Artist, Void, Void> {
        private ArtistDao dao;

        public InsertArtistTask(ArtistDao a) {
            this.dao = a;
        }

        @Override
        protected Void doInBackground(Artist... artists) {
            try {
                dao.Insert(artists[0]);
            } catch (SQLiteConstraintException e) {
                Log.i("doInBackground: ", e.toString());
            }
            return null;
        }
    }

}
