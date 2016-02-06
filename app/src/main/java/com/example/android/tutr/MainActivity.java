package com.example.android.tutr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;

import com.parse.ParseUser;

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
}
