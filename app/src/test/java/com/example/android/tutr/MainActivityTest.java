package com.example.android.tutr;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Created by yito on 2/28/16.
 */
public class MainActivityTest extends TestCase {

    MainActivity main = new MainActivity();
    @Test
    public void testInputChecker(){
        String nameAlphabet = "Jhonny";
        String nameNumber = "John123";
        String nameEmpty = "";
        String courseNotEmpty = "ECSE421";
        String courseEmpty = "";

        assertEquals(main.inputChecker(nameEmpty, courseEmpty), 0);
        assertEquals(main.inputChecker(nameAlphabet, courseEmpty), 1);
        assertEquals(main.inputChecker(nameEmpty, courseNotEmpty), 2);
        assertEquals(main.inputChecker(nameAlphabet, courseNotEmpty), 3);
        assertEquals(main.inputChecker(nameNumber, courseEmpty), 4);
        assertEquals(main.inputChecker(nameNumber, courseNotEmpty), 4);



    }
}