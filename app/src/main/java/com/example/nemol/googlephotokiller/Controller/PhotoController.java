package com.example.nemol.googlephotokiller.Controller;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public static void downloadPhoto(Context context, List<Photo> photoList) {
// TODO: 05.12.2017 сделать сервис загрузки

        for (int i = 0; i < photoList.size(); i++) {
            Photo photo = photoList.get(i);
            final String name = photo.getPhotoLink();

            if (!new File(EX_PHOTO_PATH + name).exists()) {

                RequestParams params = new RequestParams();
                params.put("photo_id", photo.getPhotoId());

                RestClient.get(PHOTO_URL, params, new FileAsyncHttpResponseHandler(context) {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                        photoCallback.downloadPhoto(statusCode);
                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, File response) {

                        response.renameTo(new File(IN_PHOTO_PATH, name)); // TODO: 20.02.2018 сделать проще

                        moveFile(IN_PHOTO_PATH,
                                name, EX_PHOTO_PATH);
                        photoCallback.downloadPhoto(statusCode);
                    }
                });
            }
        }
        photoCallback.downloadPhoto(HttpStatus.SC_CREATED);
    }

    private static void moveFile(String inputPath, String inputFile, String outputPath) {

        InputStream in;
        OutputStream out;
        try {
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            out.flush();
            out.close();
            out = null;

            new File(inputPath + inputFile).delete();
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    public static void getPhotoList(int album) {
        RequestParams params = new RequestParams();
        params.put("album_id", album);
        final List<Photo> list = new ArrayList<>();
        RestClient.get(PHOTOS_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                photoCallback.getPhotoList(statusCode, null);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                super.onSuccess(statusCode, headers, timeline);
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        Photo object = new Gson().fromJson(timeline.getJSONObject(i).toString(), Photo.class);
                        list.add(object);
                    }
                    photoCallback.getPhotoList(statusCode, list);
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    public static void deletePhoto(Photo photo) {
        new File(File.separator + EX_PHOTO_PATH + photo.getPhotoLink()).delete();
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
