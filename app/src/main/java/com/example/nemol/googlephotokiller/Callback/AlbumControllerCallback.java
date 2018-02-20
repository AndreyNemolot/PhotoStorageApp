package com.example.nemol.googlephotokiller.Callback;

import com.example.nemol.googlephotokiller.Model.Album;

import java.util.ArrayList;

import butterknife.Optional;

/**
 * Created by nemol on 16.02.2018.
 */

public interface AlbumControllerCallback {

    void getAlbumList(int code, ArrayList<Album> albums);
    void addAlbum(int code);
    void deleteAlbum(int code);
}
