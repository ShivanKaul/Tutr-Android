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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_settings);
        setupActionBar();

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
        EditText oldPasswordText = (EditText)findViewById(R.id.enterOldPassword);
        String oldPasswordString = oldPasswordText.getText().toString();

        EditText newPasswordText = (EditText)findViewById(R.id.enterNewPassword);
        String newPasswordString = newPasswordText.getText().toString();

        EditText confirmPasswordText = (EditText)findViewById(R.id.confirmNewPassword);
        String confirmPasswordString = confirmPasswordText.getText().toString();

        EditText newNameText = (EditText)findViewById(R.id.enterNewName);
        String newNameString = newNameText.getText().toString();
        // all user inputs acquired

        // Check for a validity of input
        if (TextUtils.isEmpty(oldPasswordString)) {
            oldPasswordText.setError(getString(R.string.error_field_required));
            focusView = oldPasswordText;
            cancel = true;
        }
        else if (TextUtils.isEmpty(newPasswordString)) {
            newPasswordText.setError(getString(R.string.error_field_required));
            focusView = newPasswordText;
            cancel = true;
        }
        else if (TextUtils.isEmpty(confirmPasswordString)) {
            confirmPasswordText.setError(getString(R.string.error_field_required));
            focusView = confirmPasswordText;
            cancel = true;
        }
        else if (checkEqualsPasswords(newPasswordString,oldPasswordString))
            validatePassword(newPasswordString, newPasswordText);

        if (cancel) {
            // There was an error; don't attempt saving password
            // form field with an error.
            focusView.requestFocus();
        } else
            setNewPasswordOnParse(newPasswordString);

        cancel = false;
        if (TextUtils.isEmpty(newNameString)) {
            newNameText.setError(getString(R.string.error_field_required));
            focusView = newNameText;
            cancel = true;
        }
        else if (checkValidName(newNameString))
            setNewNameOnParse(newNameString);

        if (cancel) {
            // There was an error; don't attempt saving password
            // form field with an error.
            focusView.requestFocus();
        } else
            setNewNameOnParse(newPasswordString);
    }

    protected boolean checkEqualsPasswords(String s1, String s2) {
        return (s1.equals(s2) && s1.length() != 0 && s2.length() != 0);
    }

    private void validatePassword(String password, EditText newPasswordText) {
        boolean hasCapLetter = !password.equals(password.toLowerCase());
        boolean haslowerCaseLetter = !password.equals(password.toUpperCase());

        // handle length of password
        if (password.length() < 8 || password.length() > 16) {
            newPasswordText.setError(getString(R.string.error_length_password));
            focusView = newPasswordText;
            cancel = true;
        } else if (!hasCapLetter) {
            newPasswordText.setError(getString(R.string.error_CapCase));
            focusView = newPasswordText;
            cancel = true;
        } else if (!haslowerCaseLetter) {
            newPasswordText.setError(getString(R.string.error_lowerCase));
            focusView = newPasswordText;
            cancel = true;
        }
    }

    //Checks if name only contains letters
    public boolean isAlpha(String name) {
        return name.matches("[a-zA-Z]+");
    }

    //Checks if name is valid
    public void checkValidName(String s, EditText newNameText) {
        boolean onlyLetters = isAlpha(s);

        //Checks if nothing has been enetered in name field
        if (s.length() < 1) {
            newNameText.setError(getString(R.string.error_invalid_name_1));
            focusView = newNameText;
            cancel = true;
        //Checks if name is more than 70 characters
        } else if (s.length() > 70) {
            newNameText.setError(getString(R.string.error_invalid_name_2));
            focusView = newNameText;
            cancel = true;
        //Checks if name only contains letters
        } else if (!onlyLetters){
            newNameText.setError(getString(R.string.error_invalid_name_3));
            focusView = newNameText;
            cancel = true;
        }
    }

    protected void setNewNameOnParse(String s) {
        // TODO
    }

    protected void setNewPasswordOnParse(String new_password) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.setPassword(new_password);
        currentUser.saveInBackground();
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
