package com.example.nemol.googlephotokiller.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.nemol.googlephotokiller.Controller.PhotoController;
import com.example.nemol.googlephotokiller.Callback.CreateAnswerCallback;
import com.example.nemol.googlephotokiller.Fragment.AuthorizationDialogFragment;
import com.example.nemol.googlephotokiller.Fragment.ChoiceAlbumFragment;
import com.example.nemol.googlephotokiller.Fragment.CreateAlbumDialogFragment;
import com.example.nemol.googlephotokiller.Fragment.RegistrationDialogFragment;
import com.example.nemol.googlephotokiller.GetPictures;
import com.example.nemol.googlephotokiller.Model.ActiveUser;
import com.example.nemol.googlephotokiller.MyAdapter;
import com.example.nemol.googlephotokiller.R;

import com.example.nemol.googlephotokiller.Controller.UserController;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;


import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CreateAnswerCallback {


    @BindView(R.id.imageGallery)
    RecyclerView recyclerView;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
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
    private MyAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        UserController.registerCallBack(this);
        PhotoController.registerCallBack(this);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        File linksFile = new File("/storage/emulated/0/wallpapers/links.txt");
        GetPictures pictures = new GetPictures(linksFile);
        adapter = new MyAdapter(pictures.getLinks(), new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {
                Intent i = new Intent(MainActivity.this, FullImageActivity.class);
                i.putExtra("url", item);
                startActivity(i);
            }
        });
        recyclerView.setAdapter(adapter);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @OnClick(R.id.menu_item_add_photo)
    public void addPhotoClick(View view) {
        if (ActiveUser.getLogin() != null) {
            ChoiceAlbumFragment choise = new ChoiceAlbumFragment();
            choise.show(getFragmentManager(), "dlg4");
            /*getImage();
            progressBar.setVisibility(View.VISIBLE); // TODO: 05.12.2017 перенести туда где реально начинается загрузка
            Snackbar.make(view, "Загрузка фото", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();*/
        } else {
            Snackbar.make(view, "Пользователь не авторизирован", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @OnClick(R.id.menu_item_add_album)
    public void createAlbumClick(View view) {
        if (ActiveUser.getLogin() != null) {
            CreateAlbumDialogFragment create = new CreateAlbumDialogFragment();
            create.show(getFragmentManager(), "dlg3");
        }else{
            Snackbar.make(view, "Пользователь не авторизирован", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public void getImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    Uri chosenImageUri = data.getData().normalizeScheme();
                    PhotoController.uploadPhoto(getApplicationContext(),chosenImageUri);
                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_album) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_auth) {

            AuthorizationDialogFragment auth = new AuthorizationDialogFragment();
            auth.show(getFragmentManager(), "dlg2");
        } else if (id == R.id.nav_register) {
            RegistrationDialogFragment reg = new RegistrationDialogFragment();
            reg.show(getFragmentManager(), "dlg1");
        } else if (id == R.id.nav_logout) {
            ActiveUser.setLogin(null);
            ActiveUser.setPassword(null);
            setMenuItem(false);
            setName("GPhotoKiller");

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void setMenuItem(boolean isAuth) {
        navigationView.getMenu().findItem(R.id.nav_auth).setVisible(!isAuth);
        navigationView.getMenu().findItem(R.id.nav_register).setVisible(!isAuth);
        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(isAuth);
    }

    void setName(String name) {
        View header = navigationView.findViewById(R.id.navHeader);
        TextView tvName = (TextView) header.findViewById(R.id.tvUserName);
        tvName.setText(name);
    }

    @Override
    public void createAnswer(int code, String action) {
        switch (code) {
            case 200: //пользователь авторизирован
                if (action.equals("authorization")) {
                    setMenuItem(true);
                    setName(ActiveUser.getLogin());
                    Toast.makeText(this, "Пользователь авторизован", Toast.LENGTH_LONG).show();
                } else if (action.equals("upload")) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Фотография загружена", Toast.LENGTH_LONG).show();
                }
                break;
            case 409:
                if (action.equals("upload")) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Не удалось агрузить фотографию", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
