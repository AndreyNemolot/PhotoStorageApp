package com.example.nemol.googlephotokiller.Controller;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.example.nemol.googlephotokiller.Callback.AnswerCallback;
import com.example.nemol.googlephotokiller.Callback.PhotoControllerCallback;
import com.example.nemol.googlephotokiller.Model.Photo;
import com.example.nemol.googlephotokiller.RestClient;
import com.google.gson.Gson;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;

/**
 * Created by nemol on 28.11.2017.
 */

public class PhotoController extends AppCompatActivity {

    private final static String PHOTO_URL = "photo/photo";
    private final static String PHOTOS_URL = "photo/photos";
    private final static String PHOTO_PATH = "/data/data/com.example.nemol.googlephotokiller/cache/";
    //private final static String PHOTO_PATH = "/storage/emulated/0/gPhotoKiller";
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

    public static void downloadPhoto(Context context, List<Photo> photoList) {
// TODO: 05.12.2017 сделать сервис загрузки

        for (int i = 0; i < photoList.size(); i++) {
            Photo photo = photoList.get(i);
            final String name = photo.getPhotoLink();

            if (!new File(PHOTO_PATH + name).exists()) {

                RequestParams params = new RequestParams();
                params.put("photo_id", photo.getPhotoId());

                RestClient.get(PHOTO_URL, params, new FileAsyncHttpResponseHandler(context) {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                        photoCallback.downloadPhoto(statusCode);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, File response) {
                        response.renameTo(new File(PHOTO_PATH, name)); // TODO: 23.12.2017 сохранять на внешней памяти
                        photoCallback.downloadPhoto(statusCode);
                    }
                });
            }
        }
        photoCallback.downloadPhoto(HttpStatus.SC_CREATED);
    }

    public static void getPhotoList(int album) {
        RequestParams params = new RequestParams();
        params.put("album_id", album);
        final List<Photo> list = new ArrayList<>();
        RestClient.get(PHOTOS_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                //201 created
                //409 conflict (user exist)
                //answerCallback.createAnswer(statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        Photo object = new Gson().fromJson(timeline.getJSONObject(i).toString(), Photo.class);
                        list.add(object);
                    }
                    photoCallback.getPhotoList(list);
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    public static void deletePhoto(Photo photo) {
        new File(PHOTO_PATH + photo.getPhotoLink()).delete();
        RequestParams params = new RequestParams();
        params.put("photo_id", photo.getPhotoId());
        RestClient.delete(PHOTO_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
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
