package com.atog.grrrben.share.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.atog.grrrben.share.classes.User;

import java.io.IOException;

import static java.lang.System.currentTimeMillis;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    /**
     * Number of seconds the cache is valid
     */
    private static int cacheduration = 3000;

    // Shared Preferences
    private SharedPreferences pref;

    private Editor editor;
    private Context _context;

    // Shared pref mode
    public static int PRIVATE_MODE = 0;

    // Shared preferences file username
    private static final String PREF_NAME = "AndrSessionLogin";

    private static final String KEY_DATE_LOGGED_IN = "date_logged_in";
    private static final String KEY_USER = "user";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(User user) {
        // first set a time
        editor.putLong(KEY_DATE_LOGGED_IN, currentTimeMillis());
        // and put the user in
        try {
            editor.putString(KEY_USER, ObjectSerializer.serialize(user));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    public User getUser() {
        User user = null;
        try {
            String userString = pref.getString(KEY_USER, "");
            user = (User) ObjectSerializer.deserialize(userString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    public void logout () {
        editor.putLong(KEY_DATE_LOGGED_IN, 0);
        editor.commit();
    }

    public boolean isLoggedIn(){

        long loggedInAt = pref.getLong(KEY_DATE_LOGGED_IN, 0);
        long nowInMs = currentTimeMillis();

        int loggedInSeconds = (int)(loggedInAt / 1000);
        int nowInSeconds = (int)(nowInMs / 1000);

        int loginValidTill =  loggedInSeconds + cacheduration;
        return (loginValidTill > nowInSeconds);
    }
}