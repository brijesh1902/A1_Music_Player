package com.brizzs.a1musicplayer.ui.album;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.brizzs.a1musicplayer.model.Songs;
import com.brizzs.a1musicplayer.utils.Common;

import java.util.ArrayList;
import java.util.List;

public class AlbumRepo {

    Context context;
    private String title, artist, data, date, duration, id, album, key;
    private List<Songs> list = new ArrayList<>();
    private MutableLiveData<List<Songs>> liveData = new MutableLiveData<>();


    public AlbumRepo(Application application) {
        context = application;
    }

    @SuppressLint("Range")
    public LiveData<List<Songs>> getSongsAlbum(String k) {

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

                Uri uriImage = ContentUris.withAppendedId(Common.alb_Uri, Long.parseLong(id));

                Songs songs = new Songs();
                if (data.endsWith(".mp3")) {
                   if (key.equals(k)) {
                       songs.setArtist(artist);
                       songs.setName(title);
                       songs.setImage(String.valueOf(uriImage));
                       songs.setData(data);
                       songs.setDate(date);
                       songs.setDuration(duration);
                       songs.setAlbum(album);
                       songs.setAlbumKey(id);

                       list.add(songs);
                   }
                    liveData.setValue(list);
                }

            } while (cursor.moveToNext());
        }
        cursor.close();

        return liveData;
    }
}
