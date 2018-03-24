package com.example.nemol.googlephotokiller.Service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;

import com.example.nemol.googlephotokiller.Controller.DBController;
import com.example.nemol.googlephotokiller.Model.Album;
import com.example.nemol.googlephotokiller.Model.ServerDoneEvent;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class DBAlbumService extends IntentService {

    private final String INTENT_MESSAGE = "jsonArray";

    public DBAlbumService() {
        super("DBAlbumService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized (this) {
            String albums = intent.getStringExtra(INTENT_MESSAGE);
            DBController controller = new DBController(this);
            try {
                JSONArray jsonArrayAlbums = new JSONArray(albums);
                for (int i = 0; i < jsonArrayAlbums.length(); i++) {
                    Album album = new Gson().fromJson(jsonArrayAlbums.getJSONObject(i).toString(), Album.class);
                    if(!controller.albumExist(album)) {
                        controller.addAlbum(album);
                    }
                }
                EventBus.getDefault().post(new ServerDoneEvent(true));
            } catch (JSONException e) {
                EventBus.getDefault().post(new ServerDoneEvent(false));
                e.printStackTrace();
            }
        }
    }


}
