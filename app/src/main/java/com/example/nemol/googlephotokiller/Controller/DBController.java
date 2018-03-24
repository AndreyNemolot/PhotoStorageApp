package com.example.nemol.googlephotokiller.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.nemol.googlephotokiller.Model.ActiveUser;
import com.example.nemol.googlephotokiller.Model.Album;
import com.example.nemol.googlephotokiller.Model.Photo;
import com.example.nemol.googlephotokiller.PhotoStoreDBHelper;


public class DBController {

    private Context context;
    private SQLiteOpenHelper DBHelper;

    public DBController(Context context) {
        this.context = context;
        DBHelper = new PhotoStoreDBHelper(context);

    }

    public boolean addUser() {
        ContentValues userValues = new ContentValues();
        userValues.put("_id", Integer.toString(ActiveUser.getId()));
        userValues.put("LOGIN", ActiveUser.getLogin());
        userValues.put("PASSWORD", ActiveUser.getPassword());
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("USER", new String[]{"_id"},
                    null, null, null, null, null);
            if (cursor.moveToFirst()) {
                db.update("USER", userValues, "_id=?",
                        new String[]{Integer.toString(cursor.getInt(0))});
            } else {
                db.insert("USER", null, userValues);
            }
            cursor.close();
            db.close();
            return true;
        } catch (SQLException e) {
            db.close();
            Toast.makeText(context, "Ошибка базы данных", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean deleteUser() {
        ContentValues userValues = new ContentValues();
        userValues.put("_id", Integer.toString(ActiveUser.getId()));
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("USER", new String[]{"_id"},
                    null, null, null, null, null);
            if (cursor.moveToFirst()) {
                db.delete("USER", "_id = ?",
                        new String[]{userValues.getAsString("_id")});
            }
            cursor.close();
            db.close();
            return true;
        } catch (SQLException e) {
            db.close();
            Toast.makeText(context, "Ошибка базы данных", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean loadUser() {
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("USER", new String[]{"_id", "LOGIN", "PASSWORD"},
                    null, null, null, null, null);
            if (cursor.moveToFirst()) {
                ActiveUser.saveUser(cursor.getInt(0),
                        cursor.getString(1), cursor.getString(2), true);
                cursor.close();
                db.close();
                return true;
            } else {
                db.close();
                cursor.close();
                return false;
            }
        } catch (SQLException e) {
            db.close();
            Toast.makeText(context, "Ошибка базы данных", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean loadUserByLogin() {
        ContentValues userValues = new ContentValues();
        userValues.put("LOGIN", ActiveUser.getLogin());
        userValues.put("PASSWORD", ActiveUser.getPassword());
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("USER", new String[]{"_id", "LOGIN", "PASSWORD"},
                    "LOGIN = ? and PASSWORD = ?", new String[]{userValues.getAsString("LOGIN"),
                            userValues.getAsString("PASSWORD")}, null, null, null);
            if (cursor.moveToFirst()) {
                ActiveUser.saveUser(cursor.getInt(0),
                        cursor.getString(1), cursor.getString(2), false);
                cursor.close();
                db.close();
                return true;
            } else {
                db.close();
                cursor.close();
                return false;
            }
        } catch (SQLException e) {
            db.close();
            Toast.makeText(context, "Ошибка базы данных", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void addAlbum(Album album) {
        ContentValues albumValues = new ContentValues();
        albumValues.put("_id", album.getAlbumId());
        albumValues.put("ALBUM_TITLE", album.getAlbumTitle());
        albumValues.put("USER_ID", album.getUserId());
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        db.insert("ALBUMS", null, albumValues);
        db.close();
    }

    public void deleteAlbum(int albumId) {
        ContentValues values = new ContentValues();
        values.put("_id", albumId);
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        Cursor cursor = db.query("AlBUMS", new String[]{"_id"},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            db.delete("ALBUMS", "_id = ?",
                    new String[]{values.getAsString("_id")});
        }
        cursor.close();
        db.close();
    }

    public boolean albumExist(Album album) {
        ContentValues albumValues = new ContentValues();
        albumValues.put("_id", album.getAlbumId());
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        Cursor cursor = db.query("AlBUMS", new String[]{"_id"},
                "_id = ?", new String[]{albumValues.getAsString("_id")},
                null, null, null);
        return cursor.moveToFirst();
    }

    public boolean photoExist(Photo photo) {
        ContentValues photoValues = new ContentValues();
        photoValues.put("_id", photo.getPhotoId());
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        Cursor cursor = db.query("PHOTOS", new String[]{"_id"},
                "_id = ?", new String[]{photoValues.getAsString("_id")},
                null, null, null);
        return cursor.moveToFirst();
    }

    public void addPhoto(Photo photo) {
        ContentValues photoValues = new ContentValues();
        photoValues.put("_id", photo.getPhotoId());
        photoValues.put("PHOTO_LINK", photo.getPhotoLink());
        photoValues.put("ALBUM_ID", photo.getAlbumId());
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        db.insert("PHOTOS", null, photoValues);
        db.close();
    }

    void deletePhoto(Photo photo) {
        ContentValues photoValues = new ContentValues();
        photoValues.put("_id", photo.getPhotoId());
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        Cursor cursor = db.query("PHOTOS", new String[]{"_id"},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            db.delete("PHOTOS", "_id = ?",
                    new String[]{photoValues.getAsString("_id")});
        }
        cursor.close();
        db.close();
    }

    void deletePhotosInAlbum(int albumId){
        Cursor cursor = getPhotoList(albumId);
        if(cursor!=null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String link = cursor.getString(1);
                final Photo photo = new Photo(id, link);
                PhotoController.deletePhoto(context, photo);
            }
            deleteAlbum(albumId);
            cursor.close();
        }
    }

    public Cursor getPhotoList(int albumId) {
        ContentValues photoValues = new ContentValues();
        photoValues.put("ALBUM_ID", albumId);
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        try {
            return db.query("PHOTOS", new String[]{"_id", "PHOTO_LINK"},
                    "ALBUM_ID = ?", new String[]{photoValues.getAsString("ALBUM_ID")},
                    null, null, null);
        } catch (SQLException e) {
            Toast.makeText(context, "Ошибка базы данных", Toast.LENGTH_SHORT).show();
            db.close();
            return null;
        }
    }

}
