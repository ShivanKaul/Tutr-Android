package com.example.android.tutr;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;


public class TutorListAdapter extends BaseAdapter {
    private final Context context;
    private Activity activity;
    private LayoutInflater inflater;
    private final List<UserToRating> usersToRatings;
    private boolean showCustomButton;
    customButtonListener customListner;

    public TutorListAdapter(Context context, List<UserToRating> usersToRatings, boolean showCustomButton) {
        this.context = context;
        this.usersToRatings = usersToRatings;
        this.showCustomButton = showCustomButton;
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

    public void removeAt(int position) {
         usersToRatings.remove(position);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }


    public interface customButtonListener {
        public void onButtonClickListner(int position, UserToRating value);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_item, parent, false);

        //Get the textviews
        TextView name = (TextView) rowView.findViewById(R.id.tutorName);
        TextView rating = (TextView) rowView.findViewById(R.id.rating);
        TextView rate = (TextView) rowView.findViewById(R.id.hourlyRate);

        //Get parse user + rating
        final UserToRating utr = getItem(position);
        final ParseObject userObj = utr.getUser();
        final ParseObject ratingObj = utr.getRating();

        //Set fields
        name.setText(userObj.getString("name"));
        rating.setText(context.getString(R.string.hourly_rate_text) + String.format("%.2f", userObj.getDouble("hourlyRate")));
        if (ratingObj.getDouble("rating") == 0)
            rate.setText(context.getString(R.string.rating_text) + "N/A");
        else
            rate.setText(context.getString(R.string.rating_text) + String.format("%.1f", ratingObj.getDouble("rating")) + " (" + ratingObj.getInt("ratingCount") + ")");


        //Add remove button if enabled
        if (showCustomButton){
            ImageButton removeFav = (ImageButton) rowView.findViewById(R.id.removeFavorite);
            removeFav.setVisibility(View.VISIBLE);

            removeFav.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (customListner != null) {
                        customListner.onButtonClickListner(position, utr);
                    }
                }
            });
        }



        return rowView;
    }
}
