package com.example.nemol.googlephotokiller.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;


import com.example.nemol.googlephotokiller.Adapter.AlbumAdapter;
import com.example.nemol.googlephotokiller.Callback.AlbumControllerCallback;
import com.example.nemol.googlephotokiller.Controller.AlbumController;
import com.example.nemol.googlephotokiller.Controller.PhotoController;
import com.example.nemol.googlephotokiller.Fragment.CreateAlbumDialogFragment;
import com.example.nemol.googlephotokiller.Model.ActiveUser;
import com.example.nemol.googlephotokiller.Model.Album;
import com.example.nemol.googlephotokiller.Controller.PreferencesController;
import com.example.nemol.googlephotokiller.R;

import com.github.clans.fab.FloatingActionButton;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.HttpStatus;

public class MainActivity extends AppCompatActivity
        implements AlbumControllerCallback {


    @BindView(R.id.list_album)
    RecyclerView albumList;
    @BindView(R.id.fab_add_album)
    FloatingActionButton fabAddAlbum;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        CreateAlbumDialogFragment.registerAlbumCallBack(this);
        AlbumController.registerAlbumCallBack(this);

        albumList.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        albumList.setLayoutManager(layoutManager);

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
        switch (item.getItemId()) {
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

    @OnClick(R.id.fab_add_album)
    public void createAlbumClick(View view) {
            CreateAlbumDialogFragment create = new CreateAlbumDialogFragment();
            create.show(getFragmentManager(), "dlg3");
    }

    public void setAlbumList(ArrayList<Album> albums) {
        AlbumAdapter adapter = new AlbumAdapter(albums, new AlbumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Album item) {
                openImagesActivity(item);
            }
        }, new AlbumAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(final Album item) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Вы уверены")
                        .setMessage("Хотите удалить альбом и все фотографии в нём?")
                        .setPositiveButton("Да",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        AlbumController.deleteAlbum(item.getAlbumId());
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
        albumList.setAdapter(adapter);
    }

    public void openImagesActivity(Album item) {
        Intent intent = new Intent(this, ImagesActivity.class);
        intent.putExtra("albumId", item.getAlbumId());
        intent.putExtra("albumTitle", item.getAlbumTitle());
        startActivity(intent);
    }

    @Override
    public void getAlbumList(int code, ArrayList<Album> albums) {
        if (code == HttpStatus.SC_OK) {
            Toast.makeText(this, "Получен список альбомов", Toast.LENGTH_LONG).show();
            setAlbumList(albums);
        } else {
            Toast.makeText(this, "Не удалось получить список альбомов", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void addAlbum(int code) {
        Toast.makeText(this, "!!!!!", Toast.LENGTH_LONG).show();
        AlbumController.registerAlbumCallBack(this);
        AlbumController.getAllAlbums();
    }

    @Override
    public void deleteAlbum(int code) {
        Toast.makeText(this, "Альбом удалён", Toast.LENGTH_LONG).show();
        AlbumController.registerAlbumCallBack(this);
        AlbumController.getAllAlbums();
    }

}
