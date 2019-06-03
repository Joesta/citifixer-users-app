package com.dso30bt.project2019.potapp.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Joesta on 2019/05/30.
 */
public class ErrorHandler {

    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }
}
