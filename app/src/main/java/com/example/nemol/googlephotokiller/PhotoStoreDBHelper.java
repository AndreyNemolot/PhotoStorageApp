package com.example.nemol.googlephotokiller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nemol on 28.02.2018.
 */

public class PhotoStoreDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "photoStore";
    private static final int DB_VERSION = 1;

    public PhotoStoreDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE USER(_id INTEGER PRIMARY KEY,"
        + "LOGIN TEXT,"
        + "PASSWORD TEXT);");

        sqLiteDatabase.execSQL("CREATE TABLE ALBUMS(_id INTEGER PRIMARY KEY,"
                + "ALBUM_TITLE TEXT,"
                + "USER_ID INTEGER,"
                + "FOREIGN KEY (USER_ID) REFERENCES USER (_id));");

        sqLiteDatabase.execSQL("CREATE TABLE PHOTOS(_id INTEGER PRIMARY KEY,"
                + "PHOTO_LINK TEXT,"
                + "ALBUM_ID INTEGER,"
                + "FOREIGN KEY (ALBUM_ID) REFERENCES ALBUM (ALBUM_ID));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
