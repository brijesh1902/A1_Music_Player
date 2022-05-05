package com.brizzs.a1musicplayer.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "artist_table", indices = {@Index(value = {"id"}, unique = true)})
public class Artist {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    private String songName, artist, image;

    public Artist(){}

    public Artist(@NonNull String id, String songName, String artist, String image) {
        this.id = id;
        this.songName = songName;
        this.artist = artist;
        this.image = image;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
