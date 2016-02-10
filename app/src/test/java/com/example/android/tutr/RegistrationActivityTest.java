package com.example.android.tutr;

import android.app.Activity;
import static org.mockito.Mockito.*;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;
import android.test.mock.MockContext;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.Assert.*;

public class RegistrationActivityTest extends InstrumentationTestCase {
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
        user.put("name", "test");
        user.setPassword("TESTTESTTESt");
        user.setUsername("test@mcgill.ca");
        user.setEmail("test@mcgill.ca");
//        user.delete();
    }

    @Test
    public void testRegister() throws Exception {
        Context mContext = Mockito.mock(Context.class);
        String[] perms = {"android.permission.GET_ACCOUNTS", "android.permission.READ_PROFILE", "android.permission.READ_CONTACTS",
                "android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE"};
        PackageManager mPackageManager = Mockito.mock(PackageManager.class);
        PackageInfo mPackageInfo = Mockito.mock(PackageInfo.class);
        Mockito.when(mContext.getApplicationContext()).thenReturn(mContext);
        Mockito.when(mContext.getPackageManager()).thenReturn(mPackageManager);

        Mockito.when(mPackageManager.getApplicationInfo(any(String.class), anyInt())).thenReturn(null);
        Mockito.when(mPackageManager.getPackageInfo(any(String.class), anyInt())).thenReturn(mPackageInfo);
//        Mockito.when(mPackageInfo.requestedPermissions).thenReturn(perms);
//        Context context = new MockContext();
//        context.

        if (!PARSE_INITIALIZED) {
            Parse.initialize(mContext, "VkzVamQKx5aGl1pFvVZ0HCx1ewHn1lfdQcFm1aJk", "SRNV5PCkuCkAFubGl8nXydaRYoCgbqFvrTZhU1bm");
            PARSE_INITIALIZED = true;
        }
        ParseUser user = new ParseUser();
        user.put("name", "test");
        user.setPassword("TESTTESTTESt");
        user.setUsername("test@mcgill.ca");
        user.setEmail("test@mcgill.ca");
        user.signUp();
        // check if user exists
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("name", "test");
        query.whereEqualTo("password", "TESTTESTTESt");
        query.whereEqualTo("email", "test@mcgill.ca");
        List<ParseUser> users = query.find();
        assertEquals(users.size(), 2);
        assertEquals(users.get(0), user);

    }
}