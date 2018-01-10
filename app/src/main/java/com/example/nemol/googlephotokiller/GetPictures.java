package com.example.nemol.googlephotokiller;

import com.example.nemol.googlephotokiller.Model.Photo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nemol on 05.10.2017.
 */

public class GetPictures {

    ArrayList<String> pictureLinks;
    private final String PHOTO_PATH = "file:///data/data/com.example.nemol.googlephotokiller/cache/";

    public GetPictures(List<Photo> photoList) {
        pictureLinks = new ArrayList<>();
        readLinks(photoList);
    }

    public void readLinks(List<Photo> photoList){

        for(int i=0; i<photoList.size(); i++){
            String[] link = photoList.get(i).getPhotoLink().split("//");
            final String name = link[link.length-1];
            pictureLinks.add(PHOTO_PATH + name);
        }
    }

    public ArrayList<String> getLinks(){
        return pictureLinks;
    }
}
