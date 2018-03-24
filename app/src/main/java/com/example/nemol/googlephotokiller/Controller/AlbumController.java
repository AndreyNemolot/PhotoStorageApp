package com.example.nemol.googlephotokiller.Controller;


import android.content.Context;

import com.example.nemol.googlephotokiller.Callback.AlbumControllerCallback;
import com.example.nemol.googlephotokiller.Model.ActiveUser;
import com.example.nemol.googlephotokiller.Model.Album;
import com.example.nemol.googlephotokiller.Model.User;
import com.example.nemol.googlephotokiller.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

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
        User user = new User("andrey", "12345");
        Album album = new Album(user, title);
        RequestParams params = new RequestParams();


        //params.put("albumId", "1");
        params.put("albumTitle", title);
        params.put("user.userId", ActiveUser.getId());
        params.add("user.login", "andrey");
        params.add("user.password", "12345");
        params.add("user.enabled", "1");
        params.add("user.role", "ROLE_USER");


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
                super.onSuccess(statusCode, headers, timeline);
                albumCallback.getAlbumList(statusCode, timeline);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                albumCallback.getAlbumList(statusCode, null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                albumCallback.getAlbumList(statusCode, null);
            }
        });
    }

    public static void deleteAlbum(Context context, final int albumId) {
        new DBController(context).deletePhotosInAlbum(albumId);
        RequestParams params = new RequestParams();
        params.put("user_id", ActiveUser.getId());
        params.put("album_id", albumId);

        RestClient.delete(ALBUM_URL, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                        super.onSuccess(statusCode, headers, timeline);
                        albumCallback.deleteAlbum(statusCode, albumId);
                    }
                }
        );
    }

}
