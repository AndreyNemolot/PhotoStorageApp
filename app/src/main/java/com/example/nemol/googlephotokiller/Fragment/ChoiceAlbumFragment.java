package com.example.nemol.googlephotokiller.Fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


import com.example.nemol.googlephotokiller.Callback.AlbumListCallback;
import com.example.nemol.googlephotokiller.Controller.AlbumController;
import com.example.nemol.googlephotokiller.Model.Album;
import com.example.nemol.googlephotokiller.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.choise_album_dialog, null);
        ButterKnife.bind(this, dialogView);
        AlbumController.registerAlbumsCallBack(this);
        AlbumController.getAllAlbums();

        return dialogView;
    }

    @OnClick(R.id.btnYes)
    public void setBtnYes() {
        // TODO: 12.12.2017 отправлять id альбома для загрузки фото
    }

    @OnClick(R.id.btnNo)
    public void setBtnNo() {

    }

    @Override
    public void albumsList(List<Album> albums) {
        ArrayAdapter<Album> adapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, albums);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAlbum.setAdapter(adapter);
    }
}
