package com.example.nemol.googlephotokiller;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.nemol.googlephotokiller.Controller.UserController;
import com.example.nemol.googlephotokiller.Model.ActiveUser;
import com.example.nemol.googlephotokiller.Model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nemol on 14.11.2017.
 */

public class AuthorizationDialogFragment extends DialogFragment {
    @BindView(R.id.input_login)
    EditText etLogin;
    @BindView(R.id.input_password)
    EditText etPassword;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.auth_dialog, null);
        ButterKnife.bind(this, dialogView);
        return dialogView;
    }

    @OnClick(R.id.btnYes)
    public void setBtnYes(View view) {
        ActiveUser.setLogin(etLogin.getText().toString());
        ActiveUser.setPassword(etPassword.getText().toString());
        UserController.authorization();
        // TODO: 12.11.2017 создать activeUsr
        // TODO: 12.11.2017 получать список фотографий
        dismiss();
    }

    @OnClick(R.id.btnNo)
    public void setBtnNo(View view) {
        dismiss();
    }

}
