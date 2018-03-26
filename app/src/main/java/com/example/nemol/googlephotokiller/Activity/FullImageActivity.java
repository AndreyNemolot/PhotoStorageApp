package com.example.nemol.googlephotokiller.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;



import com.example.nemol.googlephotokiller.BitmapTransform;
import com.example.nemol.googlephotokiller.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FullImageActivity extends AppCompatActivity {

    @BindView(R.id.full_image)
    ImageView image;
    /*private static final int MAX_WIDTH = 3840;
    private static final int MAX_HEIGHT = 2160;*/
    private URI photoPath;
    private String photoName;
    private String photoLink;
    private final int MAX_WIDTH = 5000;
    private final int MAX_HEIGHT = 3000;
    private ShareActionProvider shareActionProvider;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.full_image_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        setIntent();
        return super.onCreateOptionsMenu(menu);
    }

    private void setIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
      //  Uri uri = Uri.fromFile(new File(photoPath2, photoName));
        File image = new File(photoPath.getPath(), photoName);
        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                "com.example.nemol.fileprovider", image);
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
        shareActionProvider.setShareIntent(shareIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
        photoPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toURI();
        photoName = getIntent().getStringExtra("photoLink");

        photoLink = photoPath.toString() + photoName;
        int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));
        Picasso.with(this).load(photoLink).transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                .resize(size, size)
                .centerInside().into(image);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
