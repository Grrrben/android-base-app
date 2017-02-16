package com.atog.grrrben.share.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.atog.grrrben.share.classes.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

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

    private Gson gson;

    // Shared pref mode
    private static int PRIVATE_MODE = 0;

    // Shared preferences file username
    private static final String PREF_NAME = "AndrSessionLogin";
    private static final String KEY_DATE_LOGGED_IN = "date_logged_in";
    private static final String KEY_USER = "user";
    private static final String KEY_UUID = "unique_id";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        gson = new Gson();
    }

    public void setLogin(JSONObject user) {
        // first set a time
        editor.putLong(KEY_DATE_LOGGED_IN, currentTimeMillis());
        // and put the user in
        editor.putString(KEY_USER, user.toString());
        Log.d(TAG, user.toString());
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    public User getUser() {
        if (!isLoggedIn()) {
            Log.d(TAG, "Not logged in");
            return null;
        }
        String userString = pref.getString(KEY_USER, "");
        if (userString.equals("")) {
            Log.d(TAG, "userString empty");
            return null;
        }

        try {
            return gson.fromJson(userString, User.class);
        } catch (JsonSyntaxException e) {
            Log.d(TAG, "JsonSyntaxException");
            Log.d(TAG, e.getMessage());
            return null;
        }
    }

    public String getUuid() {
        String uuid = pref.getString(KEY_UUID, "");
        if (uuid.equals("")) {
            Log.d(TAG, "uuid empty");
            uuid = UUID.randomUUID().toString();
            editor.putString(KEY_UUID, uuid);
            editor.commit();
        }
        return uuid;
    }

    public void logout () {
        editor.putLong(KEY_DATE_LOGGED_IN, 0);
        editor.putString(KEY_USER, "");
        editor.commit();
    }

    public boolean isLoggedIn(){
        // logged in time depends on cacheduration
        long loggedInAt = pref.getLong(KEY_DATE_LOGGED_IN, 0);
        long nowInMs = currentTimeMillis();
        int loggedInSeconds = (int)(loggedInAt / 1000);
        int nowInSeconds = (int)(nowInMs / 1000);
        int loginValidTill =  loggedInSeconds + cacheduration;

        String userString = pref.getString(KEY_USER, "");
        return (!userString.equals("") && loginValidTill > nowInSeconds);
    }
}