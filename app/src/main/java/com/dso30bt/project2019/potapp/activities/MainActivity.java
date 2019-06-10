package com.dso30bt.project2019.potapp.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dso30bt.project2019.potapp.R;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";
    private final RxPermissions rxPermissions = new RxPermissions(this); // where this is an Activity or Fragment instance
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize fab
        initUI();
    }

    private void initUI() {
        FloatingActionButton fab = findViewById(R.id.fab);
        //register fab to listener
        registerFab(fab);
    }

    private void registerFab(FloatingActionButton fab) {
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "Fab tapped", Toast.LENGTH_SHORT).show();

        disposable = rxPermissions
                .request(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        // I can control the camera now
                        Log.d(TAG, "onClick: I can control the camera now");
                        gotoImageActivity();
                    } else {
                        // Oops permission denied
                        Log.d(TAG, "onClick: Oops, permission denied");
                        requestPermission();
                    }
                });
    }

    private void gotoImageActivity() {
        startActivity(new Intent(MainActivity.this, ImageActivity.class));
    }

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
        if (!disposable.isDisposed()) {
            disposable.dispose();
        }
        super.onPause();
    }
}
