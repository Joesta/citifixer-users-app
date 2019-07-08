package com.dso30bt.project2019.potapp.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.dso30bt.project2019.potapp.adapters.PotholeAdapter;
import com.dso30bt.project2019.potapp.models.Pothole;
import com.dso30bt.project2019.potapp.models.User;
import com.dso30bt.project2019.potapp.utils.Constants;
import com.dso30bt.project2019.potapp.utils.SharedPreferenceManager;
import com.dso30bt.project2019.potapp.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.dso30bt.project2019.potapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";
    private final RxPermissions rxPermissions = new RxPermissions(this); // where this is an Activity or Fragment instance
    private Disposable disposable;

    //firebase
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize fab
        //initUI();

        db.collection(Constants.USER_COLLECTION)
                .document(SharedPreferenceManager.getUserEmail(MainActivity.this))
                .get()
                .addOnSuccessListener(MainActivity.this, documentSnapshot -> {

                    User user = documentSnapshot.toObject(User.class);
                    assert user != null;
                    List<Pothole> potholeList = user.getPotholes();

                    initUI();
                    recyclerView = findViewById(R.id.recycler_view);
                    recyclerView.setHasFixedSize(true);

                    PotholeAdapter potholeAdapter = new PotholeAdapter(MainActivity.this, potholeList, user.getName());
                    layoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(potholeAdapter);




                }).addOnFailureListener(error -> Utils.showToast(MainActivity.this, "Error: " + error.getLocalizedMessage()));
    }

    private void initUI() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);

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
        Intent i = new Intent(MainActivity.this, ImageActivity.class);

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
        if (!disposable.isDisposed() && disposable != null) {
            disposable.dispose();
        }
        super.onPause();
    }
}
