package com.example.nemol.googlephotokiller.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.nemol.googlephotokiller.BitmapTransform;
import com.example.nemol.googlephotokiller.R;
import com.squareup.picasso.Picasso;

public class FullImageActivity extends AppCompatActivity {

    ImageView image;
    private static final int MAX_WIDTH = 3840;
    private static final int MAX_HEIGHT = 2160;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        getSupportActionBar().hide();

        image  = (ImageView)findViewById(R.id.full_image);

        Intent i =getIntent();
        String url = i.getStringExtra("url");
        int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));
        Picasso.with(this).load(url).transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                .resize(size, size)
                .centerInside().into(image);


    }
}
