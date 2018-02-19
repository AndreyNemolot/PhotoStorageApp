package com.example.nemol.googlephotokiller.Fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nemol.googlephotokiller.Callback.AlbumControllerCallback;
import com.example.nemol.googlephotokiller.Controller.AlbumController;
import com.example.nemol.googlephotokiller.Model.Album;
import com.example.nemol.googlephotokiller.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.HttpStatus;

/**
 * Created by nemol on 11.12.2017.
 */

public class CreateAlbumDialogFragment extends DialogFragment implements AlbumControllerCallback {

    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.tvMessage)
    TextView tvMessage;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private static AlbumControllerCallback albumCallback;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.create_album_dialog, null);
        ButterKnife.bind(this, dialogView);
        AlbumController.registerAlbumCallBack(this);
        return dialogView;
    }

    public static void registerAlbumCallBack(AlbumControllerCallback callback) {
        albumCallback = callback;
    }

    @OnClick(R.id.btnYes)
    public void setBtnYes() {
        tvMessage.setVisibility(View.GONE);
        String title = etTitle.getText().toString();
        if(!title.equals("")){
            progressBar.setVisibility(View.VISIBLE);
            AlbumController.createAlbum(title);
        }else{
            tvMessage.setText("Нужно ввести название");
            tvMessage.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btnNo)
    public void setBtnNo() {
        dismiss();
    }

    @Override
    public void addAlbum(int code) {
        switch (code){
            case HttpStatus.SC_CONFLICT:
                progressBar.setVisibility(View.GONE);
                tvMessage.setText("Альбом с таким названием существует");
                tvMessage.setVisibility(View.VISIBLE);
                break;
            case HttpStatus.SC_CREATED:
                AlbumController.getAllAlbums();
                progressBar.setVisibility(View.GONE);
                albumCallback.addAlbum(HttpStatus.SC_CREATED);
                dismiss();
                break;
        }
    }

    @Override
    public void getAlbum() {

    }

    @Override
    public void getAlbumList(int code, ArrayList<Album> albums) {

    }

    @Override
    public void deleteAlbum(int code) {

    }
}
