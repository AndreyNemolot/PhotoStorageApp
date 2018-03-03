package com.example.nemol.googlephotokiller.Service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Environment;

import com.example.nemol.googlephotokiller.Controller.DBController;
import com.example.nemol.googlephotokiller.Controller.PhotoController;
import com.example.nemol.googlephotokiller.Model.Album;
import com.example.nemol.googlephotokiller.Model.Photo;
import com.example.nemol.googlephotokiller.PhotoListEvent;
import com.example.nemol.googlephotokiller.R;
import com.example.nemol.googlephotokiller.ServerDoneEvent;
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
            try {
                JSONArray jsonArrayPhotos = new JSONArray(photos);
                List<Photo> photoList = new ArrayList<>();
                for (int i = 0; i < jsonArrayPhotos.length(); i++) {
                    Photo object = new Gson().fromJson(jsonArrayPhotos.getJSONObject(i).toString(), Photo.class);
                    if (!new File(EX_PHOTO_PATH + object.getPhotoLink()).exists()) {
                        photoList.add(object);
                    }
                        ContentValues photoValues = new ContentValues();
                        photoValues.put("_id", object.getPhotoId());
                        photoValues.put("PHOTO_LINK", getResources().getString(R.string.photo_path) + object.getPhotoLink());
                        photoValues.put("ALBUM_ID", object.getAlbumId());
                        new DBController(this).addPhoto(photoValues);
                }
                EventBus.getDefault().post(new PhotoListEvent(photoList));
            } catch (JSONException e) {
                EventBus.getDefault().post(new ServerDoneEvent(false));
                e.printStackTrace();
            }
        }

    }

}
