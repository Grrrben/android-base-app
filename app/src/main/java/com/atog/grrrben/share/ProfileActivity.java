package com.atog.grrrben.share;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.atog.grrrben.share.helpers.SQLiteHandler;
import java.util.HashMap;

public class ProfileActivity extends BaseActivity {

    private TextView txtName;
    private TextView txtEmail;

    private SQLiteHandler db;

    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_profile);
        super.onCreate(savedInstanceState);

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

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();

        String email = user.get("email");

        // Displaying the user details on the screen
        txtName.setText(user.get("username"));
        Log.d(TAG, txtName.toString());
        txtEmail.setText(email);
    }

}
