package com.example.nemol.googlephotokiller.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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


import com.example.nemol.googlephotokiller.Adapter.AlbumListCursorAdapter;
import com.example.nemol.googlephotokiller.Callback.AlbumControllerCallback;
import com.example.nemol.googlephotokiller.Controller.AlbumController;
import com.example.nemol.googlephotokiller.Controller.DBAlbumController;
import com.example.nemol.googlephotokiller.Controller.DBPhotoController;
import com.example.nemol.googlephotokiller.Controller.DBUserController;
import com.example.nemol.googlephotokiller.Model.ServerDoneEvent;
import com.example.nemol.googlephotokiller.Service.DBAlbumService;
import com.example.nemol.googlephotokiller.Fragment.CreateAlbumDialogFragment;
import com.example.nemol.googlephotokiller.Model.ActiveUser;
import com.example.nemol.googlephotokiller.Model.Album;
import com.example.nemol.googlephotokiller.PhotoStoreDBHelper;
import com.example.nemol.googlephotokiller.R;

import com.github.clans.fab.FloatingActionButton;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.HttpStatus;

public class MainActivity extends AppCompatActivity
        implements AlbumControllerCallback, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.list_album)
    RecyclerView albumList;
    @BindView(R.id.fab_add_album)
    FloatingActionButton fabAddAlbum;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private String INTENT_MESSAGE = "jsonArray";
    private AlbumListCursorAdapter cursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        setAlbumList();
        if (ActiveUser.isOnline()) {
            AlbumController.getAllAlbums();
        }

        albumList.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        albumList.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActiveUser.isOnline()) {
            EventBus.getDefault().register(this);
            CreateAlbumDialogFragment.registerAlbumCallBack(this);
            AlbumController.registerAlbumCallBack(this);
        }
    }

    @Override
    protected void onStop() {
        if (ActiveUser.isOnline()) {
            EventBus.getDefault().unregister(this);
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                new DBUserController(this).deleteUser();
                ActiveUser.isAuth(false);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.fab_add_album)
    public void createAlbumClick(View view) {
        if (ActiveUser.isOnline()) {
            CreateAlbumDialogFragment create = new CreateAlbumDialogFragment();
            create.show(getFragmentManager(), "dlg3");
        } else {
            Toast.makeText(this, "Нужно подключение к серверу", Toast.LENGTH_LONG).show();
        }
    }

    public void setAlbumList() {
        Cursor cursor = new DBAlbumController(this).getAllAlbums(ActiveUser.getId());
        cursorAdapter = new AlbumListCursorAdapter(this, cursor);
        cursorAdapter.setListener(new AlbumListCursorAdapter.Listener() {
            @Override
            public void onClick(Album album) {
                openImagesActivity(album);
            }

            @Override
            public void onLongClick(final Album album) {
                final int albumId = album.getAlbumId();
                if (ActiveUser.isOnline()) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Вы уверены")
                            .setMessage("Хотите удалить альбом и все фотографии в нём?")
                            .setPositiveButton("Да",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            new DBPhotoController(builder.getContext()).deletePhotosInAlbum(albumId);
                                            AlbumController.deleteAlbum(builder.getContext(), albumId);
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
        albumList.setAdapter(cursorAdapter);
        //cursor.close();
        //db.close();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ServerDoneEvent event) {
        if (event.getState()) {
            Cursor cursor = new DBAlbumController(this).getAllAlbums(ActiveUser.getId());
            cursorAdapter.changeCursor(cursor);
            cursor.close();
        } else {
            Toast.makeText(this, "Не удалось получить список альбомов", Toast.LENGTH_LONG).show();
        }
    }

    public void openImagesActivity(Album album) {
        Intent intent = new Intent(this, ImagesActivity.class);
        intent.putExtra("albumId", album.getAlbumId());
        intent.putExtra("albumTitle", album.getAlbumTitle());
        startActivity(intent);
    }

    @Override
    public void getAlbumList(int code, JSONArray albums) {
        if (code == HttpStatus.SC_OK) {
            Intent intent = new Intent(this, DBAlbumService.class);
            intent.putExtra(INTENT_MESSAGE, albums.toString());
            startService(intent);
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, "Получен список альбомов", Toast.LENGTH_LONG).show();
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, "Не удалось получить список альбомов", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void addAlbum(int code) {
        Toast.makeText(this, "Альбом добавлен", Toast.LENGTH_LONG).show();
        AlbumController.registerAlbumCallBack(this);
        AlbumController.getAllAlbums();
    }

    @Override
    public void deleteAlbum(int code, int albumId) {
        if (code == HttpStatus.SC_OK) {
            Toast.makeText(this, "Альбом удалён", Toast.LENGTH_LONG).show();
            Cursor cursor = new DBAlbumController(this).getAllAlbums(ActiveUser.getId());
            cursorAdapter.changeCursor(cursor);
            cursor.close();
        }
    }

    @Override
    public void onRefresh() {
        if (ActiveUser.isOnline()) {
            AlbumController.getAllAlbums();
        }else{
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
