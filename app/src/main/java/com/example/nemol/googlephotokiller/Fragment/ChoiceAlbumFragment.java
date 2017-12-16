package com.example.nemol.googlephotokiller.Fragment;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


import com.example.nemol.googlephotokiller.Callback.AlbumListCallback;
import com.example.nemol.googlephotokiller.Callback.PhotoAnswerCallback;
import com.example.nemol.googlephotokiller.Controller.AlbumController;
import com.example.nemol.googlephotokiller.Controller.PhotoController;
import com.example.nemol.googlephotokiller.Model.Album;
import com.example.nemol.googlephotokiller.R;

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

    public static void registerCallBack(PhotoAnswerCallback callback) {
        progressBarActive = callback;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    Uri chosenImageUri = data.getData().normalizeScheme();
                    PhotoController.uploadPhoto(getActivity(),chosenImageUri, getAlbumId());
                    progressBarActive.photoAnswer(-1);
                    dismiss();
                }
                break;
            }
        }
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
    public void albumsList(List<Album> albums) {
        ArrayAdapter<Album> adapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, albums);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAlbum.setAdapter(adapter);
    }
}
