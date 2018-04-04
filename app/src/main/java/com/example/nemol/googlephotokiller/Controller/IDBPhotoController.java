package com.example.nemol.googlephotokiller.Controller;

import android.database.Cursor;

import com.example.nemol.googlephotokiller.Model.Photo;

public interface IDBPhotoController {

    public boolean photoExist(Photo photo);

    public void addPhoto(Photo photo);

    void deletePhoto(Photo photo);

    void deletePhotosInAlbum(int albumId);

    public Cursor getPhotoList(int albumId);
}
