package com.example.nemol.googlephotokiller.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.nemol.googlephotokiller.Callback.PhotoControllerCallback;
import com.example.nemol.googlephotokiller.Model.Photo;
import com.example.nemol.googlephotokiller.RestClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import cz.msebera.android.httpclient.Header;

/**
 * Created by nemol on 28.11.2017.
 */

public class PhotoController extends AppCompatActivity {

    private final static String PHOTO_URL = "photo/photo";
    private final static String PHOTOS_URL = "photo/photos";
    private final static String IN_PHOTO_PATH = "/data/data/com.example.nemol.googlephotokiller/cache/";
    private final static String EX_PHOTO_PATH = Environment.getExternalStorageDirectory() + File.separator + "GooglePhotoKiller/";
    private static PhotoControllerCallback photoCallback;

    public static void registerPhotoCallBack(PhotoControllerCallback Callback) {
        photoCallback = Callback;
    }

    public static void uploadPhoto(String path, int albumId) {
        RequestParams params = new RequestParams();
        final String contentType = RequestParams.APPLICATION_OCTET_STREAM;
        File myFile = new File(path);
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
                photoCallback.uploadPhoto(statusCode);
            }

        });
    }

    public static void downloadPhoto(Context context, final Photo photo) {
            final String name = photo.getPhotoLink();

            RequestParams params = new RequestParams();
            params.put("photo_id", photo.getPhotoId());

            RestClient.get(PHOTO_URL, params, new FileAsyncHttpResponseHandler(context) {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    photoCallback.downloadPhoto(statusCode);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File response) {
                    response.renameTo(new File(IN_PHOTO_PATH, name));
                    photo.movePhotoToExternalStorage();
                    photoCallback.downloadPhoto(statusCode);
                }
            });
    }

    public static void getPhotoList(int AlbumId) {
        RequestParams params = new RequestParams();
        params.put("album_id", AlbumId);
        RestClient.get(PHOTOS_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                photoCallback.getPhotoList(statusCode, null);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                super.onSuccess(statusCode, headers, timeline);
                photoCallback.getPhotoList(statusCode, timeline);
            }
        });
    }

    public static void deletePhoto(Photo photo, Context context) {
        new File(File.separator + EX_PHOTO_PATH + photo.getPhotoLink()).delete();
        RequestParams params = new RequestParams();
        params.put("photo_id", photo.getPhotoId());
        new DBController(context).deletePhoto(photo);

        RestClient.delete(PHOTO_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                photoCallback.deletePhoto(statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                photoCallback.deletePhoto(statusCode);
            }

        });
    }

}
