package com.example.nemol.googlephotokiller.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nemol.googlephotokiller.Adapter.PhotoListCursorAdapter;
import com.example.nemol.googlephotokiller.Callback.PhotoControllerCallback;
import com.example.nemol.googlephotokiller.Controller.PhotoController;
import com.example.nemol.googlephotokiller.ImageFilePath;
import com.example.nemol.googlephotokiller.Model.ActiveUser;
import com.example.nemol.googlephotokiller.Model.Photo;
import com.example.nemol.googlephotokiller.PhotoListEvent;
import com.example.nemol.googlephotokiller.PhotoStoreDBHelper;
import com.example.nemol.googlephotokiller.R;
import com.example.nemol.googlephotokiller.ServerDoneEvent;
import com.example.nemol.googlephotokiller.Service.DBPhotoService;
import com.github.clans.fab.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

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
    private int albumId = 0;
    private int PERMISSION_REQUEST_CODE = 1;
    private String INTENT_MESSAGE = "jsonArray";
    private PhotoListCursorAdapter cursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageList.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        imageList.setLayoutManager(layoutManager);

        albumId = getIntent().getExtras().getInt("albumId");
        String albumTitle = getIntent().getExtras().getString("albumTitle");

        getSupportActionBar().setTitle(albumTitle);

        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)) {
            requestMultiplePermissions();
        }
        if (ActiveUser.isOnline()) {
            PhotoController.getPhotoList(albumId);
        }
        setImagesList();

    }

    public void requestMultiplePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                }, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActiveUser.isOnline()) {
            EventBus.getDefault().register(this);
            PhotoController.registerPhotoCallBack(this);
        }
    }

    @Override
    protected void onStop() {
        if (ActiveUser.isOnline()) {
            EventBus.getDefault().unregister(this);
        }
        super.onStop();
    }

    @OnClick(R.id.fab_add_photo)
    public void uploadPhotoClick(View view) {
        if (ActiveUser.isOnline()) {
                getImage();
        } else {
            Toast.makeText(this, "Нужно подключение к серверу", Toast.LENGTH_LONG).show();
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PhotoListEvent event) {
        cursorAdapter.changeCursor(getCursor());
        PhotoController.downloadPhoto(this, event.getPhotoList());
    }


    @Override
    public void getPhotoList(int code, JSONArray photos) {
        if (code == HttpStatus.SC_OK) {

            Intent intent = new Intent(this, DBPhotoService.class);
            intent.putExtra(INTENT_MESSAGE, photos.toString());
            startService(intent);
            cursorAdapter.changeCursor(getCursor());
            Toast.makeText(this, "Получен список фотографий", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "Не удалось получить список фотографий", Toast.LENGTH_LONG).show();
        }
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
            cursorAdapter.changeCursor(getCursor());
        } else {
            Toast.makeText(this, "Не удалось загрузить фото", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void deletePhoto(int code) {
        if (code == HttpStatus.SC_NO_CONTENT) {
            Toast.makeText(this, "Фото удалено", Toast.LENGTH_LONG).show();
            cursorAdapter.changeCursor(getCursor());
            PhotoController.getPhotoList(albumId);
        } else {
            Toast.makeText(this, "Не удалось удалить фото", Toast.LENGTH_LONG).show();
        }
    }

    public Cursor getCursor() {
        SQLiteOpenHelper DBHelper = new PhotoStoreDBHelper(this);
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        return db.query("PHOTOS", new String[]{"_id", "PHOTO_LINK", "ALBUM_ID"},
                "ALBUM_ID = ?", new String[]{Integer.toString(albumId)},
                null, null, null);
    }

    void setImagesList() {
        cursorAdapter = new PhotoListCursorAdapter(this, getCursor());
        cursorAdapter.setListener(new PhotoListCursorAdapter.Listener() {
            @Override
            public void onClick(Photo photo) {
                Intent i = new Intent(ImagesActivity.this, FullImageActivity.class);
                i.putExtra("photoLink", photo.getPhotoLink());
                startActivity(i);
            }

            @Override
            public void onLongClick(final Photo photo) {
                if (ActiveUser.isOnline()) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ImagesActivity.this);
                    builder.setTitle("Вы уверены")
                            .setMessage("Хотите удалить фотографию?")
                            .setPositiveButton("Да",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            PhotoController.deletePhoto(photo, builder.getContext());
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
            }
        });
        imageList.setAdapter(cursorAdapter);
    }

}
