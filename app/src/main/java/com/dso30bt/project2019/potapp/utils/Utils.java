package com.dso30bt.project2019.potapp.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Joesta on 2019/06/15.
 */
public class Utils {
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
