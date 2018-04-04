package com.example.nemol.googlephotokiller.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.nemol.googlephotokiller.Model.ActiveUser;
import com.example.nemol.googlephotokiller.PhotoStoreDBHelper;

public class DBUserController implements IDBUserController {

    private Context context;
    private SQLiteOpenHelper DBHelper;

    public DBUserController(Context context) {
        this.context = context;
        this.DBHelper = new PhotoStoreDBHelper(context);
    }

    @Override
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

    @Override
    public boolean deleteUser() {
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("USER", new String[]{"_id"},
                    null, null, null, null, null);
            if (cursor.moveToFirst()) {
                db.delete("USER", "_id = ?",
                        new String[]{Integer.toString(ActiveUser.getId())});
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

    @Override
    public boolean getUser() {
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

    @Override
    public boolean getUserByLogin() {
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("USER", new String[]{"_id", "LOGIN", "PASSWORD"},
                    "LOGIN = ? and PASSWORD = ?", new String[]{ActiveUser.getLogin(),
                            ActiveUser.getPassword()}, null, null, null);
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
}
