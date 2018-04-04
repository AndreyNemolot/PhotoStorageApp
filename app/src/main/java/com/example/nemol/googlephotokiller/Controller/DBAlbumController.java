package com.example.nemol.googlephotokiller.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.IntegerRes;

import com.example.nemol.googlephotokiller.Model.ActiveUser;
import com.example.nemol.googlephotokiller.Model.Album;
import com.example.nemol.googlephotokiller.PhotoStoreDBHelper;

public class DBAlbumController implements IDBAlbumController {

    private Context context;
    private SQLiteOpenHelper DBHelper;

    public DBAlbumController(Context context) {
        this.context = context;
        this.DBHelper = new PhotoStoreDBHelper(context);
    }

    @Override
    public void addAlbum(Album album) {
        ContentValues albumValues = new ContentValues();
        albumValues.put("_id", album.getAlbumId());
        albumValues.put("ALBUM_TITLE", album.getAlbumTitle());
        albumValues.put("USER_ID", album.getUserId());
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        db.insert("ALBUMS", null, albumValues);
        db.close();
    }

    @Override
    public void deleteAlbum(int albumId) {
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        Cursor cursor = db.query("AlBUMS", new String[]{"_id"},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            db.delete("ALBUMS", "_id = ?",
                    new String[]{Integer.toString(albumId)});
        }
        cursor.close();
        db.close();
    }

    @Override
    public boolean albumExist(Album album) {
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        Cursor cursor = db.query("AlBUMS", new String[]{"_id"},
                "_id = ?", new String[]{Integer.toString(album.getAlbumId())},
                null, null, null);
        return cursor.moveToFirst();
    }

    @Override
    public Cursor getAllAlbums(int userId) {
        SQLiteOpenHelper DBHelper = new PhotoStoreDBHelper(context);
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        return db.query("ALBUMS", new String[]{"_id", "ALBUM_TITLE"},
                "USER_ID = ?", new String[]{Integer.toString(userId)},
                null, null, null);
    }
}
