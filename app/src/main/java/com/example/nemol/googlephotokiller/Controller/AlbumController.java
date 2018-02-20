package com.example.nemol.googlephotokiller.Controller;


import com.example.nemol.googlephotokiller.Callback.AlbumControllerCallback;
import com.example.nemol.googlephotokiller.Callback.PhotoControllerCallback;
import com.example.nemol.googlephotokiller.Model.ActiveUser;
import com.example.nemol.googlephotokiller.Model.Album;
import com.example.nemol.googlephotokiller.Model.Photo;
import com.example.nemol.googlephotokiller.RestClient;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by nemol on 11.12.2017.
 */

public class AlbumController {

    private final static String ALBUM_URL = "album/album";
    private final static String ALBUMS_URL = "album/albums";
    private final static String PHOTO_PATH = "/data/data/com.example.nemol.googlephotokiller/cache/";

    private static AlbumControllerCallback albumCallback;

    public static void registerAlbumCallBack(AlbumControllerCallback callback) {
        albumCallback = callback;
    }

    public static void createAlbum(String title) {
        RequestParams params = new RequestParams();
        params.put("userId", ActiveUser.getId());
        params.put("albumTitle", title);

        RestClient.post(ALBUM_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                albumCallback.addAlbum(statusCode);
            }
        });
    }

    public static void getAllAlbums() {
        RequestParams params = new RequestParams();
        params.put("user_id", ActiveUser.getId());
        RestClient.get(ALBUMS_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                albumCallback.getAlbumList(statusCode, null);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                ArrayList<Album> list = new ArrayList<>();
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        Gson gson = new Gson();
                        Album object = gson.fromJson(timeline.getJSONObject(i).toString(), Album.class);
                        list.add(object);
                    }
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
                albumCallback.getAlbumList(statusCode, list);
            }
        });
    }

    public static void deleteAlbum(int albumId) {
        RequestParams params = new RequestParams();
        params.put("user_id", ActiveUser.getId());
        params.put("album_id", albumId);
        RestClient.delete(ALBUM_URL, params, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                        super.onSuccess(statusCode, headers, timeline);
                        try {
                            for (int i = 0; i < timeline.length(); i++) {
                                Photo object = new Gson().fromJson(timeline.getJSONObject(i).toString(), Photo.class);
                                new File(PHOTO_PATH + object.getPhotoLink()).delete();
                            }
                            albumCallback.deleteAlbum(statusCode);
                        } catch (JSONException e) {
                            System.out.println(e.getMessage());
                        }
                    }



                }
        );
    }

}
