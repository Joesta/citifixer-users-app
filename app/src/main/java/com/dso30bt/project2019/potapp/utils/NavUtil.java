package com.dso30bt.project2019.potapp.utils;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Joesta on 2019/05/30.
 */
public class NavUtil {
    public static void moveToNextActivity(Context packageContext, Class<? extends AppCompatActivity> destinationPackageContext, String... intentExtras) {
        if (intentExtras.length > 0) {
            Intent intentWithExtras = new Intent(packageContext, destinationPackageContext);
            intentWithExtras.putExtra(Constants.EXTRA_EMAIL, intentExtras[0]);
            intentWithExtras.putExtra(Constants.EXTRA_FULLNAME, intentExtras[1]);
            packageContext.startActivity(intentWithExtras);
        } else {
            packageContext.startActivity(new Intent(packageContext, destinationPackageContext));
        }
    }
}

