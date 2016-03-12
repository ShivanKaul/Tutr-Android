package com.example.android.tutr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RefreshCallback;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private String originalReview;

    private RatingBar rating_bar;

    private EditText enter_new_review_field;
    private Button save_new_review_button;

    private double old_rating;
    private double rating_counter;

    private ParseObject userRating;
    private ParseObject userReviews;
    private boolean notRatedYet = true;

    private Button callButton;
    private Button msgButton;

    private  ImageView tutor_pic;

    private String tutorUsername;
    private CheckBox favoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tutor);

        // get the intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String name = intent.getStringExtra("name");
        tutorUsername = intent.getStringExtra("username");

        setUpUIElements(name, username);

        addListenerOnRatingBar(username);

        getDataForTutor(username);

        initializeFavoritesButton(username);

        setUpReviews(username, name);


        callButton = (Button) this.findViewById(R.id.call);
        msgButton = (Button) this.findViewById(R.id.msg);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phone.getText()));
                    startActivity(callIntent);
                }
            }
        });


        msgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.setData(Uri.parse("sms:" + phone.getText()));
                    startActivity(smsIntent);
                }
            }
        });
    }
    private void loadProfilePicFromParse(ParseUser currentUser) {
        ParseFile postImage = currentUser.getParseFile("profilePicture");
        if (postImage == null) {
            tutor_pic.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(0, 0);
            tutor_pic.setLayoutParams(layoutParams);
            return;
        }
        String imageUrl = postImage.getUrl();//live url
        Uri imageUri = Uri.parse(imageUrl);
        Picasso.with(this).load(imageUri.toString()).fit().into(tutor_pic);
    }

    /**
     * Adds listener on the Rating bar, so that when a rating is done the ratings are persisted.
     * The user cannot rate twice.
     * Credits: http://www.mkyong.com/android/android-rating-bar-example/
     *
     * @param username Username for Ratings table
     */
    public void addListenerOnRatingBar(final String username) {

        rating_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                if (notRatedYet) {
                    // Make read-only
                    rating_bar.setIsIndicator(true);
                    // Calculate new rating + update rating counter
                    int newCounter = (int) (rating_counter) + 1;
                    double average = averageRating(newCounter, rating, old_rating, rating_counter);

                    userRating.put("rating", average);
                    userRating.add("ratedBy", ParseUser.getCurrentUser().getUsername() + "," + rating);
                    userRating.put("username", username);
                    userRating.put("ratingCount", newCounter);
                    // Fire off Parse event in background thread
                    userRating.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(ViewTutor.this, "Rated, thank you!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ViewTutor.this, "Problem storing rating" + e, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }



    /**
     * Set up UI elements
     */
    private void setUpUIElements(final String name, final String username) {
        // need to assign default value to fields in case
        // no ratings
        setTitle(name);

        tutor_pic = (ImageView) findViewById(R.id.tutor_pic);
        current_rating = (TextView) findViewById(R.id.display_rating);
        ratingCounter = (TextView) findViewById(R.id.display_counter);
        description = (TextView) findViewById(R.id.display_description);
        courses = (TextView) findViewById(R.id.display_subjects);

        rate = (TextView) findViewById(R.id.display_rate);
        email = (TextView) findViewById(R.id.display_email);
        phone = (TextView) findViewById(R.id.display_phone);

        favoriteButton = (CheckBox) findViewById(R.id.favoriteButton);

        rating_bar = (RatingBar) findViewById(R.id.ratingBar);

        enter_new_review_field = (EditText) findViewById(R.id.add_new_review);
        save_new_review_button = (Button) findViewById(R.id.save_new_review_button);
        save_new_review_button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        try {
                            saveNewReview(username, name);
                        } catch (Exception e) {
                        }
                    }
                });
    }

    /**
     * lists all available reviews for a tutor
     */
    private void setUpReviews(final String username, final String name) {
        // UI elements
        LinearLayout ReviewsLinearLayout = (LinearLayout) findViewById(R.id.reviews);

        EditText reviewText;
        Button editReviewButton = null, saveReviewButton = null, deleteReviewButton = null;
        LinearLayout ButtonsLinearLayout = null;

        Map<String, String> usernameReview = new HashMap<String, String>();

        // Get rating from rating table
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Reviews");
        query.whereEqualTo("username", username);

        try {
            userReviews = query.find().get(0);
        } catch (ParseException p) {
            Toast.makeText(ViewTutor.this, "Problem fetching data from Reviews table" + p, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // Handle the case where there are no reviews
            return;
        }

        List reviews = userReviews.getList("reviews");

        // Collect reviews from Parse Object
        for (Object usernameAndReviewFromParse : reviews) {
            if (usernameAndReviewFromParse.toString().split(":::").length == 2) {
                String user = usernameAndReviewFromParse.toString().split(":::")[0];
                String review = usernameAndReviewFromParse.toString().split(":::")[1];
                if (user.equals(ParseUser.getCurrentUser().getEmail())) {
                    originalReview = usernameAndReviewFromParse.toString();
                }
                usernameReview.put(user, review);
            }
        }

        int uniqueCounter = 0;
        int reviewCounter = 1;

        // Display all reviews
        final int id_padding = 100000; // need to have a unique id
        for (Map.Entry<String, String> cursor : usernameReview.entrySet()) {
            // Display review
            reviewText = new EditText(this);
            reviewText.setText(reviewCounter + ". " + cursor.getValue());
            reviewText.setId(id_padding + uniqueCounter);
            reviewText.setFocusableInTouchMode(false);
            reviewText.setFocusable(false);
            reviewText.setClickable(false);

            ReviewsLinearLayout.addView(reviewText);

            // check if logged in user owns review
            boolean user_owns_review = cursor.getKey().equals(ParseUser.getCurrentUser().getUsername());
            if (user_owns_review) {
                // let them edit
                ButtonsLinearLayout = new LinearLayout(this);
                ButtonsLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

                editReviewButton = new Button(this);
                editReviewButton.setId(id_padding + uniqueCounter + 1); // need to have a unique id
                editReviewButton.setText("Edit");
                editReviewButton.setOnClickListener(
                        new View.OnClickListener() {
                            public void onClick(View view) {
                                try {
                                    editReview(view.getId());
                                } catch (Exception e) {
                                }
                            }
                        });

                saveReviewButton = new Button(this);
                saveReviewButton.setId(id_padding + uniqueCounter + 2);
                saveReviewButton.setText("Save");
                saveReviewButton.setEnabled(false);

                // save should be not clickable
                saveReviewButton.setOnClickListener(
                        new View.OnClickListener() {
                            public void onClick(View view) {
                                try {
                                    saveReview(view.getId(), username, name);
                                } catch (Exception e) {
                                }
                            }
                        });

                deleteReviewButton = new Button(this);
                deleteReviewButton.setId(id_padding + uniqueCounter + 3);
                deleteReviewButton.setText("Delete");
                deleteReviewButton.setOnClickListener(
                        new View.OnClickListener() {
                            public void onClick(View view) {
                                try {
                                    deleteReview(view.getId(), username, name);
                                } catch (Exception e) {
                                }
                            }
                        });

                ButtonsLinearLayout.addView(editReviewButton);
                ButtonsLinearLayout.addView(saveReviewButton);
                ButtonsLinearLayout.addView(deleteReviewButton);

                ReviewsLinearLayout.addView(ButtonsLinearLayout);
            }
            uniqueCounter += 4;
            reviewCounter++;
        }
    }

    /**
     * Saves new review to database.
     * Overwrites previous one.
     *
     * @param username
     * @param name
     */
    private void saveNewReview(final String username, final String name) {
        String new_review = enter_new_review_field.getText().toString();

        // Check if review is empty
        if (TextUtils.isEmpty(new_review)) {
            enter_new_review_field.setError("Review cannot be empty!");
            // request focus to the text field
            enter_new_review_field.requestFocus();
            return;
        }

        // Replace previous review
        userReviews.removeAll("reviews", Arrays.asList(originalReview));
        userReviews.put("username", username);
        try {
            userReviews.save();
        } catch (ParseException e) {
            Toast.makeText(ViewTutor.this, "Problem modifying existing review: deleting unsuccessful",
                    Toast.LENGTH_LONG).show();
        }

        userReviews.add("reviews", ParseUser.getCurrentUser().getUsername() + ":::" + new_review);
        userReviews.put("username", username);


        try {
            userReviews.save();
            Toast.makeText(ViewTutor.this, "Saved, thank you!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ViewTutor.this, ViewTutor.class);
            intent.putExtra("username", username);
            intent.putExtra("name", name);
            startActivity(intent);
        } catch (ParseException e) {
            Toast.makeText(ViewTutor.this, "Problem storing review " + e, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Makes a review field editable
     *
     * @param id
     */
    private void editReview(int id) {
        // edit review button id is 1 item ahead of the text view
        EditText review_text = (EditText) findViewById(id - 1);
        // put save to be clickable and edit to be nonclickable
        Button saveButton = (Button) findViewById(id + 1);
        saveButton.setEnabled(true);
        findViewById(id).setEnabled(false);

//        originalReview = ParseUser.getCurrentUser().getUsername() + ":::" +
//                review_text.getText().toString().substring(3);

        // enable editing
        review_text.setFocusable(true);
        review_text.setFocusableInTouchMode(true);
        review_text.setClickable(true);

        // request focus to the text field
        review_text.requestFocus();
    }

    /**
     * saves an editable review to database.
     * save and overwrite if editing a review.
     *
     * @param id
     * @param username
     * @param name
     */
    private void saveReview(int id, final String username, final String name) {
        // Grey out save button
        findViewById(id).setEnabled(false);

        // save review button id is 2 items ahead of the text view
        EditText review_text = (EditText) findViewById(id - 2);

        // Check if review contains >= 1 letter
        if (!review_text.getText().toString().matches(".*[a-zA-Z]+.*")) {
            review_text.setError("Review cannot be empty!");
            findViewById(id).setEnabled(true);
            // request focus to the text field
            review_text.requestFocus();
            return;
        }

        // Set edit button to be enabled
        findViewById(id - 1).setEnabled(true);

        // disable editing
        review_text.setFocusable(false);
        review_text.setFocusableInTouchMode(false);
        review_text.setClickable(false);

        // get review text
        String review = review_text.getText().toString();
        String sanitizedReview = review;

        // Check if first characters are the ordering numbers
        final Matcher matcher = Pattern.compile("[0-9]\\.").matcher(review);
        if(matcher.find()){
            sanitizedReview = review.substring(matcher.end()).trim();
        }

        // Replace previous review

        userReviews.removeAll("reviews", Arrays.asList(originalReview));
        userReviews.put("username", username);
        try {
            userReviews.save();
        } catch (ParseException e) {
            Toast.makeText(ViewTutor.this, "Problem modifying existing review: deleting unsuccessful",
                    Toast.LENGTH_LONG).show();
        }


        userReviews.add("reviews", ParseUser.getCurrentUser().getUsername() + ":::" + sanitizedReview);
        userReviews.put("username", username);
        try {
            userReviews.save();
            Toast.makeText(ViewTutor.this, "Saved, thank you!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ViewTutor.this, ViewTutor.class);
            intent.putExtra("username", username);
            intent.putExtra("name", name);
            startActivity(intent);
        } catch (ParseException e) {
            Toast.makeText(ViewTutor.this, "Problem storing review " + e, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * deletes an existing review from database
     *
     * @param id
     * @param username
     * @param name
     */
    private void deleteReview(int id, final String username, final String name) {
        // delete review button id is 3 items ahead of the text view
        EditText review_text = (EditText) findViewById(id - 3);

        originalReview = ParseUser.getCurrentUser().getUsername() + ":::" +
                review_text.getText().toString().substring(3);

        userReviews.removeAll("reviews", Arrays.asList(originalReview));
        userReviews.put("username", username);
        // Fire off Parse event in background thread
        try {
            userReviews.save();
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(ViewTutor.this, "Problem deleting review: " + e, Toast.LENGTH_LONG).show();
        }
        Toast.makeText(ViewTutor.this, "Deleted, thank you!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(ViewTutor.this, ViewTutor.class);
        intent.putExtra("username", username);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    /**
     * Get the data to display for a tutor
     *
     * @param username Username for Ratings table
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
                        loadProfilePicFromParse(user);
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
                    } catch (Exception p) {
                        Toast.makeText(ViewTutor.this, "Problem fetching data from Ratings table" + p, Toast.LENGTH_LONG).show();
                    }

                    // Check if user has already rated tutor and if yes set ratings bar to be non editable
                    try {
                        for (Object usernameAndRating : userRating.getList("ratedBy")) {
                            if (usernameAndRating.toString().contains(ParseUser.getCurrentUser().getUsername())) {
                                notRatedYet = false;
                                float stars = Float.parseFloat(usernameAndRating.toString().split(",")[1]);
                                rating_bar.setRating(stars);
                                rating_bar.setIsIndicator(true);
                            }
                        }
                    } catch (NullPointerException npe) {
                    }

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

                    if (!TextUtils.isEmpty(user.getString("phone"))) {
                        phone.setText(user.getString("phone"));
                    } else {
                        phone.setText("");
                        callButton.setVisibility(View.GONE);
                        msgButton.setVisibility(View.GONE);
                    }

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
                        ratingCounter.setText("(" + String.valueOf((int) rating_counter) + ")");
                    } else ratingCounter.setText("");
                } else {
                    Toast.makeText(ViewTutor.this, "Problem fetching user from database" + e, Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    public void onFavorite(View view) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String> favoritesList =  (List<String>) currentUser.get("favorites");
        if (favoriteButton.isChecked()){
            favoritesList.add(tutorUsername);
            currentUser.put("favorites", favoritesList);
            currentUser.saveInBackground();
            Toast.makeText(ViewTutor.this, "Added to favorites", Toast.LENGTH_LONG).show();
        }
        else{
            favoritesList.remove(tutorUsername);
            currentUser.put("favorites", favoritesList);
            currentUser.saveInBackground();
            Toast.makeText(ViewTutor.this, "Removed from favorites", Toast.LENGTH_LONG).show();
        }
    }

    public void initializeFavoritesButton(String username){

        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseUser.getCurrentUser().refreshInBackground(new RefreshCallback() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // Success!
                } else {
                    Toast.makeText(ViewTutor.this, "Could not refresh current user", Toast.LENGTH_LONG).show();
                }
            }
        });
        List<String> favoritesList =  (List<String>) currentUser.get("favorites");


        for (int i = 0; i < favoritesList.size(); i++) {
            if (favoritesList.get(i).equals(username)) {
                favoriteButton.setChecked(true);
                break;
            } else {
                favoriteButton.setChecked(false);
            }
        }
    }

    /* HELPER METHODS */

    /**
     * Get average rating.
     */
    public double averageRating(int newCounter, float rating, double old_rating, double rating_counter) {
        double average = (rating_counter == 0) ?
                rating : ((old_rating * rating_counter) + rating) / (newCounter);
        return average;
    }
}