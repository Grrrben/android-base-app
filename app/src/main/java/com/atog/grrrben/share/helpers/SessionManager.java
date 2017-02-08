package com.atog.grrrben.share.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import static java.lang.System.currentTimeMillis;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    /**
     * Number of seconds the cache is valid
     */
    private static int cacheduration = 300;

    // Shared Preferences
    private SharedPreferences pref;

    private Editor editor;
    private Context _context;

    // Shared pref mode
    public static int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "AndrSessionLogin";

    private static final String KEY_DATE_LOGGED_IN = "date_logged_in";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {
        if (isLoggedIn) {
            editor.putLong(KEY_DATE_LOGGED_IN, currentTimeMillis());
        } else {
            editor.putLong(KEY_DATE_LOGGED_IN, 0);
        }
        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
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