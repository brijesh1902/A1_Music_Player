package com.brizzs.a1musicplayer.ui.playlist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.brizzs.a1musicplayer.model.PlayList;
import com.brizzs.a1musicplayer.model.Songs;

import java.util.List;

public class PlaylistViewModel extends AndroidViewModel {

    PlaylistRepo repo;

    public PlaylistViewModel(@NonNull Application application) {
        super(application);
        repo = new PlaylistRepo(application);
    }

    LiveData<List<PlayList>> getPlaylists(){
        return repo.getAllData();
    }

    public void insertToFavourites(Songs songs) {
        repo.setToFavourites(songs);
    }
}
