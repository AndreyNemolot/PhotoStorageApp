package com.example.nemol.googlephotokiller.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nemol.googlephotokiller.Adapter.PhotoAdapter;
import com.example.nemol.googlephotokiller.Callback.PhotoControllerCallback;
import com.example.nemol.googlephotokiller.Controller.PhotoController;
import com.example.nemol.googlephotokiller.ImageFilePath;
import com.example.nemol.googlephotokiller.Model.ActiveUser;
import com.example.nemol.googlephotokiller.Model.Photo;
import com.example.nemol.googlephotokiller.R;
import com.github.clans.fab.FloatingActionButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.HttpStatus;

public class ImagesActivity extends AppCompatActivity implements PhotoControllerCallback {

    @BindView(R.id.imageGallery)
    RecyclerView imageList;
    @BindView(R.id.fab_add_photo)
    FloatingActionButton fabAddPhoto;
    @BindView(R.id.progressBarMain)
    ProgressBar progressBar;
    private List<Photo> photoList;
    private int albumId = 0;
    private String albumTitle;
    private int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageList.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        imageList.setLayoutManager(layoutManager);

        PhotoController.registerPhotoCallBack(this);

        albumId = (Integer) getIntent().getExtras().get("albumId");
        albumTitle = getIntent().getExtras().getString("albumTitle");
        getSupportActionBar().setTitle(albumTitle);
        PhotoController.getPhotoList(albumId);


    }

    @OnClick(R.id.fab_add_photo)
    public void uploadPhotoClick(View view) {
            if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                getImage();
            } else {
                requestMultiplePermissions();
            }
    }

    public void getImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    String selectedImagePath;
                    Uri selectedImageUri = data.getData();
                    selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedImageUri);
                    PhotoController.uploadPhoto(selectedImagePath, albumId);
                    progressBar.setVisibility(View.VISIBLE);
                }
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void requestMultiplePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                }, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getImage();
    }

    @Override
    public void getPhotoList(List<Photo> list) {
        Toast.makeText(this, "Получен список фотографий", Toast.LENGTH_LONG).show();
        photoList = list;
        PhotoController.downloadPhoto(getApplicationContext(), photoList);
        //startService(new Intent(this, DownloadPhotoService.class));
    }

    @Override
    public void uploadPhoto(int code) {
        if (code == HttpStatus.SC_ACCEPTED) {
            Toast.makeText(this, "Фото загружено в альбом", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            PhotoController.getPhotoList(albumId);
        } else {
            Toast.makeText(this, "Не удалось загрузить фото в альбом", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void downloadPhoto(int code) {
        if (code == HttpStatus.SC_CREATED) {
            Toast.makeText(this, "Фото загружено на телефон", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            setImagesList();
        } else {
            Toast.makeText(this, "Не удалось загрузить фото", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void deletePhoto(int code) {
        if (code == HttpStatus.SC_NO_CONTENT) {
            Toast.makeText(this, "Фото удалено", Toast.LENGTH_LONG).show();
            PhotoController.getPhotoList(albumId);
        } else {
            Toast.makeText(this, "Не удалось удалить фото", Toast.LENGTH_LONG).show();
        }
    }

    void setImagesList() {// TODO: 16.02.2018 вынести как в мэйне
        PhotoAdapter adapter = new PhotoAdapter(photoList, new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {
                Intent i = new Intent(ImagesActivity.this, FullImageActivity.class);
                i.putExtra("url", item);
                startActivity(i);
            }
        }, new PhotoAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(final Photo photo) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ImagesActivity.this);
                builder.setTitle("Вы уверены")
                        .setMessage("Хотите удалить эту фотографию?")
                        .setPositiveButton("Да",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        PhotoController.deletePhoto(photo);
                                    }
                                })
                        .setNegativeButton("Нет",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        imageList.setAdapter(adapter);
    }

}
