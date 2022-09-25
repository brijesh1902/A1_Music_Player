package com.brizzs.a1musicplayer.ui.main;

import static com.brizzs.a1musicplayer.utils.Common.SPAN_COUNT;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.brizzs.a1musicplayer.model.Album;
import com.brizzs.a1musicplayer.model.Artist;
import com.brizzs.a1musicplayer.model.Songs;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final MainRepo repo;
    int spanCount = SPAN_COUNT;

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
