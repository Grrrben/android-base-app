package com.atog.grrrben.share;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.atog.grrrben.share.classes.User;
import com.atog.grrrben.share.helpers.SQLiteHandler;

import java.util.List;

public class ContactsActivity extends BaseActivity {

    private static final String TAG = "ContactsActivity";

    private SQLiteHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_stuff);
        super.onCreate(savedInstanceState);

        db = new SQLiteHandler(getApplicationContext());
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
}
