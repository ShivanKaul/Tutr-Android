package com.example.android.tutr;

public class CourseValidator {
    public static boolean isValidCourse(String c) {
        return c.matches("^[a-zA-Z]{4}[0-9]{3}$") || c.matches("^[a-zA-Z]{4} [0-9]{3}$");
    }
}
