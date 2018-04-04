package com.example.nemol.googlephotokiller.Service;

import android.app.IntentService;
import android.content.Intent;

import com.example.nemol.googlephotokiller.Controller.DBPhotoController;
import com.example.nemol.googlephotokiller.Model.Photo;
import com.example.nemol.googlephotokiller.Model.ServerDoneEvent;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;

public class DBPhotoService extends IntentService {

    private final String INTENT_MESSAGE = "jsonArray";

    public DBPhotoService() {
        super("DBPhotoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized (this) {
            String photos = intent.getStringExtra(INTENT_MESSAGE);
            DBPhotoController controller = new DBPhotoController(this);
            try {
                JSONArray jsonArrayPhotos = new JSONArray(photos);
                for (int i = 0; i < jsonArrayPhotos.length(); i++) {
                    Photo photo = new Gson().fromJson(jsonArrayPhotos.getJSONObject(i).toString(), Photo.class);
                    if (!controller.photoExist(photo)) {
                        controller.addPhoto(photo);
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
