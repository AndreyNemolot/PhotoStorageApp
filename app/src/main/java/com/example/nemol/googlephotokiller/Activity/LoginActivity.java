package com.example.nemol.googlephotokiller.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.nemol.googlephotokiller.Callback.UserControllerCallback;
import com.example.nemol.googlephotokiller.Controller.UserController;
import com.example.nemol.googlephotokiller.Model.ActiveUser;
import com.example.nemol.googlephotokiller.Model.User;
import com.example.nemol.googlephotokiller.Controller.PreferencesController;
import com.example.nemol.googlephotokiller.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.HttpStatus;


public class LoginActivity extends AppCompatActivity implements UserControllerCallback {

    @BindView(R.id.email)
    AutoCompleteTextView mEmailView;
    @BindView(R.id.password)
    EditText mPasswordView;
    @BindView(R.id.login_progress)
    View mProgressView;
    @BindView(R.id.login_form)
    View mLoginFormView;
    @BindView(R.id.login)
    Button btnLogin;
    @BindView(R.id.registration)
    Button btnRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        UserController.registerCallBack(this);
        ButterKnife.bind(this);

        PreferencesController preferences = new PreferencesController();
        if(preferences.loadText(getApplicationContext())){
            showProgress(true);
            UserController.authorization();
        }
    }

    @OnClick(R.id.login)
    public void login(View view) {
        attemptLogin(true);
    }

    @OnClick(R.id.registration)
    public void registration(final View view) {

        final String password = mPasswordView.getText().toString();
        final EditText input = new EditText(LoginActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        input.setLayoutParams(lp);

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Сообщение")
                .setMessage("Подтвердите пароль")
                .setPositiveButton("Подтверждаю",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(password.equals(input.getText().toString())) {
                                    attemptLogin(false);
                                    dialog.cancel();
                                }else{
                                    Toast.makeText(LoginActivity.this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

        AlertDialog alert = builder.create();
        alert.setView(input);
        alert.show();
    }

    private void attemptLogin(boolean isLogin) {

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String login = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(login)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            if(isLogin){
                ActiveUser.saveUser(login, password);
                UserController.authorization();
            }else{
                UserController.registration(new User(login, password));
            }

        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public void userAction(int code) {
        if (code == HttpStatus.SC_OK){
            PreferencesController preferences = new PreferencesController();
            preferences.saveUser(getApplicationContext());
            Toast.makeText(this, "Пользователь авторизован", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else{
            showProgress(false);
            Toast.makeText(this, "Что то пошло не так", Toast.LENGTH_LONG).show();
        }
    }

}

