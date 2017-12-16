package com.example.nemol.googlephotokiller.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
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


import com.example.nemol.googlephotokiller.Callback.PhotoAnswerCallback;
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
        implements NavigationView.OnNavigationItemSelectedListener, CreateAnswerCallback, PhotoAnswerCallback {


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
        ChoiceAlbumFragment.registerCallBack(this);

        setImages();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        loadText();
    }

    void saveUser() {
        SharedPreferences sPref;
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("saved_text", Integer.toString(ActiveUser.getId()));
        ed.putString("login", ActiveUser.getLogin());
        ed.putString("password", ActiveUser.getPassword());
        ed.apply();
        Toast.makeText(this, "User saved", Toast.LENGTH_SHORT).show();
    }

    void loadText() {
        SharedPreferences sPref;
        sPref = getPreferences(MODE_PRIVATE);
        if (!sPref.getString("login", "").equals("")) {
            ActiveUser.setId(Integer.decode(sPref.getString("id", "")));
            ActiveUser.setLogin(sPref.getString("login", ""));
            ActiveUser.setPassword(sPref.getString("password", ""));
            UserController.authorization();
            Toast.makeText(this, "User loaded", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User NOT loaded", Toast.LENGTH_SHORT).show();
        }
    }

    void setImages() {
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
    }

    @OnClick(R.id.menu_item_add_photo)
    public void addPhotoClick(View view) {
        if (ActiveUser.getId() > 0) {
            ChoiceAlbumFragment choice = new ChoiceAlbumFragment();
            choice.show(getFragmentManager(), "dlg4");
        } else {
            Snackbar.make(view, "Пользователь не авторизирован", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
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
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

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
            ActiveUser.isAuth(false);
            setMenuItemAuthorized(false);
            saveUser();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void setMenuItemAuthorized(boolean isAuth) {
        if (isAuth) {
            setNavBarName(ActiveUser.getLogin());
        } else {
            setNavBarName("GPhotoKiller");
        }
        navigationView.getMenu().findItem(R.id.nav_auth).setVisible(!isAuth);
        navigationView.getMenu().findItem(R.id.nav_register).setVisible(!isAuth);
        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(isAuth);
    }

    void setNavBarName(String name) {
        View header = navigationView.findViewById(R.id.navHeader);
        TextView tvName = header.findViewById(R.id.tvUserName);
        tvName.setText(name);
    }

    @Override
    public void createAnswer(int code) {
        switch (code) {
            case 200: //пользователь авторизирован
                setMenuItemAuthorized(true);
                setNavBarName(ActiveUser.getLogin());
                saveUser();
                Toast.makeText(this, "Пользователь авторизован", Toast.LENGTH_LONG).show();
                break;
            case 409:
                Toast.makeText(this, "Что то пошло не так", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void photoAnswer(int code) {
        switch (code) {
            case -1:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case 200:
                Toast.makeText(this, "Фото загружено", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                break;
            case 409:
                Toast.makeText(this, "Не удалось загрузить фото", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                break;
        }
    }

}
