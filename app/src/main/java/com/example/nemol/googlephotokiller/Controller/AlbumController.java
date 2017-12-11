package com.example.nemol.googlephotokiller.Controller;


import com.example.nemol.googlephotokiller.Callback.AlbumListCallback;
import com.example.nemol.googlephotokiller.Callback.CreateAnswerCallback;
import com.example.nemol.googlephotokiller.Model.Album;
import com.example.nemol.googlephotokiller.RestClient;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by nemol on 11.12.2017.
 */

public class AlbumController {

    private final static String ALBUM_URL = "album/album";
    private final static String ALBUMS_URL = "album/albums";
    private static CreateAnswerCallback answerCallback;
    private static AlbumListCallback albumListCallback;

    public static void registerAnswerCallBack(CreateAnswerCallback answer) {
        answerCallback = answer;
    }

    public static void registerAlbumsCallBack(AlbumListCallback albums) {
        albumListCallback = albums;
    }


    public static void createAlbum(String title) {
        RequestParams params = new RequestParams();
        params.put("userId", 43);// TODO: 11.12.2017 брать ид из активюзер
        params.put("albumTitle", title);

        RestClient.post(ALBUM_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                //201 created
                //409 conflict (user exist)
                answerCallback.createAnswer(statusCode, "createAlbum");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                try {
                    JSONObject firstEvent = (JSONObject) timeline.get(0);

                } catch (JSONException ex) {
                }
            }
        });
    }

    public static JSONObject getAllAlbums() {
        RequestParams params = new RequestParams();
        params.put("userId", 43);// TODO: 11.12.2017 брать ид из активюзер
        RestClient.get(ALBUMS_URL, null, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                //201 created
                //409 conflict (user exist)
                answerCallback.createAnswer(statusCode, "createAlbum");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {

                    List<Album> list = new ArrayList<Album>();
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

    /*public static void setParams(String typeOfAction){
        RequestParams params = new RequestParams();
        switch (typeOfAction){
            case "created":
                params.put()
                break;
        }

    }*/
}
