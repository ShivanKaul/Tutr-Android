package com.example.android.tutr;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.test.InstrumentationTestCase;
import android.test.mock.MockContext;

import com.parse.Parse;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;

/**
 * Created by Tong on 19/03/2016.
 */
public class FavoritesActivityTest extends InstrumentationTestCase {
    private static boolean PARSE_INITIALIZED = false;


    @Before
    @After
    public void clean() throws Exception {
        Context context = new MockContext();
//        RegistrationActivity reg = new RegistrationActivity();
        if (!PARSE_INITIALIZED) {
            Parse.initialize(context);
            PARSE_INITIALIZED = true;
        }
        // delete user from parse
        ParseUser user = new ParseUser();
        user.put("name", "Tong");
        user.setPassword("Qwert123");
        user.setUsername("yito@mail.mcgill.ca");
        user.setEmail("yito@mail.mcgill.ca");
//        user.delete();
    }

    @Test
    public void testonButtonClickListner() throws Exception {
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
        List<ParseUser> users = query.find();
        String[] favorites = {"last.test@mail.mcgill.ca", "demo2@mail.mcgill.ca"};
        assertEquals(favorites.length, 2);
        assertEquals(favorites[0], "last.test@mail.mcgill.ca");

    }
}