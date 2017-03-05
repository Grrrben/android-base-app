package com.atog.grrrben.share;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.atog.grrrben.share.classes.User;
import com.atog.grrrben.share.helpers.JsonRequestQueue;
import com.atog.grrrben.share.helpers.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import static com.atog.grrrben.share.R.id.coordinatorLayout;

public class ContactsActivity extends BaseActivity {

    private static final String TAG = "ContactsActivity";

    private SQLiteHandler db;

    private ContactsTask mContactsTask = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_stuff);
        super.onCreate(savedInstanceState);

        db = new SQLiteHandler(getApplicationContext());
        // create the table if not exists
        List<User> contacts = db.getContacts();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void getContacts(){
        mContactsTask = new ContactsTask(this);
        mContactsTask.execute((Void) null);
    }

    public class ContactsTask extends AsyncTask<Void, Void, Boolean> {

        private ContactsActivity mContext;
        private final String TAG = "UserLoginTask";

        private JSONObject user;

        ContactsTask(ContactsActivity context) {
            mContext = context;
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
                                JSONObject contacts = response.getJSONObject("contacts");

                                if (success) {
                                    Log.d(TAG, "success");
                                } else {
                                    Log.d(TAG, "not success");
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
//            showProgress(false);
            Log.d(TAG, "success: " + success.toString());
        }

        @Override
        protected void onCancelled() {
            mContactsTask = null;
//            showProgress(false);
        }
    }
}
