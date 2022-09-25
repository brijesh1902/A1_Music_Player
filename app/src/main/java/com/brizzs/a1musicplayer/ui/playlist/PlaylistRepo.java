package com.brizzs.a1musicplayer.ui.playlist;

import static com.brizzs.a1musicplayer.utils.Common.FAVOURITES;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.brizzs.a1musicplayer.dao.PlayListDao;
import com.brizzs.a1musicplayer.dao.SongsDao;
import com.brizzs.a1musicplayer.db.PlayListDB;
import com.brizzs.a1musicplayer.db.SongsDB;
import com.brizzs.a1musicplayer.model.PlayList;
import com.brizzs.a1musicplayer.model.Songs;
import com.brizzs.a1musicplayer.utils.Common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlaylistRepo {

    Context context;
    PlayListDao playListDao;
    SongsDao songsDao;
    LiveData<List<Songs>> liveSongs;
    private LiveData<List<PlayList>> liveData;
    private String title, artist, data, date, duration, id, album, key;
    private List<Songs> list = new ArrayList<>();
    int value = -1;

    public PlaylistRepo(Application application) {
        context = application;
        SongsDB db = SongsDB.getDatabase(application);
        songsDao = db.songsDao();
        liveSongs = songsDao.getSongs();
    }

    @SuppressLint("Range")
    public LiveData<List<Songs>> getAllData() {
        return liveSongs;
    }

    public void setToFavourites(Songs songs) {

        PlayList playList = new PlayList();
        playList.setId(String.valueOf(1));
        playList.setName(FAVOURITES);
        if (playList.getImage1() != null) playList.setImage1(songs.getImage());
        if (playList.getImage2() != null) playList.setImage2(songs.getImage());
        if (playList.getImage3() != null) playList.setImage3(songs.getImage());
        if (playList.getImage4() != null) playList.setImage4(songs.getImage());

        PlayListDB.databaseWriteExecutor.execute(() -> {
            playListDao.insert(playList);
        });
    }
}
