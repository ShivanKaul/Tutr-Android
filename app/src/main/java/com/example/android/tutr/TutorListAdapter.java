package com.example.android.tutr;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class TutorListAdapter extends BaseAdapter {
    private final Context context;
    private Activity activity;
    private LayoutInflater inflater;
    private final List<UserToRating> usersToRatings;

    public TutorListAdapter(Context context, List<UserToRating> usersToRatings) {
        this.context = context;
        this.usersToRatings = usersToRatings;
    }

    @Override
    public int getCount() {
        return usersToRatings.size();
    }

    @Override
    public UserToRating getItem(int position) {
        return usersToRatings.get(position);
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

        //Get parse user + rating
        UserToRating userToRating = getItem(position);
        ParseObject userObj = userToRating.getUser();
        ParseObject ratingObj = userToRating.getRating();

        //Set fields
        name.setText(userObj.getString("name"));
        rating.setText(context.getString(R.string.hourly_rate_text) + String.format("%.2f", userObj.getDouble("hourlyRate") ));
        if (ratingObj.getDouble("rating") == 0)
            rate.setText(context.getString(R.string.rating_text) + "N/A");
        else
            rate.setText(context.getString(R.string.rating_text) + String.format("%.1f", ratingObj.getDouble("rating")) + " (" + ratingObj.getInt("ratingCount") + ")");


        return rowView;
    }
}
