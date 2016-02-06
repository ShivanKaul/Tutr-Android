package com.example.android.tutr;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

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
public class AccSetActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }


    View focusView = null;
    boolean cancel = false;
    boolean incorrect_old_pw = false;
    EditText oldPasswordTextField, newNameTextField;

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

        oldPasswordTextField = (EditText) findViewById(R.id.enterOldPassword);
        String oldPasswordString = oldPasswordTextField.getText().toString();

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
        incorrect_old_pw = false;
        oldPasswordTextField.setError(null);
        newPasswordTextField.setError(null);
        confirmPasswordTextField.setError(null);
        newNameTextField.setError(null);

        // Check for a validity of input
        if (TextUtils.isEmpty(oldPasswordString)) {
            oldPasswordTextField.setError(getString(R.string.error_field_required));
            focusView = oldPasswordTextField;
            cancel = true;
        }

        if (cancel) {
            // user did not enter old password (empty input)
        } else {
            // check if user entered correct old password
            // TODO CHECK PARSE FUNCTIONALITY
            ParseUser.logInInBackground(ParseUser.getCurrentUser().getUsername(), oldPasswordString, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user == null) {
                        incorrect_old_pw = true;
                        oldPasswordTextField.setError(getString(R.string.error_incorrect_password));
                        focusView = oldPasswordTextField;
                    } else {
                        // The password was correct
                        // continue to next step without any changes
                    }
                }
            });
        }

        if (incorrect_old_pw || cancel) {
            focusView.requestFocus();
        } else {
            boolean new_empty = TextUtils.isEmpty(newPasswordString),
                    confirm_empty = TextUtils.isEmpty(confirmPasswordString);
            if ( new_empty || confirm_empty) {
                cancel = true;
                if (new_empty ^ confirm_empty) {
                    if (new_empty) newPasswordTextField.setError(getString(R.string.error_field_required));
                    if (confirm_empty) confirmPasswordTextField.setError(getString(R.string.error_field_required));
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
                setNewPasswordOnParse(newPasswordString);
                updatedPW = true;
            }
            // PASSWORD IS NOW SAVED IF ALL RELEVANT FIELDS WERE ENTERED AND VALID

            if (TextUtils.isEmpty(newNameString)) {
                cancel = true;
            } else {
                if (checkValidName(newNameString)){
                    setNewNameOnParse(newNameString);
                    updatedName = true;
                }
                else {
                    cancel = true;
                    newNameTextField.setError(getString(R.string.error_invalid_name));
                    focusView = newNameTextField;
                    focusView.requestFocus();
                }
            }
        }

        if (updatedName && updatedPW) {
            Toast.makeText(AccSetActivity.this, "Saved new name and password!", Toast.LENGTH_SHORT).show();
        }
        else if (updatedName){
            Toast.makeText(AccSetActivity.this, "Saved new name!", Toast.LENGTH_SHORT).show();
        }
        else if (updatedPW){
            Toast.makeText(AccSetActivity.this, "Saved new password!", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(AccSetActivity.this, "Nothing saved!", Toast.LENGTH_SHORT).show();

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
        if (s.length() < 2) {
            return false;
            //Checks if name is more than 70 characters
        } else if (s.length() > 70) {
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
        currentUser.setUsername(username);
        currentUser.saveInBackground();
    }

    protected void setNewPasswordOnParse(String new_password) {
        // TODO CHECK PARSE FUNCTIONALITY
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.setPassword(new_password);
        currentUser.saveInBackground();
    }

    public boolean isAlpha(String name) {
        return name.matches("[a-zA-Z]+");
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "AccSet Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.android.tutr/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "AccSet Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.android.tutr/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane account_settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), AccSetActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane account_settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), AccSetActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane account_settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), AccSetActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
