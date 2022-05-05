package com.brizzs.a1musicplayer.ui.album;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.brizzs.a1musicplayer.model.Songs;

import java.util.List;

public class AlbumViewModel extends AndroidViewModel {

    private AlbumRepo repo;

    public AlbumViewModel(@NonNull Application application) {
        super(application);
        repo = new AlbumRepo(application);
    }

    public LiveData<List<Songs>> getAlbumSongsLiveData(String key) {
        return repo.getSongsAlbum(key);
    }

}
