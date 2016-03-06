package com.example.android.tutr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

/** View Tutor class. Handles the view of the tutor's profile - what is diplayed when
 * the user clicks on a Tutor while searching.
 */

public class ViewTutor extends AppCompatActivity {

    private TextView current_rating;
    private TextView ratingCounter;
    private TextView description;
    private TextView courses;

    private TextView rate;
    private TextView email;
    private TextView phone;

    private RatingBar rating_bar;

    private double old_rating;
    private double rating_counter;

    private ParseObject userRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tutor);
        // get the intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        setUpUIElements();


        addListenerOnRatingBar(username);

        getDataForTutor(username);
    }


    /** Adds listener on the Rating bar, so that when a rating is done the ratings are persisted.
     * The user cannot rate twice.
     * Credits: http://www.mkyong.com/android/android-rating-bar-example/
     *
     * @param username  Username for Ratings table
     */
    public void addListenerOnRatingBar(final String username) {

        rating_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                // Make read-only
                rating_bar.setIsIndicator(true);
                // Calculate new rating + update rating counter
                int newCounter = (int)(rating_counter) + 1;
                double average = (rating_counter == 0) ?
                        rating : ((old_rating * rating_counter) + rating) / (newCounter);

                userRating.put("rating", average);
                userRating.add("ratedBy", ParseUser.getCurrentUser().getUsername());
                userRating.put("username", username);
                userRating.put("ratingCount", newCounter);
                // Fire off Parse event in background thread
                userRating.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(ViewTutor.this, "Rated, thank you!", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(ViewTutor.this, "Problem storing rating" + e, Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        });
    }

    /** Set up UI elements
     */
    private void setUpUIElements() {
        // need to assign default value to fields in case
        // no ratings
        current_rating = (TextView) findViewById(R.id.display_rating);
        ratingCounter = (TextView) findViewById(R.id.display_counter);
        description = (TextView) findViewById(R.id.display_description);
        courses = (TextView) findViewById(R.id.display_subjects);

        rate = (TextView) findViewById(R.id.display_rate);
        email = (TextView) findViewById(R.id.display_email);
        phone = (TextView) findViewById(R.id.display_phone);

        rating_bar = (RatingBar) findViewById(R.id.ratingBar);
    }

    /** Get the data to display for a tutor
     * @param username  Username for Ratings table
     */
    private void getDataForTutor(final String username) {
        // get courses, description, current rating, rating counter,
        // hourly rate, phone number
        // allow users to rate a tutor
        // rating transmits data to parse in background thread

        email.setText(username);
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    ParseUser user = null;
                    // This should not fail, because we're querying for something already found
                    // in main activity
                    try {
                        user = users.get(0);
                    } catch (Exception exp) {
                        Toast.makeText(ViewTutor.this, "Could not fetch item from Ratings, which" +
                                "is really weird because we were able to just a second back" + exp, Toast.LENGTH_LONG).show();
                        // What is even happening, just die
                        finish();
                    }

                    // Get rating from rating table
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Ratings");
                    query.whereEqualTo("username", username);
                    try {
                        userRating = query.find().get(0);
                        old_rating = userRating.getDouble("rating");
                        rating_counter = userRating.getDouble("ratingCount");
                    } catch (ParseException p) {
                        Toast.makeText(ViewTutor.this, "Problem fetching data from Ratings table" + p, Toast.LENGTH_LONG).show();
                    }

                    // Check if user has already rated tutor and if yes set ratings bar to be non editable
                    try {
                        if (userRating.getList("ratedBy").contains(ParseUser.getCurrentUser().getUsername())) {
                            rating_bar.setIsIndicator(true);
                        }
                    } catch (NullPointerException npe) {}

                    if (user.getList("courses") != null) {
                        List<String> subjects = user.getList("courses");
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String course : subjects) {
                            stringBuilder.append(", ").append(course);
                        }
                        //remove the starting ','
                        stringBuilder.deleteCharAt(0);
                        stringBuilder.deleteCharAt(0);
                        courses.setText(stringBuilder.toString());
                    } else courses.setText("");
                    if (user.getString("phone") != null) {
                        phone.setText(user.getString("phone"));
                    } else phone.setText("");
                    if (user.getDouble("hourlyRate") != 0) {
                        rate.setText(String.valueOf(user.getDouble("hourlyRate")));
                    } else rate.setText("");
                    if (user.getString("description") != null) {
                        description.setText(user.getString("description"));
                    } else description.setText("");
                    if (old_rating > 0) {
                        current_rating.setText(new DecimalFormat("##.#").format(old_rating));
                    } else current_rating.setText("Unrated");
                    if (rating_counter > 0) {
                        ratingCounter.setText("(" + String.valueOf((int)rating_counter) + ")");
                    } else ratingCounter.setText("");
                } else {
                    Toast.makeText(ViewTutor.this, "Problem fetching user from database" + e, Toast.LENGTH_LONG).show();
                }
            }
        });

    }


}
