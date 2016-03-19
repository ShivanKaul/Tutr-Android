package com.example.android.tutr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.tutr.TutorListAdapter.customButtonListener;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity implements customButtonListener {

    TutorListAdapter adapter;
    int searchInputCheck;
    List<UserToRating> usersToRatings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        //Fetch and populate list
//        List<UserToRating> favorites = fetchFavoriteList();
//        populateResults((ArrayList) favorites);

        //Temporary code
        fetchFavoritesList();
    }

    private void fetchFavoritesList() {

        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String> favoritesList = (List<String>) currentUser.get("favorites");

        //Clear listView
        ListView list = (ListView) findViewById(R.id.favorites_list);
        list.setAdapter(null);

        //Setup query
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");

        query.whereEqualTo("available", "yes");
        query.orderByAscending("name");
        query.whereContainedIn("username", favoritesList);

        //Fetch list
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> parseUsers, ParseException e) {
                if (e == null) {

                    List<ParseObject> parseUsersList = parseUsers;
                    //TextView searchResultTextView = (TextView) findViewById(R.id.searchResultTextView);

                    if (parseUsers.size() == 0) {
                        Toast.makeText(FavoritesActivity.this, "No Favorites", Toast.LENGTH_LONG).show();

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
                            ratings = ParseQuery.or(queries).find();
                        } catch (ParseException p) {
                            Toast.makeText(FavoritesActivity.this, "Problem fetching data from Ratings table" + p, Toast.LENGTH_LONG).show();
                            // What is even happening, just die
                            finish();
                        }
                        usersToRatings = zipLists(parseUsersList, ratings);
                        populateResults((ArrayList) usersToRatings);
                    }
                } else {
                    //request has failed
                    Toast.makeText(FavoritesActivity.this, "Request failed, try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Return a list of UserToRating objects consisting of 2 parse objects, one for the rating and one for the tutor
     *
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
     *
     * @param u
     * @param r
     */
    private void complainAboutSizes(int u, int r) {
        Toast.makeText(FavoritesActivity.this, "Incompatible sizes of users: " + u + " and fetched ratings: " + r, Toast.LENGTH_LONG).show();
        // What is even happening, just die
        finish();
    }

    /**
     * Populates the search results list to be displayed to the user
     *
     * @param userToRatings
     */
    private void populateResults(ArrayList<UserToRating> userToRatings) {
        LinearLayout searchResultLayout = (LinearLayout) findViewById(R.id.searchResultLayout);

        //Populate list view test
        adapter = new TutorListAdapter(this, userToRatings, true);
        adapter.setCustomButtonListner(FavoritesActivity.this);

        //Get list and set adapter
        ListView list = (ListView) findViewById(R.id.favorites_list);
        list.setAdapter(adapter);

        //Set on item click listener
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Redirects to the tutor's profile page
                UserToRating clickedUser = (UserToRating) parent.getItemAtPosition(position);
                String username = clickedUser.getUser().getString("username");
                String name = clickedUser.getUser().getString("name");
                Intent intent = new Intent(FavoritesActivity.this, ViewTutor.class);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                startActivity(intent);

            }
        });
    }

    //TODO
    private void removeUserFromParse(ParseObject user) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String> favoritesList = (List<String>) currentUser.get("favorites");
        String username = user.getString("username");
        favoritesList.remove(username);
        currentUser.put("favorites", favoritesList);
        currentUser.saveInBackground();
        Toast.makeText(FavoritesActivity.this, username + " Removed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onButtonClickListner(int position, UserToRating user) {
        //Remove from list
        adapter.removeAt(position);
        adapter.notifyDataSetChanged();

        //Remove from parse
        removeUserFromParse(user.getUser());
    }

}
