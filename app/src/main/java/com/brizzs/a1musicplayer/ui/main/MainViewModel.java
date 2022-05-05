package com.brizzs.a1musicplayer.ui.main;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.brizzs.a1musicplayer.model.Album;
import com.brizzs.a1musicplayer.model.Artist;
import com.brizzs.a1musicplayer.model.Songs;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private MainRepo repo;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repo = new MainRepo(application);
    }

    public LiveData<List<Songs>> getLiveData() {
        return repo.getSongs();
    }

    public LiveData<List<Album>> getAlbumLiveData() {
        return repo.getAlbumSongs();
    }

    public LiveData<List<Artist>> getArtistLiveData() {
        return repo.getArtistSongs();
    }

}
