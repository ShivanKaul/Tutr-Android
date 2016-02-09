package com.example.android.tutr;


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Used to update user password and name on the Parse database.
 */
public class AccSetActivity extends AppCompatActivity {

    View focusView = null;
    boolean cancel = false;
    EditText newNameTextField;

    /**
     * Overidden definition of the default onCreate method.
     * Opens "edit account" window; listens to clicks on button to save changes to the user account.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_settings);

        Button saveChangesButton = (Button) findViewById(R.id.accountSettingsSaveChangesButton);

        saveChangesButton.setOnClickListener(
                new OnClickListener() {
                    public void onClick(View view) {
                        saveChanges();
                    }
                });
    }

    /**
     * Acts on press of "Save Changes" button. Checks inputs and saves to Parse database if valid.
     */
    protected void saveChanges() {
        // get all use text inputs
        boolean updatedPW = false, updatedName = false;

        EditText newPasswordTextField = (EditText) findViewById(R.id.enterNewPassword);
        String newPasswordString = newPasswordTextField.getText().toString();

        EditText confirmPasswordTextField = (EditText) findViewById(R.id.confirmNewPassword);
        String confirmPasswordString = confirmPasswordTextField.getText().toString();

        newNameTextField = (EditText) findViewById(R.id.enterNewName);
        String newNameString = newNameTextField.getText().toString();
        // all user inputs acquired

        // IMPORTANT: RESET CONTROL AND VARS TO DEFAULT STATE
        focusView = null;
        cancel = false;
        newPasswordTextField.setError(null);
        confirmPasswordTextField.setError(null);
        newNameTextField.setError(null);

        // Check for validity of input

        boolean new_empty = TextUtils.isEmpty(newPasswordString),
                confirm_empty = TextUtils.isEmpty(confirmPasswordString);
        if (new_empty || confirm_empty) {
            cancel = true;
            if (new_empty ^ confirm_empty) {
                if (new_empty)
                    newPasswordTextField.setError(getString(R.string.error_field_required));
                if (confirm_empty)
                    confirmPasswordTextField.setError(getString(R.string.error_field_required));
            }
        } else {
            if (newPasswordString.equals(confirmPasswordString)) {
                if (validatePassword(newPasswordString, newPasswordTextField)) {
                    // password is valid. continue to saving
                } else {
                    focusView = newPasswordTextField;
                    // error fields are set by validatePassword function
                    focusView.requestFocus();
                    cancel = true;
                }
            } else {
                newPasswordTextField.setError(getString(R.string.error_field_not_matching));
                confirmPasswordTextField.setError(getString(R.string.error_field_not_matching));
                focusView = newPasswordTextField;
                focusView.requestFocus();
                cancel = true;
            }
        }

        if (cancel) {
            // nothing to do
        } else {
            // set flag to update password
            updatedPW = true;
        }

        if (TextUtils.isEmpty(newNameString)) {
            cancel = true;
        } else {
            if (checkValidName(newNameString)) {
                // set flag to update name
                updatedName = true;
            } else {
                // incorrect input was entered; handle errors
                cancel = true;
                newNameTextField.setError(getString(R.string.error_invalid_name));
                focusView = newNameTextField;
                focusView.requestFocus();
            }
        }

        // save appropriate fields to Parse; print UI feedback to user
        if (updatedName && updatedPW) {
            Toast.makeText(AccSetActivity.this, "Saved new name and password!", Toast.LENGTH_SHORT).show();
            setNewCredentialsOnParse(newPasswordString, newNameString);
        } else if (updatedName) {
            Toast.makeText(AccSetActivity.this, "Saved new name!", Toast.LENGTH_SHORT).show();
            setNewNameOnParse(newNameString);
        } else if (updatedPW) {
            Toast.makeText(AccSetActivity.this, "Saved new password!", Toast.LENGTH_SHORT).show();
            setNewPasswordOnParse(newPasswordString);
        } else
            Toast.makeText(AccSetActivity.this, "Nothing saved!", Toast.LENGTH_SHORT).show();

        // appy activity changes to the app (change screen)
        if (updatedPW)
        {
            ParseUser.logOut();
            Toast.makeText(AccSetActivity.this, "Please login again!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AccSetActivity.this, LoginActivity.class));
            finish();
        }
        else if (updatedName) {
            startActivity(new Intent(AccSetActivity.this, MainActivity.class));
            finish();
        }
        else {
            // nothing to do; all errors are displayed or corrected
        }
    }

    /**
     * returns true if valid password
     * @param password
     *              String input for password to validate
     * @param newPasswordText
     *              EditText field from the app UI for setting of errors related to input string
     * @return true if password is valid
     */
    private boolean validatePassword(String password, EditText newPasswordText) {
        boolean hasCapLetter = !password.equals(password.toLowerCase());
        boolean haslowerCaseLetter = !password.equals(password.toUpperCase());

        // handle length of password
        if (password.length() < 8 || password.length() > 16) {
            newPasswordText.setError(getString(R.string.error_length_password));
            focusView = newPasswordText;
            return false;
        } else if (!hasCapLetter) {
            newPasswordText.setError(getString(R.string.error_CapCase));
            focusView = newPasswordText;
            return false;
        } else if (!haslowerCaseLetter) {
            newPasswordText.setError(getString(R.string.error_lowerCase));
            focusView = newPasswordText;
            return false;
        }
        return true;
    }

    /**
     * Checks if input string representing name is valid according to project specified criteria.
     * @param s
     *          String input for name to validate
     * @return
     *         true if input name is valid
     */
    public boolean checkValidName(String s) {
        //Checks if nothing has been enetered in name field
        if (s.length() > 70) {
            return false;
            //Checks if name only contains letters
        } else if (!isAlpha(s)) {
            return false;
        }
        return true;
    }

    /**
     * Saves new "name" to Parse database
     * @param name
     *              String representing the "name" field of the user
     */
    protected void setNewNameOnParse(String name) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put("name", name);
        try {
            currentUser.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves new "password" to Parse database
     * @param new_password
     *              String representing the "password" field of the user
     */
    protected void setNewPasswordOnParse(String new_password) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.setPassword(new_password);
        try {
            currentUser.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves new "name" and "password" to Parse database together.
     * This method is needed because password and name must be saved together,
     * but not independently.
     * @param new_password
     *              String representing the new "password" field to be saved
     * @param name
     *              String representing the new "name" field to be saved
     */
    protected void setNewCredentialsOnParse(String new_password, String name) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.setPassword(new_password);
        currentUser.put("name", name);
        try {
            currentUser.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if input string contains only letters.
     * @param name
     * @return true is input only contains letters
     */
    public boolean isAlpha(String name) {
        return name.matches("[a-zA-Z]+");
    }
}
