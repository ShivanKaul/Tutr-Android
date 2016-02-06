package com.example.android.tutr;

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
import com.parse.LogInCallback;
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
    private UserRegisterTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText nameView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        //Parse.initialize(this);


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

        // hide the action bar
        //getActionBar().hide();
        //getSupportActionBar().s;
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private boolean cancel = false;
    private View focusView = null;

    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        nameView.setError(null);


        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();


        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else validatePassword(password);

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        // Check for name
        if (TextUtils.isEmpty(password)) {
            nameView.setError(getString(R.string.error_field_required));
            focusView = nameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login/registration attempt.

            mAuthTask = new UserRegisterTask(email, password);
            /*
                IMPORTANT: We need two buttons. Login and register. If login clicked:
             */
            // mAuthTask.loginUser();
            // If register:
            // mAuthTask.registerUser();

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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {
        private final String mEmail;
        private final String mPassword;

        UserRegisterTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Send request to Parse and match if user name and password exist
            //
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

        }

        void loginUser() {
            ParseUser.logInInBackground(mEmail, mPassword, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        // Hooray! The user is logged in.
                    } else {
                        // Signup failed. Look at the ParseException to see what happened.
                    }
                }
            });
        }

        void registerUser() {
            ParseUser user = new ParseUser();
            user.setUsername(mEmail);
            user.setPassword(mPassword);
//            user.setEmail(email);
            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        // Hooray! Let them use the app now.
                    } else {
                        // Sign up didn't succeed. Look at the ParseException
                        // to figure out what went wrong
                    }
                }
            });
        }
    }
}

