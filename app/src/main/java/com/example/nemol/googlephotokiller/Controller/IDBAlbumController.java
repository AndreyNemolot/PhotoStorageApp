package com.example.nemol.googlephotokiller.Controller;

import android.database.Cursor;

import com.example.nemol.googlephotokiller.Model.Album;

public interface IDBAlbumController {

    public void addAlbum(Album album);

    public void deleteAlbum(int albumId);

    public boolean albumExist(Album album);

    public Cursor getAllAlbums(int userId);

}
