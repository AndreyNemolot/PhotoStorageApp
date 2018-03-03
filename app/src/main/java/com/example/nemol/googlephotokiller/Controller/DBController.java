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
import com.example.nemol.googlephotokiller.PhotoStoreDBHelper;

/**
 * Created by nemol on 28.02.2018.
 */

public class DBController {

    private Context context;
    private SQLiteOpenHelper DBHelper;

    public DBController(Context context) {
        this.context = context;
        DBHelper = new PhotoStoreDBHelper(context);

    }

    public boolean saveUser(ContentValues userValues) {
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("USER", new String[]{"_id"},
                    null, null, null, null, null);
            if (cursor.moveToFirst()) {
                db.update("USER", userValues, "_id=?",
                        new String[]{Integer.toString(cursor.getInt(0))});
            }else {
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

    public boolean deleteUser(ContentValues userValues) {
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("USER", new String[]{"_id"},
                    null, null, null, null, null);
            if (cursor.moveToFirst()) {
                db.delete("USER","_id = ?",
                        new String[]{ userValues.getAsString("_id")});
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
                        cursor.getString(1), cursor.getString(2));
                cursor.close();
                db.close();
                return true;
            }else {
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

    public void addAlbum(ContentValues albumValues){
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        db.insert("ALBUMS", null, albumValues);
        db.close();
    }

    void deleteAlbum(ContentValues albumValues){
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        Cursor cursor = db.query("AlBUMS", new String[]{"_id"},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            db.delete("ALBUMS","_id = ?",
                    new String[]{ albumValues.getAsString("_id")});
        }// TODO: 03.03.2018 так же удалить все фотографии
        cursor.close();
        db.close();
    }

    public void addPhoto(ContentValues albumValues){
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        db.insert("PHOTOS", null, albumValues);
        db.close();
    }

    void deletePhoto(ContentValues photoValues){
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        Cursor cursor = db.query("PHOTOS", new String[]{"_id"},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            db.delete("PHOTOS","_id = ?",
                    new String[]{ photoValues.getAsString("_id")});
        }
        cursor.close();
        db.close();
    }
}
