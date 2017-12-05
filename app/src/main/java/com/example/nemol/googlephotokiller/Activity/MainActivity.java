package com.example.nemol.googlephotokiller.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import com.example.nemol.googlephotokiller.AuthorizationDialogFragment;
import com.example.nemol.googlephotokiller.Controller.PhotoController;
import com.example.nemol.googlephotokiller.CreateAnswerCallback;
import com.example.nemol.googlephotokiller.GetPictures;
import com.example.nemol.googlephotokiller.Model.ActiveUser;
import com.example.nemol.googlephotokiller.MyAdapter;
import com.example.nemol.googlephotokiller.R;
import com.example.nemol.googlephotokiller.RegistrationDialogFragment;
import com.example.nemol.googlephotokiller.Controller.UserController;


import java.io.File;
import java.util.zip.Inflater;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CreateAnswerCallback {

    @BindView(R.id.imageGallery)
    RecyclerView recyclerView;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
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

    @OnClick(R.id.fab)
    public void fabClick(View view) {
        if(ActiveUser.getLogin()!=null) {
            PhotoController.uploadPhoto();
            progressBar.setVisibility(View.VISIBLE);
            Snackbar.make(view, "Загрузка фото", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else{
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
