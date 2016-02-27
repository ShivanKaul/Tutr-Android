package com.example.android.tutr;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

import static java.lang.Float.parseFloat;


public class TutorListAdapter extends BaseAdapter {
    private final Context context;
    private Activity activity;
    private LayoutInflater inflater;
    private final List<ParseObject> users;

    public TutorListAdapter(Context context, List<ParseObject> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.search_result_item, parent, false);

        //Get the textviews
        TextView name = (TextView) rowView.findViewById(R.id.tutorName);
        TextView rating = (TextView) rowView.findViewById(R.id.rating);
        TextView rate = (TextView) rowView.findViewById(R.id.hourlyRate);

        //Get parse user
        ParseObject user = users.get(position);

        //Set fields
        name.setText(user.getString("name"));
        rating.setText(context.getString(R.string.hourly_rate_text) + String.format("%.2f", user.getDouble("hourlyRate") ));
        rate.setText(context.getString(R.string.rating_text) + String.format("%.1f", user.getDouble("rating")));


        return rowView;
    }
}
