package com.example.android.tutr;

import org.junit.*;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.test.InstrumentationTestCase;
import android.test.mock.MockContext;

import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ParseObject;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;

/**
 * import static org.junit.Assert.*;
 * <p/>
 * /**
 * Created by Dylan Chan on 3/9/2016.
 */
public class ViewTutorTest extends InstrumentationTestCase {
    private static boolean PARSE_INITIALIZED = false;

    @Before
    @After
    public void clean() throws Exception {

        Context context = new ViewTutor();
    }

    //   averageRating(int new_counter, float rating, double old_rating, double rating_counter);
    @Test
    public void testAverage() {
        Assert.assertEquals(2.57, ViewTutor.averageRating(10, 5.0f, 2.3, 9.0), 0.001);
        Assert.assertEquals(3.875, ViewTutor.averageRating(4, 3.5f, 4.0, 3.0), 0.001);
        Assert.assertEquals(4.8909, ViewTutor.averageRating(11, 3.8f, 5.0, 10.0), 0.001);
        Assert.assertEquals(4.117, ViewTutor.averageRating(51, 0.00f, 4.2, 50.0), 0.001);

        //test with no rating beforehand
        Assert.assertEquals(3.888, ViewTutor.averageRating(1, 3.888f, 0.0, 0), 0.0001);
    }

    @Test
    public void testReview() throws Exception {

        String review = "";

        Context mContext = Mockito.mock(Context.class);
        String[] perms = {"android.permission.GET_ACCOUNTS", "android.permission.READ_PROFILE", "android.permission.READ_CONTACTS",
                "android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE"};
        PackageManager mPackageManager = Mockito.mock(PackageManager.class);
        PackageInfo mPackageInfo = Mockito.mock(PackageInfo.class);
        Mockito.when(mContext.getApplicationContext()).thenReturn(mContext);
        Mockito.when(mContext.getPackageManager()).thenReturn(mPackageManager);

        Mockito.when(mPackageManager.getApplicationInfo(any(String.class), anyInt())).thenReturn(null);
        Mockito.when(mPackageManager.getPackageInfo(any(String.class), anyInt())).thenReturn(mPackageInfo);


        if (!PARSE_INITIALIZED) {
            Parse.initialize(mContext, "VkzVamQKx5aGl1pFvVZ0HCx1ewHn1lfdQcFm1aJk", "SRNV5PCkuCkAFubGl8nXydaRYoCgbqFvrTZhU1bm");
            PARSE_INITIALIZED = true;
        }
        ParseUser user = new ParseUser();
        user.put("name", "Tong");
        user.setPassword("Qwert123");
        user.setUsername("yito@mail.mcgill.ca");
        user.setEmail("yito@mail.mcgill.ca");
        user.signUp();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("name", "Tong");
        query.whereEqualTo("password", "Qwert123");
        query.whereEqualTo("email", "yito@mail.mcgill.ca");


        ParseObject userReviews = query.find().get(0);
        List reviews = userReviews.getList("reviews");

        for (Object usernameAndReviewFromParse : reviews) {
            if (usernameAndReviewFromParse.toString().split(":::").length == 2) {
                review = usernameAndReviewFromParse.toString().split(":::")[1];
            }
        }
        assertEquals(review, "You Are the best TA EVER");

    }


}