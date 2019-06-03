package com.dso30bt.project2019.potapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Joesta on 2019/05/29.
 */
public final class InternetConnectionHelper {

    //check for internet connection
    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager conMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMgr.getActiveNetworkInfo().isConnected();
    }
}
