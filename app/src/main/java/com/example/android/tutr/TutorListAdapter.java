package com.example.android.tutr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class TutorListAdapter extends ArrayAdapter<String>  {
    private final Context context;
    private final String[] values;

    public TutorListAdapter(Context context, String[] values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
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
        if (user.getDouble("rating") == 0)
            rate.setText(context.getString(R.string.rating_text) + "N/A");
        else
            rate.setText(context.getString(R.string.rating_text) + String.format("%.1f", user.getDouble("rating")));

        return rowView;
    }
}
