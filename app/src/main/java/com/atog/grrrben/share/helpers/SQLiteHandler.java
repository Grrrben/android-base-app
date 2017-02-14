package com.atog.grrrben.share.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.atog.grrrben.share.classes.User;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table user
    private static final String TABLE_USER = "user"; // depricated
    private static final String TABLE_CONTACTS = "contacts";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_GROUP = "group";
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

        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USERNAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UUID + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        String sqlTableContacts = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_USERNAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_UUID + " TEXT"
                + KEY_GROUP + " TEXT,"
                + ")";
        db.execSQL(sqlTableContacts);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }

    /**
     * Put the contacts in the database. Uses a User object to represent a contact.
     */
    public void syncContacts(User[] contacts) {
        deleteAllcontacts();
        SQLiteDatabase db = this.getWritableDatabase();
        for (User contact : contacts) {
            ContentValues values = new ContentValues();
            values.put(KEY_USERNAME, contact.username);
            values.put(KEY_EMAIL, contact.email);
            values.put(KEY_UUID, contact.uuid);
            long id = db.insert(TABLE_USER, null, values);
        }
        db.close();
    }

    private void deleteAllcontacts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, null, null);
        db.close();
    }
}
