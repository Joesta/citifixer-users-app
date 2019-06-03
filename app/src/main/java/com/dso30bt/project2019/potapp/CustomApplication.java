package com.dso30bt.project2019.potapp;

import android.app.Application;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Joesta on 2019/05/30.
 */
public class CustomApplication extends Application {
    private static final String TAG = CustomApplication.class.getSimpleName();
    public static CustomApplication instance = null;
    private FirebaseFirestore db;

    public static CustomApplication getInstance() {
        if (null == instance) {
            instance = new CustomApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: I was here > application context");
        super.onCreate();
        //db = FirebaseFirestore.getInstance();
    }

    public FirebaseFirestore getDbInstance(){
        return FirebaseFirestore.getInstance();
    }
}
