package com.example.nemol.googlephotokiller.Model;

import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by nemol on 23.12.2017.
 */

public class Photo {

    private int photoId;
    private int albumId;
    private String photoLink;
    private final String IN_PHOTO_PATH = "/data/data/com.example.nemol.googlephotokiller/cache/";
    private final String EX_PHOTO_PATH = Environment.getExternalStorageDirectory() + File.separator + "GooglePhotoKiller/";


    public Photo() {
    }

    public Photo(int photoId, String photoLink) {
        this.photoId = photoId;
        this.photoLink = photoLink;
    }

    public String getIN_PHOTO_PATH() {
        return IN_PHOTO_PATH;
    }

    public static Photo fromCursor(Cursor cursor) {
        Photo listItem = new Photo();
        listItem.setPhotoLink(cursor.getString(cursor.getColumnIndex("PHOTO_LINK")));
        return listItem;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public int getAlbumId() {
        return albumId;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public void movePhotoToExternalStorage() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                InputStream in;
                OutputStream out;
                try {
                    File dir = new File(EX_PHOTO_PATH);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    in = new FileInputStream(IN_PHOTO_PATH + photoLink);
                    out = new FileOutputStream(EX_PHOTO_PATH + photoLink);

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    out.flush();
                    out.close();

                    new File(IN_PHOTO_PATH + photoLink).delete();
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
            }
        });
    }


}
