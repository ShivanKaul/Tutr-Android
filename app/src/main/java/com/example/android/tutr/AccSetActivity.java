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
 * A {@link PreferenceActivity} that presents a set of application account_settings. On
 * handset devices, account_settings are presented as a single list. On tablets,
 * account_settings are split by category, with category headers shown to the left of
 * the list of account_settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class AccSetActivity extends AppCompatActivity {

    View focusView = null;
    boolean cancel = false;
    EditText newNameTextField;

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

        // Check for a validity of input

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

        } else {
//            setNewPasswordOnParse(newPasswordString);
            updatedPW = true;
        }
        // PASSWORD IS NOW SAVED IF ALL RELEVANT FIELDS WERE ENTERED AND VALID

        if (TextUtils.isEmpty(newNameString)) {
            cancel = true;
        } else {
            if (checkValidName(newNameString)) {
//                setNewNameOnParse(newNameString);
                updatedName = true;
            } else {
                cancel = true;
                newNameTextField.setError(getString(R.string.error_invalid_name));
                focusView = newNameTextField;
                focusView.requestFocus();
            }
        }

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

        if (updatedPW)
        {
            ParseUser.logOut();
            Toast.makeText(AccSetActivity.this, "Please login again!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AccSetActivity.this, LoginActivity.class));
        }
        else if (updatedName)
            startActivity(new Intent(AccSetActivity.this, MainActivity.class));
    }

    // returns true if valid password
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

    //Checks if name is valid
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

    protected void setNewNameOnParse(String username) {
        // TODO CHECK PARSE FUNCTIONALITY
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put("name", username);
        try {
            currentUser.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    protected void setNewPasswordOnParse(String new_password) {
        // TODO CHECK PARSE FUNCTIONALITY
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.setPassword(new_password);
        try {
            currentUser.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    protected void setNewCredentialsOnParse(String new_password, String username) {
        // TODO CHECK PARSE FUNCTIONALITY
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.setPassword(new_password);
        currentUser.put("name", username);
        try {
            currentUser.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean isAlpha(String name) {
        return name.matches("[a-zA-Z]+");
    }
}
