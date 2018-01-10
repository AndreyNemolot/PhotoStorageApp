package com.example.nemol.googlephotokiller.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.nemol.googlephotokiller.Adapter.AlbumAdapter;
import com.example.nemol.googlephotokiller.Callback.AlbumListCallback;
import com.example.nemol.googlephotokiller.Callback.PhotoAnswerCallback;
import com.example.nemol.googlephotokiller.Callback.PhotoControllerCallback;
import com.example.nemol.googlephotokiller.Controller.AlbumController;
import com.example.nemol.googlephotokiller.Controller.PhotoController;
import com.example.nemol.googlephotokiller.Fragment.ChoiceAlbumFragment;
import com.example.nemol.googlephotokiller.Fragment.CreateAlbumDialogFragment;
import com.example.nemol.googlephotokiller.Model.ActiveUser;
import com.example.nemol.googlephotokiller.Model.Album;
import com.example.nemol.googlephotokiller.Model.Photo;
import com.example.nemol.googlephotokiller.Adapter.PhotoAdapter;
import com.example.nemol.googlephotokiller.Controller.PreferencesController;
import com.example.nemol.googlephotokiller.R;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.HttpStatus;

public class MainActivity extends AppCompatActivity
        implements PhotoAnswerCallback,
        PhotoControllerCallback, AlbumListCallback{


    @BindView(R.id.imageGallery)
    RecyclerView recyclerView;
    @BindView(R.id.menu)
    FloatingActionMenu menu;
    @BindView(R.id.menu_item_add_album)
    FloatingActionButton fabAddAlbum;
    @BindView(R.id.menu_item_add_photo)
    FloatingActionButton fabAddPhoto;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.progressBarMain)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private List<Photo> photoList;
    private int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        PhotoController.registerCallBack(this, this);
        ChoiceAlbumFragment.registerCallBack(this);
        AlbumController.registerAlbumsCallBack(this);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        toolbar.showOverflowMenu();
       //myToolbar.setTitleTextColor(R.color.lightPrimaryText);

        AlbumController.getAllAlbums();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        PreferencesController preferences = new PreferencesController();
        switch (item.getItemId()){
            case R.id.action_logout:
                ActiveUser.isAuth(false);
                preferences.saveUser(getApplicationContext());
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void setImages() {
        PhotoAdapter adapter = new PhotoAdapter(photoList, new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {
                Intent i = new Intent(MainActivity.this, FullImageActivity.class);
                i.putExtra("url", item);
                startActivity(i);
            }
        }, new PhotoAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(final Photo photo) {


                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
        recyclerView.setAdapter(adapter);
    }


    @OnClick(R.id.menu_item_add_photo)
    public void addPhotoClick(View view) {
        if (ActiveUser.getId() >=0) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                ChoiceAlbumFragment choice = new ChoiceAlbumFragment();
                choice.show(getFragmentManager(), "dlg4");
            }else{
                requestMultiplePermissions();
            }

        } else {
            Snackbar.make(view, "Пользователь не авторизирован", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }


    public void requestMultiplePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                },PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @OnClick(R.id.menu_item_add_album)
    public void createAlbumClick(View view) {
        if (ActiveUser.getId() > 0) {
            CreateAlbumDialogFragment create = new CreateAlbumDialogFragment();
            create.show(getFragmentManager(), "dlg3");
        } else {
            Snackbar.make(view, "Пользователь не авторизирован", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (recyclerView.getAdapter() != null) {
                if ((recyclerView.getAdapter().getClass() == PhotoAdapter.class)) {
                    toolbar.setTitle(getResources().getText(R.string.app_name));
                    AlbumController.registerAlbumsCallBack(this);
                    AlbumController.getAllAlbums();
                }else {
                    super.onBackPressed();
                }
            }else {
                super.onBackPressed();
            }
        }
    }


    @Override
    public void photoAnswer(int code) {
        switch (code) {
            case HttpStatus.SC_ACCEPTED:
                Toast.makeText(this, "Фото загружено в альбом", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                //setImages(); // TODO: 24.12.2017 если в этом альбоме
                break;
            case -1:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case HttpStatus.SC_CREATED:
                Toast.makeText(this, "Фото загружены на телефон", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                setImages();
                break;
            case HttpStatus.SC_OK:
                Toast.makeText(this, "Получен список фотографий", Toast.LENGTH_LONG).show();
                break;
            case 409:
                Toast.makeText(this, "Не удалось загрузить фото", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                break;
            case 204:
                Toast.makeText(this, "Фото удалено", Toast.LENGTH_LONG).show();
                AlbumController.getAllAlbums();
                setImages();
                break;
        }
    }

    @Override
    public void getPhotoList(List<Photo> List) {
        photoList = List;
        PhotoController.downloadPhoto(getApplicationContext(), photoList);
    }

    @Override
    public void albumsList(final ArrayList<Album> albums) {
        AlbumAdapter adapter = new AlbumAdapter(albums, new AlbumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Album item) {
                PhotoController.getAllPhotos(item.getAlbumId());
            }
        }, new AlbumAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(Album item) {
                //PhotoController.deletePhoto(photo);
            }
        });
        recyclerView.setAdapter(adapter);
    }

}
