package com.brizzs.a1musicplayer.ui.playlist;

import static com.brizzs.a1musicplayer.utils.Common.FAVOURITES;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.brizzs.a1musicplayer.dao.PlayListDao;
import com.brizzs.a1musicplayer.db.PlayListDB;
import com.brizzs.a1musicplayer.model.PlayList;
import com.brizzs.a1musicplayer.model.Songs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlaylistRepo {

    Context context;
    PlayListDao playListDao;
    private LiveData<List<PlayList>> liveData ;

    public PlaylistRepo(Application application) {
        context = application;
        PlayListDB db = PlayListDB.getDatabase(application);
        playListDao = db.playListDao();
        liveData = playListDao.getPlaylists();
    }

    public LiveData<List<PlayList>> getAllData() {
        try {
            Log.e("getAllData: ", Objects.requireNonNull(liveData.getValue()).size()+"---");
        } catch (Exception e) {
            PlayList playList = new PlayList();
            playList.setId(String.valueOf(1));
            playList.setName(FAVOURITES);
            PlayListDB.databaseWriteExecutor.execute(() -> {
                playListDao.insert(playList);
            });
            Log.e("getAllData: ", "NULLLLL");
        }
        return liveData;
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
