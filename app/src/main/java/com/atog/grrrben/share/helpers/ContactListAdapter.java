package com.atog.grrrben.share.helpers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.atog.grrrben.share.R;
import com.atog.grrrben.share.classes.User;

import java.util.List;

/**
 * Created by Gerben on 21-Jun-15.
 * Dit is de view offer_search_result_item voor de lijst met daghappen
 * - title
 * - description
 * - price
 */
public class ContactListAdapter extends ArrayAdapter<User> {

    public static String TAG = "ContactListAdapter";
    
    public ContactListAdapter(Context context, List<User> contacts) {
        super(context, 0, contacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d(TAG, "getView: running");

        // Get the data item for this position
        User contact = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_snippet, parent, false);
        }

        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.contact_name);
        name.setText(contact.username);

        // Return the completed view to render on screen
        return convertView;
    }
}