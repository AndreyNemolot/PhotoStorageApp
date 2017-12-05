package com.example.nemol.googlephotokiller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by nemol on 05.10.2017.
 */

public class GetPictures {

    ArrayList<String> pictureLinks;

    String[] images = {"file:///storage/emulated/0/wallpapers/2.jpg","file:///storage/emulated/0/wallpapers/3.jpg","file:///storage/emulated/0/wallpapers/4.jpg","file:///storage/emulated/0/wallpapers/5.jpg",
            "file:///storage/emulated/0/wallpapers/6.jpg", "file:///storage/emulated/0/wallpapers/7.jpg","file:///storage/emulated/0/wallpapers/8.jpg","file:///storage/emulated/0/wallpapers/9.jpg","file:///storage/emulated/0/wallpapers/10.jpg"};

    public GetPictures(File file) {
        pictureLinks = new ArrayList<>();
        readLinks(file);
    }

    public void readLinks(File linksFile){
        /*try {
            FileReader fileReader = new FileReader(linksFile);

            BufferedReader reader = new BufferedReader(fileReader);

            String line = null;
            while((line = reader.readLine()) != null){
                pictureLinks.add(reader.readLine());
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }*/
        for(int i=0; i<images.length; i++){
            pictureLinks.add(images[i]);
        }
    }

    public ArrayList<String> getLinks(){
        return pictureLinks;
    }
}
