package com.example.android.tutr;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int searchInputCheck;
    List<UserToRating> usersToRatings = null;
    View navHeaderLayout;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * Initializes the activity and the view of the main page
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHeaderLayout = navigationView.getHeaderView(0);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    /**
     * Called when the activity has detected the user's press of the back key.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Execute code when activity is resumed
     */
    @Override
    public void onResume() {
        super.onResume();
        try {
            ParseUser user = ParseUser.getCurrentUser();

            TextView userName = (TextView) navHeaderLayout.findViewById(R.id.userNameNav);
            userName.setText(user.getString("name"));

            TextView userEmail = (TextView) navHeaderLayout.findViewById(R.id.userEmailNav);
            userEmail.setText(user.getEmail());
        } catch (Exception e) {
        }

    }

    /**
     * Execute item code when it is pressed on navigation drawer
     *
     * @param item
     * @return returns true
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            ParseUser.logOut();
            if (ParseUser.getCurrentUser() == null) {
                Toast.makeText(MainActivity.this, "Logout Successful!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Logout Unsuccessful!", Toast.LENGTH_LONG).show();
            }

        } else if (id == R.id.nav_account_mod) {
            Intent intent = new Intent(this, AccSetActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_profile_mod) {
            Intent intent = new Intent(this, ProfileEditActivity.class);
            startActivity(intent);
        }

        else if (id == R.id.nav_favorites) {
            Intent intent = new Intent(this, FavoritesActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Fetch and parse the search results according to the user input.
     * @param view
     */
    public void onSearch(View view) {
        EditText inputName = (EditText) findViewById(R.id.nameInput);
        EditText inputCourse = (EditText) findViewById(R.id.classInput);

        String name = inputName.getText().toString();
        String course = inputCourse.getText().toString().replaceAll("\\s+", "").toLowerCase();

        //Clear listView
        ListView list = (ListView) findViewById(R.id.search_result_list);
        list.setAdapter(null);

        //Reset ordering buttons
        resetOrderingButtons();

        //Setup query
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        searchInputCheck = inputChecker(name, course);

        query.whereEqualTo("available", "yes");
        query.orderByAscending("name");

        if (searchInputCheck == 0) {
            Toast.makeText(MainActivity.this, "Empty Search Parameters", Toast.LENGTH_LONG).show();
            return;
        } else if (searchInputCheck == 1) {
            query.whereStartsWith("name", name);
        } else if (searchInputCheck == 2) {
            query.whereEqualTo("courses", course);
        } else if (searchInputCheck == 3) {
            query.whereStartsWith("name", name);
            query.whereEqualTo("courses", course);
        } else {
            Toast.makeText(MainActivity.this, "Names only contain standard alphabet", Toast.LENGTH_LONG).show();
            return;
        }

        //Fetch list
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> parseUsers, ParseException e) {
                if (e == null) {

                    List<ParseObject> parseUsersList = parseUsers;
                    TextView searchResultTextView = (TextView) findViewById(R.id.searchResultTextView);
                    searchResultTextView.setText("Search result - " + parseUsers.size() + " tutors found");

                    if (parseUsers.size() == 0) {
                        if (searchInputCheck == 1) {
                            Toast.makeText(MainActivity.this, "No Results found for tutor name specified.", Toast.LENGTH_LONG).show();
                        } else if (searchInputCheck == 2) {
                            Toast.makeText(MainActivity.this, "No Results found for course specified.", Toast.LENGTH_LONG).show();
                        } else if (searchInputCheck == 3) {
                            Toast.makeText(MainActivity.this, "No Results found.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
                        for (ParseObject user : parseUsersList) {
                            // Get rating from rating table
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Ratings");
                            query.whereEqualTo("username", user.get("username"));
                            queries.add(query);
                        }
                        List<ParseObject> ratings = null;
                        try {
                            ratings =  ParseQuery.or(queries).find();
                        } catch (ParseException p) {
                            Toast.makeText(MainActivity.this, "Problem fetching data from Ratings table" + p, Toast.LENGTH_LONG).show();
                            // What is even happening, just die
                            finish();
                        }
                        usersToRatings = zipLists(parseUsersList, ratings);
                        populateResults((ArrayList)usersToRatings);
                    }
                } else {
                    //request has failed
                    Toast.makeText(MainActivity.this, "Request failed, try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Return a list of UserToRating objects consisting of 2 parse objects, one for the rating and one for the tutor
     * @param users
     * @param ratings
     * @return
     */
    private List<UserToRating> zipLists(List<ParseObject> users, List<ParseObject> ratings) {
        if (users.size() != ratings.size()) complainAboutSizes(users.size(), ratings.size());
        List<UserToRating> usersToRatings = new ArrayList<UserToRating>();
        Iterator<ParseObject> i1 = users.iterator();
        Iterator<ParseObject> i2 = ratings.iterator();
        while (i1.hasNext() && i2.hasNext()) {
            usersToRatings.add(new UserToRating(i1.next(), i2.next()));
        }

        return usersToRatings;
    }

    /**
     * Notify user that the rating query and tutor query sizes didn't match
     * @param u
     * @param r
     */
    private void complainAboutSizes(int u, int r) {
        Toast.makeText(MainActivity.this, "Incompatible sizes of users: " + u + " and fetched ratings: " + r, Toast.LENGTH_LONG).show();
        // What is even happening, just die
        finish();
    }

    /**
     * Check the search input and assign in to one of 5 categories.
     * @param name
     * @param course
     * @return
     */
    public static int inputChecker(String name, String course) {
        if (name.matches("[A-Za-z]+") || name.equals("")) {
            if (name.equals("") && course.equals("")) {
                return 0;
            } else if (!name.equals("") && course.equals("")) {
                return 1;
            } else if (name.equals("") && !course.equals("")) {
                return 2;
            } else {
                return 3;
            }
        } else {
            return 4;
        }
    }

    /**
     * Reset the ordering buttons to their default state
     */
    private void resetOrderingButtons(){
        Button hourlyButton = (Button) findViewById(R.id.hourly_button);
        Button ratingButton = (Button) findViewById(R.id.rating_button);

        Drawable noArrow = ContextCompat.getDrawable(this, R.drawable.ic_remove_black_24dp);

        hourlyButton.setCompoundDrawablesWithIntrinsicBounds(null, null, noArrow, null);
        ratingButton.setCompoundDrawablesWithIntrinsicBounds(null, null, noArrow, null);

        setButtonTint(hourlyButton, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_material_light)));
        setButtonTint(ratingButton, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_material_light)));
    }

    /**
     * Repopulate the search result list displayed to the user according to the specified ordering by hourly rate
     * @param view
     */
    public void onHourlyClick(View view) {
        Button hourlyButton = (Button) findViewById(R.id.hourly_button);
        Button ratingButton = (Button) findViewById(R.id.rating_button);

        Drawable downArrow = ContextCompat.getDrawable(this, R.drawable.ic_keyboard_arrow_down_black_24dp);
        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_keyboard_arrow_up_black_24dp);
        Drawable noArrow = ContextCompat.getDrawable(this, R.drawable.ic_remove_black_24dp);

        setButtonTint(hourlyButton, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orderingButtonClicked)));
        setButtonTint(ratingButton, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_material_light)));
        ratingButton.setCompoundDrawablesWithIntrinsicBounds(null, null, noArrow, null);

        //Get the image
        Drawable currentOrdering = hourlyButton.getCompoundDrawables()[2];

        //From default to up
        if (currentOrdering.getConstantState().equals(noArrow.getConstantState())){
            hourlyButton.setCompoundDrawablesWithIntrinsicBounds(null, null, upArrow, null);
            usersToRatings = orderList(usersToRatings, "hourlyAscending");
            if(usersToRatings != null) populateResults((ArrayList) usersToRatings);
        }

        //From down to up
        else if (currentOrdering.getConstantState().equals(downArrow.getConstantState())){
            hourlyButton.setCompoundDrawablesWithIntrinsicBounds(null, null, upArrow, null);
            usersToRatings = orderList(usersToRatings, "hourlyAscending");
            if(usersToRatings != null) populateResults((ArrayList) usersToRatings);
        }

        //From up to down
        else if (currentOrdering.getConstantState().equals(upArrow.getConstantState())){
            hourlyButton.setCompoundDrawablesWithIntrinsicBounds(null, null, downArrow, null);
            usersToRatings = orderList(usersToRatings, "hourlyDescending");
            if(usersToRatings != null) populateResults((ArrayList) usersToRatings);
        }
    }

    /**
     * Repopulate the search result list displayed to the user according to the specified ordering by rating
     * @param view
     */
    public void onRatingClick(View view) {
        Button hourlyButton = (Button) findViewById(R.id.hourly_button);
        Button ratingButton = (Button) findViewById(R.id.rating_button);

        Drawable downArrow = ContextCompat.getDrawable(this, R.drawable.ic_keyboard_arrow_down_black_24dp);
        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_keyboard_arrow_up_black_24dp);
        Drawable noArrow = ContextCompat.getDrawable(this, R.drawable.ic_remove_black_24dp);

        setButtonTint(ratingButton, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orderingButtonClicked)));
        setButtonTint(hourlyButton, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_material_light)));
        hourlyButton.setCompoundDrawablesWithIntrinsicBounds(null, null, noArrow, null);

        //Get the image
        Drawable currentOrdering = ratingButton.getCompoundDrawables()[2];

        //From default to down
        if (currentOrdering.getConstantState().equals(noArrow.getConstantState())){
            ratingButton.setCompoundDrawablesWithIntrinsicBounds(null, null, downArrow, null);
            usersToRatings = orderList(usersToRatings, "ratingDescending");
            if(usersToRatings != null) populateResults((ArrayList) usersToRatings);
        }

        //From down to up
        else if (currentOrdering.getConstantState().equals(downArrow.getConstantState())){
            ratingButton.setCompoundDrawablesWithIntrinsicBounds(null, null, upArrow, null);
            usersToRatings = orderList(usersToRatings, "ratingAscending");
            if(usersToRatings != null) populateResults((ArrayList) usersToRatings);
        }


        //From up to down
        else if (currentOrdering.getConstantState().equals(upArrow.getConstantState())){
            ratingButton.setCompoundDrawablesWithIntrinsicBounds(null, null, downArrow, null);
            usersToRatings = orderList(usersToRatings, "ratingDescending");
            if(usersToRatings != null) populateResults((ArrayList) usersToRatings);
        }
    }

    /**
     * Reorder the search result list according to one of the 4 available ordering type.
     * @param usersToRatings
     * @param orderingType
     * @return
     */
    private List<UserToRating> orderList(List<UserToRating> usersToRatings, String orderingType) {

        if (usersToRatings == null)
            return null;

        if(orderingType.equals("hourlyAscending")){
            Collections.sort(usersToRatings, new Comparator<UserToRating>() {
                @Override
                public int compare(final UserToRating user1, final UserToRating user2) {
                    if (user1.getUser().getDouble("hourlyRate") < user2.getUser().getDouble("hourlyRate"))
                        return -1;
                    if (user1.getUser().getDouble("hourlyRate") > user2.getUser().getDouble("hourlyRate"))
                        return 1;
                    return 0;
                }
            });
        }
        else if(orderingType.equals("hourlyDescending")){
            Collections.sort(usersToRatings, new Comparator<UserToRating>() {
                @Override
                public int compare(final UserToRating user1, final UserToRating user2) {
                    if (user1.getUser().getDouble("hourlyRate") < user2.getUser().getDouble("hourlyRate")) return 1;
                    if (user1.getUser().getDouble("hourlyRate") > user2.getUser().getDouble("hourlyRate")) return -1;
                    return 0;
                }
            });
        }

        else if(orderingType.equals("ratingAscending")){
            Collections.sort(usersToRatings, new Comparator<UserToRating>() {
                @Override
                public int compare(final UserToRating user1, final UserToRating user2) {
                    if (user1.getRating().getDouble("rating") < user2.getRating().getDouble("rating")) return -1;
                    if (user1.getRating().getDouble("rating") > user2.getRating().getDouble("rating")) return 1;
                    return 0;
                }
            });
        }
        else if(orderingType.equals("ratingDescending")){
            Collections.sort(usersToRatings, new Comparator<UserToRating>() {
                @Override
                public int compare(final UserToRating user1, final UserToRating user2) {
                    if (user1.getRating().getDouble("rating") < user2.getRating().getDouble("rating")) return 1;
                    if (user1.getRating().getDouble("rating") > user2.getRating().getDouble("rating")) return -1;
                    return 0;
                }
            });
        }
        return usersToRatings;
    }

    /**
     * Set the tint of the ordering button.
     * @param button
     * @param tint
     */
    private static void setButtonTint(Button button, ColorStateList tint) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP && button instanceof AppCompatButton) {
            ((AppCompatButton) button).setSupportBackgroundTintList(tint);
        } else {
            ViewCompat.setBackgroundTintList(button, tint);
        }
    }


    /**
     * Populates the search results list to be displayed to the user
     * @param userToRatings
     */
    private void populateResults(ArrayList<UserToRating> userToRatings) {
        LinearLayout searchResultLayout = (LinearLayout) findViewById(R.id.searchResultLayout);

        //Populate list view test
        TutorListAdapter adapter = new TutorListAdapter(this, userToRatings, false);

        //Get list and set adapter
        ListView list = (ListView) findViewById(R.id.search_result_list);
        list.setAdapter(adapter);

        //Set on item click listener
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Redirects to the tutor's profile page
                UserToRating clickedUser = (UserToRating) parent.getItemAtPosition(position);
                String username = clickedUser.getUser().getString("username");
                String name = clickedUser.getUser().getString("name");
                Intent intent = new Intent(MainActivity.this, ViewTutor.class);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.android.tutr/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.android.tutr/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
