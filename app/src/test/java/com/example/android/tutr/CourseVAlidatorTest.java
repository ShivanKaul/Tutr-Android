package com.example.android.tutr;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by sangmoon on 2016-02-26.
 */
public class CourseVAlidatorTest {
    @Test
    public void validCourses() {
        assertTrue(CourseValidator.isValidCourse("ecse123"));
        assertTrue(CourseValidator.isValidCourse("math111"));
        assertTrue(CourseValidator.isValidCourse("comp 202"));
        //assertTrue(CourseValidator.isValidCourse("oao3"));
    }

    @Test
    public void invalidCourses() {
        assertFalse(CourseValidator.isValidCourse("123ecse"));
        assertFalse(CourseValidator.isValidCourse("ecse"));
        assertFalse(CourseValidator.isValidCourse("123"));
        assertFalse(CourseValidator.isValidCourse("1k2j3kl1"));
    }
}
