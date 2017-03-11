package com.atog.grrrben.share.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.atog.grrrben.share.classes.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 7;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table user
    private static final String TABLE_CONTACTS = "contacts";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_GROUP = "groupname";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UUID = "uuid";
    private static final String KEY_CREATED_AT = "created_at";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
         * SQLiteOpenHelper onCreate() and onUpgrade() callbacks are invoked when the database is
         * actually opened, for example by a call to getWritableDatabase().
         * The database is not opened when the database helper object itself is created.
         * SQLiteOpenHelper versions the database files. The version number is the int argument
         * passed to the constructor. (DATABASE_VERSION)
         * In the database file, the version number is stored in PRAGMA user_version.
         */

        String sqlTableContacts = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_USERNAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_UUID + " TEXT,"
                + KEY_GROUP + " TEXT"
                + ")";
        db.execSQL(sqlTableContacts);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        // Create tables again
        onCreate(db);
    }

    public List<User> getContacts() {
        String selectQuery = "SELECT * FROM " + TABLE_CONTACTS;
        List<User> contacts = new ArrayList<User>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    User contact = getContact(cursor);
                    contacts.add(contact);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();

        return contacts;
    }

    /**
     * Get a User object
     */
    private User getContact(Cursor cursor) {
        User user = new User();
        user.username = cursor.getString(1);
        user.email = cursor.getString(2);
        user.uuid = cursor.getString(3);
        user.group = cursor.getString(4);
        return user;
    }

    /**
     * Put the contacts in the database. Uses a User object to represent a contact.
     */
    public void syncContacts(JSONArray contacts, String group) {
        deleteAllcontacts();
        SQLiteDatabase db = this.getWritableDatabase();

        for(int n = 0; n < contacts.length(); n++) {
            ContentValues values = new ContentValues();

            try {
                JSONObject contact = contacts.getJSONObject(n);
                values.put(KEY_USERNAME, contact.getString("username"));
                values.put(KEY_EMAIL, contact.getString("email"));
                values.put(KEY_UUID, contact.getString("uuid"));
                values.put(KEY_GROUP, group);
                long id = db.insert(TABLE_CONTACTS, null, values);

                Log.d(TAG, "syncContacts: synced: " + contact.getString("username"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        db.close();
    }

    private void deleteAllcontacts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, null, null);
        db.close();
    }
}
