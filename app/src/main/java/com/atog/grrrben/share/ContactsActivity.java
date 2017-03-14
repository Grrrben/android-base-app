package com.atog.grrrben.share;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.atog.grrrben.share.classes.User;
import com.atog.grrrben.share.helpers.ContactListAdapter;
import com.atog.grrrben.share.helpers.JsonRequestQueue;
import com.atog.grrrben.share.helpers.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ContactsActivity extends BaseActivity {

    private static final String TAG = "ContactsActivity";

    private SQLiteHandler db;

    private ContactsTask mContactsTask = null;

    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_contacts);
        super.onCreate(savedInstanceState);

        mProgressView = findViewById(R.id.progress_bar);

        boolean update = getContacts();
        if (!update) {
            contactsToListView();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void contactsToListView(){
        List<User> contacts = getDatabase().getContacts();
        ContactListAdapter contactListAdapter = new ContactListAdapter(ContactsActivity.this, contacts);
        // Attach the adapter to a ListView
        ListView contactListView = (ListView) findViewById(R.id.contact_list);
        contactListView.setAdapter(contactListAdapter);
    }

    /**
     * Async fetching of contacts
     * @return boolean, true if an update will be done.
     */
    private boolean getContacts() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int syncConnPref = Integer.parseInt(sharedPref.getString("sync_frequency", "-1"));

        if (syncConnPref != -1) {
            int syncSeconds = syncConnPref * 60;
            long unixtimeNow = System.currentTimeMillis() / 1000L;
            long unixTimeUpdateNeeded = (unixtimeNow - syncSeconds);
            long lastUpdate = session.getLastUpdateContacts();

            if (lastUpdate < unixTimeUpdateNeeded) {
                Log.d(TAG, "Update because " + Long.toString(unixtimeNow) + " is smaller than " + Long.toString(unixTimeUpdateNeeded));
                mContactsTask = new ContactsTask(this);
                mContactsTask.execute((Void) null);
                session.setLastUpdateContacts(unixtimeNow);
                return true;
            }
            Log.d(TAG, "No update of contacts. ( " + Long.toString(unixtimeNow) + " > " + Long.toString(unixTimeUpdateNeeded) + ")");
        }
        return false;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private SQLiteHandler getDatabase(){
        if (null == db) {
            db = new SQLiteHandler(getApplicationContext());
        }
        return db;
    }

    public class ContactsTask extends AsyncTask<Void, Void, Boolean> {

        private ContactsActivity mContext;
        private final String TAG = "ContactsTask";

        ContactsTask(ContactsActivity context) {
            mContext = context;
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    AppConfig.URL_CONTACTS,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "Response: " + response.toString());

                            try {
                                Boolean success = response.getBoolean("success");

                                if (success) {
                                    Log.d(TAG, "getting contacts - success");
                                    JSONArray contacts = response.getJSONArray("contacts");
                                    String group = response.getString("group");
                                    db.syncContacts(contacts, group);
                                } else {
                                    Log.d(TAG, "getting contacts - nope");
//                                    Snackbar.make(coordinatorLayout, "NOPE", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                }

                            } catch (JSONException e) {
                                Log.d(TAG, "Error: " + e.toString());
//                                Snackbar.make(coordinatorLayout, "NOPE...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    Log.d(TAG, error.toString());
                }
            });
            JsonRequestQueue.getInstance(mContext).addToRequestQueue(jsObjRequest);
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mContactsTask = null;
            contactsToListView();
            showProgress(false);
            Log.d(TAG, "success: " + success.toString());
        }

        @Override
        protected void onCancelled() {
            mContactsTask = null;
            showProgress(false);
        }
    }
}
