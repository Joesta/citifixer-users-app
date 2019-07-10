package com.dso30bt.project2019.potapp.activities;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dso30bt.project2019.potapp.R;
import com.dso30bt.project2019.potapp.adapters.PotholeAdapter;
import com.dso30bt.project2019.potapp.models.Pothole;
import com.dso30bt.project2019.potapp.models.User;
import com.dso30bt.project2019.potapp.utils.Constants;
import com.dso30bt.project2019.potapp.utils.NavUtil;
import com.dso30bt.project2019.potapp.utils.SharedPreferenceManager;
import com.dso30bt.project2019.potapp.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
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

    private static final int PERMISSION_REQUEST_CODE = 1;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        getUserPotholeReportsFromDatabase();
    }

    /***
     * get user reports from firebase database and
     * load them to display on the recycler view
     */
    private void getUserPotholeReportsFromDatabase() {
        final String email = SharedPreferenceManager.getEmail(MainActivity.this);
        Log.d(TAG, "getUserPotholeReportsFromDatabase: Email address is " + email);

        if (email == null) {
            Log.d(TAG, "getUserPotholeReportsFromDatabase: User email is not found!.");
            return;
        }

        db.collection(Constants.USER_COLLECTION)
                .document(email)
                .get()
                .addOnSuccessListener(MainActivity.this, documentSnapshot -> {

                    User user = documentSnapshot.toObject(User.class);
                    assert user != null;

                    List<Pothole> potholeList = user.getPotholes();

                    setNavHeaderInfo(user);

                    //instantiate pothole adapter
                    PotholeAdapter potholeAdapter = new PotholeAdapter(MainActivity.this, potholeList, user.getName());
                    recyclerView.setAdapter(potholeAdapter);

                }).addOnFailureListener(error -> Utils.showToast(MainActivity.this, "Error: " + error.getLocalizedMessage()));
    }

    /**
     * set navigation header info
     *
     * @param user currently logged-in
     */
    private void setNavHeaderInfo(User user) {
        navHeaderName.setText(user.getName());
        navHeaderEmail.setText(user.getEmail());

        //@Todo uncomment when users are able to upload their profile picture
        // set navigation header image
//        Picasso
//                .get()
//                .load(user.getImageUrl)
//                .into(headerImageProPic);
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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // initializing floating action button
        FloatingActionButton fab = findViewById(R.id.fab);
        registerFab(fab);

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
    private void registerFab(FloatingActionButton fab) {
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
                PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause:");
        if (disposable != null) {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }
        super.onPause();
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
        SharedPreferenceManager.clearSavedLoginInfo(MainActivity.this);
        gotoLogin();
    }

    /**
     * login
     */
    private void gotoLogin() {
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
        Utils.showToast(MainActivity.this, "Share menu is tapped");
    }

    /***
     * view reports
     */
    private void viewReports() {

    }
}
