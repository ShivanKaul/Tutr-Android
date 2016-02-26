package com.example.android.tutr;

/**
 * Created by sangmoon on 2016-02-26.
 */
public class CourseValidator {
    public static boolean isValidCourse(String c) {
        return c.matches("^[a-z].*?\\d$");
    }
}
