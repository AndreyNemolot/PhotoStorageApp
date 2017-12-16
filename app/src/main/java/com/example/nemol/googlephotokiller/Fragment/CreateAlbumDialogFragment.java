package com.example.nemol.googlephotokiller.Fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nemol.googlephotokiller.Controller.AlbumController;
import com.example.nemol.googlephotokiller.Callback.CreateAnswerCallback;
import com.example.nemol.googlephotokiller.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nemol on 11.12.2017.
 */

public class CreateAlbumDialogFragment extends DialogFragment implements CreateAnswerCallback {

    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.tvMessage)
    TextView tvMessage;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.create_album_dialog, null);
        ButterKnife.bind(this, dialogView);
        AlbumController.registerAnswerCallBack(this);
        return dialogView;
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
    public void createAnswer(int code) {
        switch (code){
            case 409:
                progressBar.setVisibility(View.GONE);
                tvMessage.setText("Альбом с таким названием существует");
                tvMessage.setVisibility(View.VISIBLE);
                break;
            case 201:
                dismiss();
                break;
        }
    }
}
