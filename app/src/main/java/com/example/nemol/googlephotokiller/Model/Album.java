package com.example.nemol.googlephotokiller.Model;

import android.database.Cursor;

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

    public Album(){

    }

    public static Album fromCursor(Cursor cursor) {
        Album listItem = new Album();
        listItem.setAlbumTitle(cursor.getString(cursor.getColumnIndex("ALBUM_TITLE")));
        return listItem;
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

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }
}
