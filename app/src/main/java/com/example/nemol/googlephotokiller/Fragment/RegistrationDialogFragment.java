package com.example.nemol.googlephotokiller.Fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nemol.googlephotokiller.Controller.UserController;
import com.example.nemol.googlephotokiller.Callback.CreateAnswerCallback;
import com.example.nemol.googlephotokiller.Model.User;
import com.example.nemol.googlephotokiller.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nemol on 11.12.2017.
 */

public class RegistrationDialogFragment extends DialogFragment implements CreateAnswerCallback {

    @BindView(R.id.input_login)
    EditText etLogin;
    @BindView(R.id.btnYes)
    Button btnYes;
    @BindView(R.id.btnNo)
    Button btnNo;
    @BindView(R.id.input_password)
    EditText etPassword;
    @BindView(R.id.input_password_check)
    EditText etPasswordCheck;
    @BindView(R.id.password_not_match)
    TextView tvNotMatch;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.register_dialog, null);
        UserController.registerCallBack(this);
        ButterKnife.bind(this, dialogView);
        return dialogView;
    }

    @OnClick(R.id.btnYes)
    public void setBtnYes() {
        tvNotMatch.setVisibility(View.GONE);
        String login = etLogin.getText().toString();
        String password = etPassword.getText().toString();
        if (password.equals(etPasswordCheck.getText().toString())) {
            UserController.registration(new User(login, password));
            progressBar.setVisibility(ProgressBar.VISIBLE);

        } else {
            tvNotMatch.setText(R.string.password_not_match);
            tvNotMatch.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btnNo)
    public void setBtnNo(View view) {
        dismiss();
    }

    void showAnswer(String message){
        tvNotMatch.setText(message);
        tvNotMatch.setVisibility(View.VISIBLE);
    }
    @Override
    public void createAnswer(int code, String action) {
        String message = "Что то пошло не так";
        switch(code){
            case 201:
                progressBar.setVisibility(ProgressBar.GONE);
                dismiss();
                break;
            case 409:
                message = "Не удалось зарегистрировать пользователя";
                progressBar.setVisibility(ProgressBar.GONE);
                //dismiss();
                break;
        }
        showAnswer(message);
    }
}