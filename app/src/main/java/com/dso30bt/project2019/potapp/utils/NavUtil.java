package com.dso30bt.project2019.potapp.utils;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Joesta on 2019/05/30.
 */
public class NavUtil {
    public static void moveToNextActivity(Context packageContext, Class<? extends AppCompatActivity> destinationPackageContext) {
        packageContext.startActivity(new Intent(packageContext, destinationPackageContext));
    }
}

