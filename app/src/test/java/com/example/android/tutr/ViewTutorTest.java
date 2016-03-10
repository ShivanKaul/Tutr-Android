package com.example.android.tutr;

import junit.framework.TestCase;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Dylan Chan on 3/9/2016.
 */
public class ViewTutorTest extends TestCase {
    //   averageRating(int new_counter, float rating, double old_rating, double rating_counter);
    @Test
    public void testAverage() {
        assertEquals(2.57, ViewTutor.averageRating(10, 5.0f, 2.3, 9.0), 0.001);
        assertEquals(3.875, ViewTutor.averageRating(4, 3.5f, 4.0, 3.0), 0.001);
        assertEquals(4.8909, ViewTutor.averageRating(11, 3.8f, 5.0, 10.0), 0.001);
        assertEquals(4.117, ViewTutor.averageRating(51, 0.00f, 4.2,50.0), 0.001);

        //test with no rating beforehand
        assertEquals(3.888, ViewTutor.averageRating(1, 3.888f, 0.0, 0), 0.0001);
    }
}