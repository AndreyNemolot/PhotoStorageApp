package com.example.nemol.googlephotokiller.Controller;


import com.example.nemol.googlephotokiller.Callback.AlbumListCallback;
import com.example.nemol.googlephotokiller.Callback.UserControllerCallback;
import com.example.nemol.googlephotokiller.Model.ActiveUser;
import com.example.nemol.googlephotokiller.Model.Album;
import com.example.nemol.googlephotokiller.RestClient;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by nemol on 11.12.2017.
 */

public class AlbumController {

    private final static String ALBUM_URL = "album/album";
    private final static String ALBUMS_URL = "album/albums";
    private static UserControllerCallback answerCallback;
    private static AlbumListCallback albumListCallback;

    public static void registerAnswerCallBack(UserControllerCallback answer) {
        answerCallback = answer;
    }

    public static void registerAlbumsCallBack(AlbumListCallback albums) {
        albumListCallback = albums;
    }


    public static void createAlbum(String title) {
        RequestParams params = new RequestParams();
        params.put("userId", ActiveUser.getId());
        params.put("albumTitle", title);

        RestClient.post(ALBUM_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                //201 created
                //409 conflict (user exist)
                answerCallback.userAction(statusCode);
            }
        });
    }

    public static JSONObject getAllAlbums() {
        RequestParams params = new RequestParams();
        params.put("user_id", ActiveUser.getId());
        RestClient.get(ALBUMS_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                //201 created
                //409 conflict (user exist)
                //answerCallback.createAnswer(statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {

                    ArrayList<Album> list = new ArrayList<Album>();
                    try {
                        int i;
                        for (i = 0; i < timeline.length(); i++){
                            Gson gson = new Gson();
                            Album object = gson.fromJson(timeline.getJSONObject(i).toString(), Album.class);
                            list.add(object);
                        }
                    } catch (JSONException e) {
                        System.out.println(e.getMessage());
                    }
                    albumListCallback.albumsList(list);
            }
        });
        return new JSONObject();
    }
}
