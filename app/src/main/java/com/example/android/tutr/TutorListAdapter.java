package com.example.android.tutr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;



/**
 * Created by Jado on 21/02/2016.
 */
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

        //Set values
        name.setText(values[position]);
        rating.setText(context.getString(R.string.hourly_rate_text) + "$10.50");
        rate.setText(context.getString(R.string.rating_text) + "3.7");

        return rowView;
    }
}
