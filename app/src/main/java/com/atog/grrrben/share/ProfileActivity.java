package com.atog.grrrben.share;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.atog.grrrben.share.classes.User;
import com.atog.grrrben.share.helpers.SQLiteHandler;
import com.atog.grrrben.share.helpers.SessionManager;

public class ProfileActivity extends BaseActivity {

    private TextView txtName;
    private TextView txtEmail;

    private SQLiteHandler db;
    private SessionManager session;

    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_profile);
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        txtName = (TextView) findViewById(R.id.username);
        txtEmail = (TextView) findViewById(R.id.email);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        session = new SessionManager(this);
        User user = session.getUser();
        // Displaying the user details on the screen
        txtName.setText(user.username);
        txtEmail.setText(user.email);
        Log.d(TAG, txtName.toString());
    }

}
