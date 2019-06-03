package com.dso30bt.project2019.potapp.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Joesta on 2019/05/30.
 */
public class NavUtil {
    public static void moveToNextActivity(Context packageContext, Class<?> destinationPackageContext) {
        packageContext.startActivity(new Intent(packageContext, destinationPackageContext));
    }
}

