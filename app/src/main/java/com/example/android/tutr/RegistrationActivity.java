package com.example.android.tutr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.squareup.picasso.Picasso;

import java.util.regex.Pattern;


/**
 * A registration screen that offers registering via email/password.
 */
public class RegistrationActivity extends AppCompatActivity implements EditText.OnEditorActionListener {

    // UI references.
    private EditText mEmailView;
    private EditText nameView;
    private EditText mPasswordView;

    // Keep track of whether registering has been cancelled
    private boolean cancel = false;
    private View focusView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // load an image to the image view
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Picasso.with(this).load("file:///android_asset/tutr_img.jpg").fit().into(imageView);

        // declare all the edit text : email, name, password
        // and set the to answer on editActionListenner from the pop up keyboard
        mEmailView = (EditText) findViewById(R.id.email);
        mEmailView.setOnEditorActionListener(this);

        nameView = (EditText) findViewById(R.id.name);
        nameView.setOnEditorActionListener(this);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(this);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // On register button being clicked, start registration flow
                attemptRegister();
            }
        });
    }

    /**
     * Registration logic
     */
    private void attemptRegister() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        nameView.setError(null);

        // Store values at the time of the login attempt.
        final String mEmail = mEmailView.getText().toString();
        final String mPassword = mPasswordView.getText().toString();
        final String mName = nameView.getText().toString();

        // Check for a valid password
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else validatePassword(mPassword);

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(mEmail)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        // Check for name
        if (TextUtils.isEmpty(mName)) {
            nameView.setError(getString(R.string.error_field_required));
            focusView = nameView;
            cancel = true;
        }
        if (!mName.matches("[a-zA-Z]+")) { // no numbers
            nameView.setError(getString(R.string.error_name_numbers));
            focusView = nameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            cancel = false;
            focusView.requestFocus();
        } else {
            // Validity checks passed and everything was A-OK, register!
            register(mEmail, mPassword, mName);
        }
    }

    /**
     * Helper function to searchInputCheck if email valid
     * @param email
     * @return true/false
     */
    private boolean isEmailValid(String email) {
        final String email_pattern = "[0-9a-z]+.?[0-9a-z]+@(mail.)?mcgill.ca";
        return Pattern.compile(email_pattern).matcher(email).matches();
    }

    /**
     * Validate the password, and if it doesn't conform report an error
     * @param password
     */
    private void validatePassword(String password) {
        boolean hasCapLetter = !password.equals(password.toLowerCase());
        boolean haslowerCaseLetter = !password.equals(password.toUpperCase());

        // handle length of password
        if (password.length() < 8 || password.length() > 16) {
            mPasswordView.setError(getString(R.string.error_length_password));
            focusView = mPasswordView;
            cancel = true;
        } else if (!hasCapLetter) {
            mPasswordView.setError(getString(R.string.error_CapCase));
            focusView = mPasswordView;
            cancel = true;
        } else if (!haslowerCaseLetter) {
            mPasswordView.setError(getString(R.string.error_lowerCase));
            focusView = mPasswordView;
            cancel = true;
        }
    }

    /**
     * Attempt to register when clicking on Send in the pop up keyboard for email and password and name
     * @param v
     * @param actionId
     * @param event
     * @return
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        if (actionId == R.id.register || actionId == EditorInfo.IME_ACTION_SEND) {
            attemptRegister();
            handled = true;
        }
        return handled;
    }

    /**
     * Register using the Parse API
     * @param mEmail
     * @param mPassword
     * @param mName
     */
    public void register(final String mEmail, final String mPassword, final String mName) {

        final ParseUser user = new ParseUser();

        user.put("name", mName);
        user.setPassword(mPassword);
        user.setUsername(mEmail);
        user.setEmail(mEmail);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {

                    // If sign up was successful, populate Ratings table (just to be safe)
                    ParseObject userRating = new ParseObject("Ratings");
                    userRating.put("username", mEmail);
                    userRating.put("rating", 0);
                    userRating.put("ratingCount", 0);

                    try {
                        userRating.save();
                        // Shivan: I chose not to raise an exception or error message here,
                        // so as to not detract from the "UX"
                    } catch (ParseException p) {}


                    Toast.makeText(RegistrationActivity.this, "Registration Successful!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                    finish();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }
        });
    }
}

