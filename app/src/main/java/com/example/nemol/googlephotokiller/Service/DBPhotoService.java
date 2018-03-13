package com.example.nemol.googlephotokiller.Service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Environment;

import com.example.nemol.googlephotokiller.Controller.DBController;
import com.example.nemol.googlephotokiller.Model.Photo;
import com.example.nemol.googlephotokiller.Model.PhotoListEvent;
import com.example.nemol.googlephotokiller.R;
import com.example.nemol.googlephotokiller.Model.ServerDoneEvent;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class DBPhotoService extends IntentService {

    private final String INTENT_MESSAGE = "jsonArray";
    private final String EX_PHOTO_PATH = Environment.getExternalStorageDirectory() + File.separator + "GooglePhotoKiller/";

    public DBPhotoService() {
        super("DBPhotoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized (this) {
            String photos = intent.getStringExtra(INTENT_MESSAGE);
            DBController controller = new DBController(this);
            try {
                JSONArray jsonArrayPhotos = new JSONArray(photos);
                List<Photo> downloadPhotoList = new ArrayList<>();
                for (int i = 0; i < jsonArrayPhotos.length(); i++) {
                    Photo photo = new Gson().fromJson(jsonArrayPhotos.getJSONObject(i).toString(), Photo.class);
                    if (!new File(EX_PHOTO_PATH + photo.getPhotoLink()).exists()) {
                        downloadPhotoList.add(photo); // TODO: 13.03.2018 сделать иначе, перенести проверку там где скачивает
                    }
                    if (!controller.photoExist(photo)) {
                        controller.addPhoto(photo);
                    }
                }
                EventBus.getDefault().post(new PhotoListEvent(downloadPhotoList));
            } catch (JSONException e) {
                EventBus.getDefault().post(new ServerDoneEvent(false));
                e.printStackTrace();
            }
        }

    }

}
