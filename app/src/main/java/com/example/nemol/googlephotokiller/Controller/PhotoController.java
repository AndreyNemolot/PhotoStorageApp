package com.example.nemol.googlephotokiller.Controller;

import android.content.Context;
import android.os.Environment;

import com.example.nemol.googlephotokiller.Callback.PhotoControllerCallback;
import com.example.nemol.googlephotokiller.Model.Photo;
import com.example.nemol.googlephotokiller.RestClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

public class PhotoController{

    private static final String PHOTO_URL = "photo/photo";
    private static final String PHOTOS_URL = "photo/photos";
    private static final String IN_PHOTO_PATH = "/data/data/com.example.nemol.googlephotokiller/cache/";
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
            final String photoPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).
                getAbsolutePath() + File.separator;
            RequestParams params = new RequestParams();
            params.put("photo_id", photo.getPhotoId());

            RestClient.get(PHOTO_URL, params, new FileAsyncHttpResponseHandler(context) {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    photoCallback.downloadPhoto(statusCode);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File response) {
                    response.renameTo(new File(IN_PHOTO_PATH, photo.getPhotoLink()));
                    photo.movePhotoToExternalStorage(photoPath);
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

    public static void deletePhoto(Context context, Photo photo) {

        RequestParams params = new RequestParams();
        params.put("photo_id", photo.getPhotoId());


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
