package com.example.android.tutr;

public class CourseValidator {
    public static boolean isValidCourse(String c) {
        return c.matches("^[a-z].*?\\d$");
    }
}
