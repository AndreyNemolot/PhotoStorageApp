package com.example.nemol.googlephotokiller.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.nemol.googlephotokiller.Controller.UserController;
import com.example.nemol.googlephotokiller.Model.ActiveUser;

/**
 * Created by nemol on 11.10.2017.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}