package com.iglweb.tvbd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

/**
 * Created by MMR on 1/4/2018.
 */

public class UserSessionManager {

    SharedPreferences pref;
    Editor editor;
    Context mContext;
    int PRIVATE_MODE = 0;

    private static final String PREFER_NAME = "TVBD_SP";
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    public static final String PHONE_ID = "phoneID";

    public UserSessionManager(Context context) {
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createUserLoginSession(String phoneID) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(PHONE_ID, phoneID);
        editor.commit();
    }

    public boolean checkLogin() {
        // Check login status
        if (!this.isUserLoggedIn()) {

            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(mContext, ChanelActivity.class);

            // Closing all the Activities from stack
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            mContext.startActivity(i);

            return true;
        }
        return false;
    }

    public HashMap<String, String> getUserPhoneID() {
        HashMap<String, String> user = new HashMap<>();
        user.put(PHONE_ID, pref.getString(PHONE_ID, ""));
        return user;
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }


}
