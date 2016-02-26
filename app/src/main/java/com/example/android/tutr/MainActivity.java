package com.example.android.tutr;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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

import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    int check;
    View headerLayout;
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

        headerLayout = navigationView.getHeaderView(0);


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

            TextView userName = (TextView) headerLayout.findViewById(R.id.userNameNav);
            userName.setText(user.getString("name"));

            TextView userEmail = (TextView) headerLayout.findViewById(R.id.userEmailNav);
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
            //startActivity(new Intent(MainActivity.this, LoginActivity.class));
            //finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onSearch(View view) {
        //TODO
        // FETCH STUFF FROM THE INTERNET
        // UPDATE XX RESULTS FOUND
        EditText inputName = (EditText) findViewById(R.id.nameInput);
        EditText inputCourse = (EditText) findViewById(R.id.classInput);
        String name = inputName.getText().toString();
        String course = inputCourse.getText().toString().replaceAll("\\s+", "").toLowerCase();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");

        check = inputChecker(name, course);

        if (check == 0) {
            Toast.makeText(MainActivity.this, "Empty Search Parameters", Toast.LENGTH_LONG).show();
        } else if (check == 1) {
            query.whereStartsWith("name", name);
            query.orderByDescending("rating");
        } else if (check == 2) {
            query.whereEqualTo("courses", course);
            query.orderByDescending("rating");
        } else if (check == 3) {
            query.whereStartsWith("name", name);
            query.whereEqualTo("courses", course);
            query.orderByDescending("rating");
        } else {
            Toast.makeText(MainActivity.this, "Names only contain standard alphabet", Toast.LENGTH_LONG).show();
        }

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() == 0) {
                        if (check == 1) {
                            Toast.makeText(MainActivity.this, "No Results found for tutor name specified", Toast.LENGTH_LONG).show();
                        } else if (check == 2) {
                            Toast.makeText(MainActivity.this, "No Results found for course specified", Toast.LENGTH_LONG).show();
                        } else if (check == 3) {
                            Toast.makeText(MainActivity.this, "No Results", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        populateResultsList(objects);
                    }


                    for (int i = 0; i < objects.size(); i++) {
                        ParseObject user = objects.get(i); // Gets first object
                        System.out.println(user.getString("username"));
                    }
                } else {
                    //request has failed
                }
            }
        });
    }

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
        if (currentOrdering.getConstantState().equals(noArrow.getConstantState()))
            hourlyButton.setCompoundDrawablesWithIntrinsicBounds(null, null, upArrow, null);

            //From down to up
        else if (currentOrdering.getConstantState().equals(downArrow.getConstantState()))
            hourlyButton.setCompoundDrawablesWithIntrinsicBounds(null, null, upArrow, null);

            //From up to down
        else if (currentOrdering.getConstantState().equals(upArrow.getConstantState()))
            hourlyButton.setCompoundDrawablesWithIntrinsicBounds(null, null, downArrow, null);

    }

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
        if (currentOrdering.getConstantState().equals(noArrow.getConstantState()))
            ratingButton.setCompoundDrawablesWithIntrinsicBounds(null, null, downArrow, null);

            //From down to up
        else if (currentOrdering.getConstantState().equals(downArrow.getConstantState()))
            ratingButton.setCompoundDrawablesWithIntrinsicBounds(null, null, upArrow, null);

            //From up to down
        else if (currentOrdering.getConstantState().equals(upArrow.getConstantState()))
            ratingButton.setCompoundDrawablesWithIntrinsicBounds(null, null, downArrow, null);
    }


    private static void setButtonTint(Button button, ColorStateList tint) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP && button instanceof AppCompatButton) {
            ((AppCompatButton) button).setSupportBackgroundTintList(tint);
        } else {
            ViewCompat.setBackgroundTintList(button, tint);
        }
    }


    private void populateResultsList(List<ParseObject> values) {
        LinearLayout searchResultLayout = (LinearLayout) findViewById(R.id.searchResultLayout);
        //searchResultLayout.setVisibility(View.GONE);

        //Populate list view test
//        TutorListAdapter adapter = new TutorListAdapter(this, values);
//
//        //Get list and set adapter
//        ListView list = (ListView) findViewById(R.id.search_result_list);
//        list.setAdapter(adapter);
//
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                //TODO LOGIC ON CLICK
//                TextView textView = (TextView) view.findViewById(R.id.tutorName);
//                String message = textView.getText().toString();
//                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
//            }
//        });
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
