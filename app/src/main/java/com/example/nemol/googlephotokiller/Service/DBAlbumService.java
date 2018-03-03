package com.example.nemol.googlephotokiller.Service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.nemol.googlephotokiller.Controller.DBController;
import com.example.nemol.googlephotokiller.Model.Album;
import com.example.nemol.googlephotokiller.ServerDoneEvent;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class DBAlbumService extends IntentService {

    private String INTENT_MESSAGE = "jsonArray";

    public DBAlbumService() {
        super("DBAlbumService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized (this) {
            String albums = intent.getStringExtra(INTENT_MESSAGE);
            try {
                JSONArray jsonArrayAlbums = new JSONArray(albums);
                for (int i = 0; i < jsonArrayAlbums.length(); i++) {
                    Album object = new Gson().fromJson(jsonArrayAlbums.getJSONObject(i).toString(), Album.class);
                    ContentValues albumValues = new ContentValues();
                    albumValues.put("_id", object.getAlbumId());
                    albumValues.put("ALBUM_TITLE", object.getAlbumTitle());
                    albumValues.put("USER_ID", object.getUserId());
                    new DBController(this).addAlbum(albumValues);
                }
                EventBus.getDefault().post(new ServerDoneEvent(true));
            } catch (JSONException e) {
                EventBus.getDefault().post(new ServerDoneEvent(false));
                e.printStackTrace();
            }
        }
    }


}
