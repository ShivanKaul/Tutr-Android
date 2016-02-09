package com.example.android.tutr;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.squareup.picasso.Picasso;

import java.util.regex.Pattern;


/**
 * A login screen that offers login via email/password.
 */
public class RegistrationActivity extends ActionBarActivity implements EditText.OnEditorActionListener {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private EditText mEmailView;
    private EditText nameView;
    private EditText mPasswordView;

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
                attemptRegister();
            }
        });
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private boolean cancel = false;
    private View focusView = null;

    private void attemptRegister() {
        System.out.println("DEBUG: Entered attemptRegister method");

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

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            cancel = false;
            focusView.requestFocus();
        } else {
            register(mEmail, mPassword, mName);
        }
    }

    private boolean isEmailValid(String email) {
        final String email_pattern = "[0-9a-z]+.?[0-9a-z]+@(mail.)?mcgill.ca";
        return Pattern.compile(email_pattern).matcher(email).matches();
    }

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

    // attempt to register when clicking on Send in the pop up keyboard for email and password and name
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        if (actionId == R.id.register || actionId == EditorInfo.IME_ACTION_SEND) {
            attemptRegister();
            handled = true;
        }
        return handled;
    }

    void register(final String mEmail, final String mPassword, final String mName) {
        ParseUser user = new ParseUser();
        user.put("name", mName);
        user.setPassword(mPassword);
        user.setUsername(mEmail);
        user.setEmail(mEmail);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
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

