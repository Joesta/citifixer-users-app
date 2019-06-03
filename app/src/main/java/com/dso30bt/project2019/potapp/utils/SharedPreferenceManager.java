package com.dso30bt.project2019.potapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Joesta on 2019/05/30.
 */
public class SharedPreferenceManager {
    /***
     * get auth_token of active user
     * @param ctx provides access to application resources
     * @return auth_token
     */
    public static String getAuthToken(Context ctx) {
         SharedPreferences sp =
                 ctx.getSharedPreferences(SharedPreferenceHelper.LOGIN_INFO, Context.MODE_PRIVATE);
        return sp.getString(SharedPreferenceHelper.AUTH_TOKEN, "");
    }

    private static class SharedPreferenceHelper {
        private static final String  LOGIN_INFO = "login_info";
        private static final String AUTH_TOKEN = "auth_token";
    }
}
