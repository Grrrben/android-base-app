package com.atog.grrrben.share;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.atog.grrrben.share.classes.Message;
import com.atog.grrrben.share.helpers.JsonRequestQueue;
import com.atog.grrrben.share.helpers.SessionManager;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.String.format;

/**
 * Holds shared methods for the activity classes
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggle;

    protected SessionManager session;

    protected Timer timer;
    protected boolean repeatedUpdates = true;

    private AsyncServerCall asyncServerCall = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);
        timer = new Timer();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(
                this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                Log.d("ActionBarDrawerToggle", "onDrawerClosed");
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                Log.d("ActionBarDrawerToggle", "onDrawerOpened");
                super.onDrawerOpened(drawerView);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.left_drawer);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);

                switch (menuItem.getItemId()) {
                    case R.id.menu_btn_home:
                        gotToActivity(new HomeActivity());
                        return true;
                    case R.id.menu_btn_profile:
                        gotToActivity(new ProfileActivity());
                        return true;
                    case R.id.menu_btn_stuff:
                        gotToActivity(new ContactsActivity());
                        return true;
                    case R.id.menu_btn_logout:
                        logout();
                        return true;
                    default:
//                        return super.onOptionsItemSelected(item);
                }
                return true;
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onstop");
        repeatedUpdates = true;
        timedTask(this, AppConfig.INTERVAL_UPDATE_INACTIVE);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onresume");
        if (asyncServerCall != null) {
            asyncServerCall.cancel(true);
        }
        repeatedUpdates = false;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

    public void gotToActivity(AppCompatActivity activity) {
        Intent intent = new Intent(this, activity.getClass());
        startActivity(intent);
        finish();
    }

    public void logout() {
        session.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    protected void timedTask(BaseActivity context, int ms) {

        final int millisec = ms;
        final BaseActivity fContext = context;

        timer.schedule(new TimerTask() {
            public void run() {
                Log.d("TIMER", "timer");
                asyncServerCall = new AsyncServerCall(fContext);
                asyncServerCall.execute((Void) null);
                if (repeatedUpdates) {
                    timedTask(fContext, millisec);
                }
            }
        }, ms);
    }

    /**
     * Checks the server for updates
     * Calls `AppConfig.URL_MESSAGE`
     */
    public class AsyncServerCall extends AsyncTask<Void, Void, Boolean> {

        private BaseActivity mContext;
        private final String TAG = "AsyncServerCall";

        AsyncServerCall(BaseActivity context) {
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            JsonObjectRequest checkUpdateRequest = new JsonObjectRequest(
                Request.Method.GET,
                AppConfig.URL_MESSAGE,
                null,
                new Response.Listener<JSONObject>() {

                    private int notificationId;

                    private String notificationTitle;
                    private String notificationBody;

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString());
                        try {
                            Boolean success = response.getBoolean("success");
                            if (success) {

                                Gson gson = new Gson();
                                Log.d(TAG, response.getString("messages"));
                                Message[] messages = gson.fromJson(response.getString("messages"), Message[].class);

                                if (messages.length == 1) {
                                    notificationTitle = messages[0].title;
                                    notificationBody = messages[0].body;
                                } else {
                                    notificationTitle = format("%d new messages", messages.length);
                                    notificationBody = "Click to open";
                                }

                                NotificationCompat.Builder mBuilder =
                                        new NotificationCompat.Builder(mContext)
                                                .setSmallIcon(R.drawable.ic_action_add)
                                                .setContentTitle(notificationTitle)
                                                .setContentText(notificationBody)
                                                .setLights(Color.BLUE, 1, 1)
                                                .setAutoCancel(true);

                                // Creates an explicit intent for an Activity in your app
                                Intent resultIntent = new Intent(mContext, HomeActivity.class);

                                // The stack builder object will contain an artificial back stack for the
                                // started Activity.
                                // This ensures that navigating backward from the Activity leads out of
                                // your application to the Home screen.
                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);

                                // Adds the back stack for the Intent (but not the Intent itself)
                                stackBuilder.addParentStack(HomeActivity.class);

                                // Adds the Intent that starts the Activity to the top of the stack
                                stackBuilder.addNextIntent(resultIntent);
                                PendingIntent resultPendingIntent =
                                        stackBuilder.getPendingIntent(
                                                0,
                                                PendingIntent.FLAG_UPDATE_CURRENT
                                        );
                                mBuilder.setContentIntent(resultPendingIntent);
                                NotificationManager mNotificationManager =
                                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                notificationId = 657532; // same, same, to ensure a single notification
                                mNotificationManager.notify(notificationId, mBuilder.build());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d(TAG, error.toString());
                    }
                });

            JsonRequestQueue.getInstance(mContext).addToRequestQueue(checkUpdateRequest);
            Log.d(TAG, "returning false");
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            asyncServerCall = null;
            Log.d(TAG, "success: " + success.toString());
        }

        @Override
        protected void onCancelled() {
            asyncServerCall = null;
        }
    }
}
