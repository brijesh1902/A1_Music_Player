package com.brizzs.a1musicplayer.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "songs")
public class Songs implements Serializable {

    @NonNull
    @PrimaryKey
    private String name;
    private String artist, image, duration, data, date, album, albumKey;

    @Ignore
    public Songs(){}

    public Songs(@NonNull String name, String artist,
                 String image, String duration, String data, String date, String album, String albumKey) {
        this.name = name;
        this.artist = artist;
        this.image = image;
        this.duration = duration;
        this.data = data;
        this.date = date;
        this.album = album;
        this.albumKey = albumKey;
    }

    public String getAlbumKey() {
        return albumKey;
    }

    public void setAlbumKey(String albumKey) {
        this.albumKey = albumKey;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getImage() {
        return image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
