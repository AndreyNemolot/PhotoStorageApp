package com.example.nemol.googlephotokiller.Controller;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import com.example.nemol.googlephotokiller.Callback.PhotoAnswerCallback;
import com.example.nemol.googlephotokiller.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by nemol on 28.11.2017.
 */

public class PhotoController extends AppCompatActivity {

    private final static String PHOTO_URL = "photo/photo";
    private static PhotoAnswerCallback progressBarActive;

    public static void registerCallBack(PhotoAnswerCallback callback) {
        progressBarActive = callback;
    }

    public static String getAbsPath(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(projection[0]);
        String picturePath = cursor.getString(columnIndex); // returns null
        cursor.close();
        return picturePath;
    }

    public static void uploadPhoto(Context context, Uri uri, int albumId) {
// TODO: 05.12.2017 сделать сервис загрузки
        RequestParams params = new RequestParams();
        final String contentType = RequestParams.APPLICATION_OCTET_STREAM;
        File myFile = new File(getAbsPath(context, uri));
        try {
            params.put("file", myFile, contentType);
            params.put("album_id", albumId);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        params.setHttpEntityIsRepeatable(true);
        params.setUseJsonStreamer(false);
        RestClient.post(PHOTO_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressBarActive.photoAnswer(statusCode);

                //200 done
                //201 created
                //409 conflict (user exist)
            }
        });

    }

}
