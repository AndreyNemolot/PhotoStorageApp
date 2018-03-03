package com.example.nemol.googlephotokiller;

import com.example.nemol.googlephotokiller.Model.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nemol on 04.03.2018.
 */

public class PhotoListEvent {

    private List<Photo> photoList;

    public PhotoListEvent(List<Photo> photoList) {
        this.photoList = photoList;
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }
}
