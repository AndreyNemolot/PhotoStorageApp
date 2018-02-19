package com.example.nemol.googlephotokiller.Callback;

import com.example.nemol.googlephotokiller.Model.Photo;

import java.util.List;

/**
 * Created by nemol on 23.12.2017.
 */

public interface PhotoControllerCallback {
    void getPhotoList(List<Photo> photoList);
    void uploadPhoto(int code);
    void downloadPhoto(int code);
    void deletePhoto(int code);

}
