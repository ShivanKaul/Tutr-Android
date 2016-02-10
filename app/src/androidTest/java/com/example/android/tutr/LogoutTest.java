package com.example.android.tutr;

import android.content.Context;

import android.util.Log;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.test.suitebuilder.annotation.LargeTest;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static org.junit.Assert.*;

/**
 * Created by Jado on 09/02/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LogoutTest{

    Context context;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);


    @Test
    public void logoutTest() throws InterruptedException {

        //Create context
        context = mActivityRule.getActivity().getBaseContext();
        Parse.initialize(context, "VkzVamQKx5aGl1pFvVZ0HCx1ewHn1lfdQcFm1aJk", "SRNV5PCkuCkAFubGl8nXydaRYoCgbqFvrTZhU1bm");

        //Login
        ParseUser.logInInBackground("test@mcgill.ca", "Qwertyu1", new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                    Log.d("Login", "Success");
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    Log.d("Login", "Failure");
                }
            }
        });

        //Logout test
        assertTrue(ParseUser.getCurrentUser() != null);
        ParseUser.logOut();
        assertTrue(ParseUser.getCurrentUser() == null);
    }
}



