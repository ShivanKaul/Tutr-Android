package com.example.android.tutr;

import android.content.Context;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Dylan Chan on 3/18/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileEditActivityTest extends TestCase {
    private static Context editProfile;
    ProfileEditActivity activity = new ProfileEditActivity();
    @Rule
    public ActivityTestRule<ProfileEditActivity> mActivityRule = new ActivityTestRule<>(ProfileEditActivity.class);
    @Before
    public void setup() {
        editProfile = InstrumentationRegistry.getContext();

    }
    /*@Test
    public void handlePictureFormatTest() {
        Uri image = Uri.parse("image.jpeg");
        assertTrue(activity.handlePictureFormat(image));

    }*/
}