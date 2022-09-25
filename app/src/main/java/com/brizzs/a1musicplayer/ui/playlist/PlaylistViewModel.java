package com.brizzs.a1musicplayer.ui.playlist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.brizzs.a1musicplayer.model.Songs;

import java.util.List;

public class PlaylistViewModel extends AndroidViewModel {

    PlaylistRepo repo;
    boolean isAvailable;

    public PlaylistViewModel(@NonNull Application application) {
        super(application);
        repo = new PlaylistRepo(application);
        isAvailable = repo.value == 1;
    }

    LiveData<List<Songs>> getPlaylists(){
        return repo.liveSongs;
    }

    public void insertToFavourites(Songs songs) {
        repo.setToFavourites(songs);
    }
}
