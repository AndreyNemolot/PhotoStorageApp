package com.example.nemol.googlephotokiller.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nemol.googlephotokiller.Adapter.PhotoListCursorAdapter;
import com.example.nemol.googlephotokiller.Callback.PhotoControllerCallback;
import com.example.nemol.googlephotokiller.Controller.DBPhotoController;
import com.example.nemol.googlephotokiller.Controller.PhotoController;
import com.example.nemol.googlephotokiller.ImageFilePath;
import com.example.nemol.googlephotokiller.Model.ActiveUser;
import com.example.nemol.googlephotokiller.Model.Photo;
import com.example.nemol.googlephotokiller.Model.ServerDoneEvent;
import com.example.nemol.googlephotokiller.PhotoStoreDBHelper;
import com.example.nemol.googlephotokiller.R;
import com.example.nemol.googlephotokiller.Service.DBPhotoService;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.HttpStatus;

public class ImagesActivity extends AppCompatActivity implements PhotoControllerCallback, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.imageGallery)
    RecyclerView imageList;
    @BindView(R.id.fab_add_photo)
    FloatingActionButton fabAddPhoto;
    @BindView(R.id.fab_take_picture)
    FloatingActionButton fabTakePicture;
    @BindView(R.id.fab_menu)
    FloatingActionMenu fabMenu;
    @BindView(R.id.progressBarMain)
    ProgressBar progressBar;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private PhotoListCursorAdapter cursorAdapter;
    private int albumId;
    private final int PERMISSION_REQUEST_CODE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;
    private final String INTENT_MESSAGE = "jsonArray";
    private String photoName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageList.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        imageList.setLayoutManager(layoutManager);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);


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
            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.setType("image/*");
            startActivityForResult(Intent.createChooser(photoPickerIntent, "Select picture"), 1);
        } else {
            Toast.makeText(this, "Нужно подключение к серверу", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.fab_take_picture)
    public void takePictureClick(View view) {
        if (ActiveUser.isOnline()) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    photoName = photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        } else {
            Toast.makeText(this, "Нужно подключение к серверу", Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    Uri selectedImageUri = data.getData();
                    String selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedImageUri);
                    PhotoController.uploadPhoto(selectedImagePath, albumId);
                    progressBar.setVisibility(View.VISIBLE);
                }
                break;
            }
            case 2: {
                PhotoController.uploadPhoto(photoName, albumId);
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void downloadPhotos(ServerDoneEvent done) {
        if (done.getState()) {
            cursorAdapter.changeCursor(getCursor());
            DBPhotoController controller = new DBPhotoController(this);
            Cursor cursor = controller.getPhotoList(albumId);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    final String link = cursor.getString(1);
                    if (!(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).
                            getAbsolutePath() + File.separator + link).exists())) {
                        int id = cursor.getInt(0);
                        final Photo photo = new Photo(id, link);
                        PhotoController.downloadPhoto(this, photo);
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
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
            mSwipeRefreshLayout.setRefreshing(false);
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

    public Cursor getCursor() { // TODO: 27.03.2018 перенести
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
                                            final String photoPath = builder.getContext().
                                                    getExternalFilesDir(Environment.DIRECTORY_PICTURES).
                                                    getAbsolutePath() + File.separator;
                                            new DBPhotoController(builder.getContext()).deletePhoto(photo);
                                            new File(photoPath + photo.getPhotoLink()).delete();
                                            PhotoController.deletePhoto(builder.getContext(), photo);
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

    @Override
    public void onRefresh() {
        if (ActiveUser.isOnline()) {
            PhotoController.getPhotoList(albumId);
        }else{
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
