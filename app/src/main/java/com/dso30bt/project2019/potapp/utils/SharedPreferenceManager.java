package com.dso30bt.project2019.potapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Joesta on 2019/05/30.
 */
public class SharedPreferenceManager {
    /***
     * get user auth_token
     * @param ctx provides access to application resources
     * @return auth_token
     */
    public static String getAuthToken(Context ctx) {
         SharedPreferences sp =
                 ctx.getSharedPreferences(SharedPreferenceHelper.LOGIN_INFO, Context.MODE_PRIVATE);
        return sp.getString(SharedPreferenceHelper.AUTH_TOKEN, "");
    }

    public static String getEmail(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(SharedPreferenceHelper.LOGIN_INFO, Context.MODE_PRIVATE);
        return sp.getString(SharedPreferenceHelper.USER_EMAIL, null);
    }

    public static void saveEmail(Context ctx, String userEmail, int flag) {
        SharedPreferences sp =
                ctx.getSharedPreferences(SharedPreferenceHelper.LOGIN_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SharedPreferenceHelper.USER_EMAIL, userEmail);
        editor.putInt(SharedPreferenceHelper.FLAG, flag);
        editor.apply();
    }


    public static int getFlag(Context ctx) {
        SharedPreferences sp
               =  ctx.getSharedPreferences(SharedPreferenceHelper.LOGIN_INFO, Context.MODE_PRIVATE);
        return sp.getInt(SharedPreferenceHelper.FLAG, 0);

    }
    public static String getName(Context ctx) {
        SharedPreferences sp
                = ctx.getSharedPreferences(SharedPreferenceHelper.LOGIN_INFO, Context.MODE_PRIVATE);
        return sp.getString(SharedPreferenceHelper.USERNAME, null);
    }

    public static void clearSavedLoginInfo(Context ctx) {
        SharedPreferences sp =
                ctx.getSharedPreferences(SharedPreferenceHelper.LOGIN_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    private static class SharedPreferenceHelper {
        public static final String STATUS = "status_info";
        private static final String FLAG = "userFlag";
        private static final String  LOGIN_INFO = "login_info";
        private static final String AUTH_TOKEN = "auth_token";
        private static final String USER_EMAIL = "emailAddress";
        private static final String USERNAME = "username";

        private static final String STATUS_TYPE = "status_type";
    }
}
