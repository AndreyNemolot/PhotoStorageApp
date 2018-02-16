package com.example.nemol.googlephotokiller.Activity;

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
import com.example.nemol.googlephotokiller.Callback.AlbumListCallback;
import com.example.nemol.googlephotokiller.Callback.PhotoAnswerCallback;
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
        implements PhotoAnswerCallback,
        AlbumListCallback {


    @BindView(R.id.list_album)
    RecyclerView albumList;
    @BindView(R.id.fab_add_album)
    FloatingActionButton fabAddAlbum;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    /*@BindView(R.id.progressBarMain)
    ProgressBar progressBar;*/
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        PhotoController.registerProgressBarCallBack(this);
        AlbumController.registerAlbumsCallBack(this);

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
        if (ActiveUser.getId() > 0) {
            CreateAlbumDialogFragment create = new CreateAlbumDialogFragment();
            create.show(getFragmentManager(), "dlg3");
        } else {
            Snackbar.make(view, "Пользователь не авторизирован", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public void photoAnswer(int code) {
        switch (code) {
            case HttpStatus.SC_ACCEPTED:
                Toast.makeText(this, "Фото загружено в альбом", Toast.LENGTH_LONG).show();
                //progressBar.setVisibility(View.GONE);
                //setImages(); // TODO: 24.12.2017 если в этом альбоме
                break;
            case -1:
                //progressBar.setVisibility(View.VISIBLE);
                break;
            case HttpStatus.SC_CREATED:
                Toast.makeText(this, "Фото загружены на телефон", Toast.LENGTH_LONG).show();
                //progressBar.setVisibility(View.GONE);
                //setImages();
                break;
            case HttpStatus.SC_OK:
                Toast.makeText(this, "Получен список фотографий", Toast.LENGTH_LONG).show();
                break;
            case 409:
                Toast.makeText(this, "Не удалось загрузить фото", Toast.LENGTH_LONG).show();
                //progressBar.setVisibility(View.GONE);
                break;
            case 204:
                Toast.makeText(this, "Фото удалено", Toast.LENGTH_LONG).show();
                AlbumController.getAllAlbums();
                //setImages();
                break;
        }
    }

    @Override
    public void albumsList(final ArrayList<Album> albums) {
        final Intent intent = new Intent(this, ImagesActivity.class);

        AlbumAdapter adapter = new AlbumAdapter(albums, new AlbumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Album item) {
                intent.putExtra("albumId", item.getAlbumId());
                intent.putExtra("albumTitle", item.getAlbumTitle());
                //PhotoController.getPhotoList(item.getAlbumId());
                startActivity(intent);

            }
        }, new AlbumAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(Album item) {
                //PhotoController.deletePhoto(photo);
            }
        });
        albumList.setAdapter(adapter);
    }

}
