package com.example.nemol.googlephotokiller.Model;

/**
 * Created by nemol on 11.12.2017.
 */

public class Album {

    private int albumId;
    private int userId;
    private String albumTitle;

    @Override
    public String toString() {
        return albumTitle;
    }

    public Album(int id, String title){
        this.userId = id;
        this.albumTitle = title;
    }

    public int getAlbumId() {
        return albumId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

}
