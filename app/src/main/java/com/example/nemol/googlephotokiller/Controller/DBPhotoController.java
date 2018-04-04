package com.example.nemol.googlephotokiller.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.nemol.googlephotokiller.Model.Photo;
import com.example.nemol.googlephotokiller.PhotoStoreDBHelper;

public class DBPhotoController implements IDBPhotoController {

    private Context context;
    private SQLiteOpenHelper DBHelper;

    public DBPhotoController(Context context) {
        this.context = context;
        this.DBHelper = new PhotoStoreDBHelper(context);
    }

    @Override
    public boolean photoExist(Photo photo) {
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        Cursor cursor = db.query("PHOTOS", new String[]{"_id"},
                "_id = ?", new String[]{Integer.toString(photo.getPhotoId())},
                null, null, null);
        return cursor.moveToFirst();
    }

    @Override
    public void addPhoto(Photo photo) {
        ContentValues photoValues = new ContentValues();
        photoValues.put("_id", photo.getPhotoId());
        photoValues.put("PHOTO_LINK", photo.getPhotoLink());
        photoValues.put("ALBUM_ID", photo.getAlbumId());
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        db.insert("PHOTOS", null, photoValues);
        db.close();
    }

    @Override
    public void deletePhoto(Photo photo) {
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        Cursor cursor = db.query("PHOTOS", new String[]{"_id"},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            db.delete("PHOTOS", "_id = ?",
                    new String[]{Integer.toString(photo.getPhotoId())});
        }
        cursor.close();
        db.close();
    }

    @Override
    public void deletePhotosInAlbum(int albumId) {
        Cursor cursor = getPhotoList(albumId);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String link = cursor.getString(1);
                final Photo photo = new Photo(id, link);
                PhotoController.deletePhoto(context, photo);
            }
            new DBAlbumController(context).deleteAlbum(albumId);
            cursor.close();
        }
    }

    @Override
    public Cursor getPhotoList(int albumId) {
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        try {
            return db.query("PHOTOS", new String[]{"_id", "PHOTO_LINK"},
                    "ALBUM_ID = ?", new String[]{Integer.toString(albumId)},
                    null, null, null);
        } catch (SQLException e) {
            Toast.makeText(context, "Ошибка базы данных", Toast.LENGTH_SHORT).show();
            db.close();
            return null;
        }
    }
}

