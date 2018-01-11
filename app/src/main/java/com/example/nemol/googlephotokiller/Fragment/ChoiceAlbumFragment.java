package com.example.nemol.googlephotokiller.Fragment;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


import com.example.nemol.googlephotokiller.Callback.AlbumListCallback;
import com.example.nemol.googlephotokiller.Callback.PhotoAnswerCallback;
import com.example.nemol.googlephotokiller.Callback.UriToPathCallback;
import com.example.nemol.googlephotokiller.Controller.AlbumController;
import com.example.nemol.googlephotokiller.Controller.PhotoController;
import com.example.nemol.googlephotokiller.ImageFilePath;
import com.example.nemol.googlephotokiller.Model.Album;
import com.example.nemol.googlephotokiller.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by nemol on 10.12.2017.
 */


public class ChoiceAlbumFragment extends DialogFragment implements AlbumListCallback {

    @BindView(R.id.spAlbum)
    Spinner spAlbum;
    @BindView(R.id.btnYes)
    Button btnYes;
    @BindView(R.id.btnNo)
    Button btnNo;
    static PhotoAnswerCallback progressBarActive;
    static UriToPathCallback pathCallback;


    public static void registerCallBack(PhotoAnswerCallback callback) {
        progressBarActive = callback;
    }

    public static void registerUriCallBack(UriToPathCallback callback) {
        pathCallback = callback;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.choise_album_dialog, null);
        ButterKnife.bind(this, dialogView);
        AlbumController.registerAlbumsCallBack(this);
        AlbumController.getAllAlbums();
        return dialogView;
    }

    public void getImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    void upload(Uri uri){

        String path = pathCallback.getPath(uri);
        System.out.println("URIPATH: " + path);
        PhotoController.uploadPhoto(path, getAlbumId());
        progressBarActive.photoAnswer(-1);
        dismiss();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    String selectedImagePath;
                    Uri selectedImageUri = data.getData();
                    selectedImagePath = ImageFilePath.getPath(getActivity(), selectedImageUri);
                    //System.out.println("URIPATH: " + chosenImageUri);
                    PhotoController.uploadPhoto(selectedImagePath, getAlbumId());
                    progressBarActive.photoAnswer(-1);
                    dismiss();
                }
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public int getAlbumId(){
        Album album = (Album)spAlbum.getSelectedItem();
        return album.getAlbumId();
    }

    @OnClick(R.id.btnYes)
    public void setBtnYes() {
        getImage();
    }

    @OnClick(R.id.btnNo)
    public void setBtnNo() {
        dismiss();
    }

    @Override
    public void albumsList(ArrayList<Album> albums) {
        ArrayAdapter<Album> adapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, albums);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAlbum.setAdapter(adapter);
    }
}
