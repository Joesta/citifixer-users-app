package com.dso30bt.project2019.potapp.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.dso30bt.project2019.potapp.R;
import com.dso30bt.project2019.potapp.models.Constructor;
import com.dso30bt.project2019.potapp.models.enums.UserEnum;
import com.dso30bt.project2019.potapp.utils.NavUtil;
import com.dso30bt.project2019.potapp.utils.SharedPreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSION_CAMERA_REQUEST_CODE = 1;
    private static final int PERMISSION_PDF_REQUEST_CODE = 22;
    private static final String TAG = "MainActivity";
    private final RxPermissions rxPermissions = new RxPermissions(this); // where this is an Activity or Fragment instance
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    DrawerLayout mDrawer;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private Disposable disposable;
    //navigation header widgets
    private TextView navHeaderName;
    private TextView navHeaderEmail;
    private ImageView headerImageProPic;
    private Menu navMenu;
    private PullRefreshLayout swipeRefreshLayout;
    private List<Constructor> constructorList = null;
    private ListView lvMain;
    private ListView lvConstructorPotholes;
    private ProgressBar progressBarMain;
    //array list adapter
    private ArrayAdapter<Constructor> constructorArrayAdapter;
    private String collection = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        int userType = SharedPreferenceManager.getFlag(this);
        setUserTypeTitle(userType);


        //getUserPotholeReportsFromDatabase();
    }

    private void setUserTypeTitle(int userType) {
        if (userType == UserEnum.CONSTRUCTOR.value) {
            this.setTitle("Reports ( 0 )");
            lvConstructorPotholes.setVisibility(View.VISIBLE);
        }
    }

    private void setNavHeaderInfo(String name, String email, String... imageUrl) {

        navHeaderName.setText(name);
        navHeaderEmail.setText(email);

        if (imageUrl.length > 0) {
            // set navigation header image
            Picasso
                    .get()
                    .load(imageUrl[0])
                    .into(headerImageProPic);
        }
    }

    /**
     * initializes widgets and set properties
     */
    private void initUI() {
        //gets toolbar
        Toolbar toolbar = getToolbar();

        // initializing drawer layout
        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // initializing navigation view and register item selected listener
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navMenu = navigationView.getMenu();

        // get header layout from navigation view
        LinearLayout navHeaderParentLayout = (LinearLayout) navigationView.getHeaderView(0);

        // initializing header widget from navigation header parent layout
        navHeaderName = navHeaderParentLayout.findViewById(R.id.navHeaderFirstName);
        navHeaderEmail = navHeaderParentLayout.findViewById(R.id.navHeaderEmail);
        headerImageProPic = navHeaderParentLayout.findViewById(R.id.navHeaderProPic);

        // initializing recycler view
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        // initializing layout manager for recycler view
        RecyclerView.LayoutManager layoutManager
                = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // initializing floating action button
        FloatingActionButton fab = findViewById(R.id.fab);
        registerListenerOnFab(fab);


        swipeRefreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // getUserPotholeReportsFromDatabase();
            swipeRefreshLayout.setRefreshing(false);
        });

        // list view
        lvMain = findViewById(R.id.lvMain);
        //progressBarMain = findViewById(R.id.progressBarMain);

        lvConstructorPotholes = findViewById(R.id.lvConstructorPothole);

    }

    /***
     * initializes toolbar
     * @return toolbar instance
     */
    private Toolbar getToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    /***
     * register floating action button to click listener
     * @param fab to register click listener to
     */
    private void registerListenerOnFab(FloatingActionButton fab) {
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "Fab tapped", Toast.LENGTH_SHORT).show();

        disposable = rxPermissions
                .request(Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        // I can control the camera now
                        Log.d(TAG, "onClick: I can control the camera now");
                        gotoPotholeImageCaptureActivity();
                    } else {
                        // Oops permission denied
                        Log.d(TAG, "onClick: Oops, permission denied");
                        requestPermission();
                    }
                });
    }


    /***
     * request for camera and storage permission
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause:");
        if (disposable != null) {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int itemId = item.getItemId();

        if (itemId == R.id.nav_profile) {
            gotoUserProfileActivity();

        } else if (itemId == R.id.nav_reports) {
            viewReports();

        } else if (itemId == R.id.nav_share) {
            shareApp();

        } else if (itemId == R.id.nav_about) {
            gotoAboutAppActivity();

        } else if (itemId == R.id.nav_logout) {
            logout();
        }

        //DrawerLayout drawer = findViewById(R.id.drawer_layout);
        mDrawer.closeDrawer(GravityCompat.START);

        return true;
    }

    /***
     * takes user to pothole capture activity
     */
    private void gotoPotholeImageCaptureActivity() {
        NavUtil.moveToNextActivity(MainActivity.this, PotholeImageCaptureActivity.class);
    }

    /***
     * take user to their profile activity
     */
    private void gotoUserProfileActivity() {
        NavUtil.moveToNextActivity(MainActivity.this, UserProfileActivity.class);
    }

    /***
     * logout user and clear stored user information saved locally
     */
    private void logout() {
        gotoLogin();
    }

    /**
     * login
     */
    private void gotoLogin() {
        SharedPreferenceManager.clearSavedLoginInfo(MainActivity.this);
        NavUtil.moveToNextActivity(MainActivity.this, LoginActivity.class);
        finish();
    }

    /**
     * takes user to about app
     */
    private void gotoAboutAppActivity() {
        NavUtil.moveToNextActivity(MainActivity.this, AboutActivity.class);
    }

    /**
     * share app
     */
    private void shareApp() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Try City-Fixer App for convenient way of locating and fixing Potholes");
        sharingIntent.setType("text/plain");
        startActivity(sharingIntent);
    }

    /***
     * view reports
     */
    private void viewReports() {
    }

}
