package com.example.android.tutr;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View.OnClickListener;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.ParseUser;

import android.widget.AdapterView.OnItemLongClickListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

        View headerLayout;

    /**
     * Initializes the activity and the view of the main page
     * @param savedInstanceState
     *
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

        addListenerToOrderingButtons();
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
     *Execute code when activity is resumed
     */
    @Override
    public void onResume(){
        super.onResume();
        try{
            ParseUser user = ParseUser.getCurrentUser();

            TextView userName = (TextView) headerLayout.findViewById(R.id.userNameNav);
            userName.setText(user.getString("name"));

            TextView userEmail = (TextView) headerLayout.findViewById(R.id.userEmailNav);
            userEmail.setText(user.getEmail());
        }
        catch (Exception e){}

    }

    /**
     * Execute item code when it is pressed on navigation drawer
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
            if(ParseUser.getCurrentUser() == null) {
                Toast.makeText(MainActivity.this, "Logout Successful!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
            else{
                Toast.makeText(MainActivity.this, "Logout Unsuccessful!", Toast.LENGTH_LONG).show();
            }

        }  else if (id == R.id.nav_account_mod) {
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

    public void onSearch(View view){
        //TODO
        // FETCH STUFF FROM THE INTERNET
        // UPDATE XX RESULTS FOUND

        String[] values = {"test1", "test2", "test3", "test1", "test2", "test3", "test1", "test2", "test3", "test1", "test2", "test3",
                "test1", "test2", "test3", "test1", "test2", "test3", "test1", "test2", "test3", "test1", "test2", "test3",
                "test1", "test2", "test3", "test1", "test2", "test3", "test1", "test2", "test3", "test1", "test2", "test3"};

        populateResultsList(values);
    }

    private void addListenerToOrderingButtons(){
        final Button hourlyButton = (Button) findViewById(R.id.hourly_button);
        final Button ratingButton = (Button) findViewById(R.id.rating_button);

        final Drawable downArrow = ContextCompat.getDrawable(this, R.drawable.ic_keyboard_arrow_down_black_24dp);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_keyboard_arrow_up_black_24dp);
        final Drawable noArrow = ContextCompat.getDrawable(this, R.drawable.ic_remove_black_24dp);

        //TODO LOGIC ON CLICK
        hourlyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                hourlyButton.setCompoundDrawablesWithIntrinsicBounds(null, null, downArrow, null);
            }
        });

        ratingButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });

    }


    private void populateResultsList(String[] values){
        LinearLayout searchResultLayout = (LinearLayout) findViewById(R.id.searchResultLayout);
        //searchResultLayout.setVisibility(View.GONE);

        //Populate list view test
        TutorListAdapter adapter = new TutorListAdapter(this, values);

        //Get list and set adapter
        ListView list = (ListView) findViewById(R.id.search_result_list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //TODO LOGIC ON CLICK
                TextView textView = (TextView) view.findViewById(R.id.tutorName);
                String message = textView.getText().toString();
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
