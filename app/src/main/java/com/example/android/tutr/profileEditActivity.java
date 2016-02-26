package com.example.android.tutr;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Used to update user password and name on the Parse database.
 */
public class profileEditActivity extends AppCompatActivity {

    Button saveChangesButton;

    // UI references.
    private EditText wage;
    private EditText description;
    private TextView desc;

    // Keep track of whether registering has been cancelled
    private boolean cancel = false;
    private View focusView = null;


    /**
     * drop down menu.
     * if user selects nothing. spinner.getValue is equal to String "Select"
     * however, when user attempts to select anything only "yes" and "no" options are available
     */
    Spinner availability_spinner;
    final String[] spinner_options = new String[]{"Yes", "No", "Select"};

    RatingBar rating_bar;

    /**
     * Overidden definition of the default onCreate method.
     * Opens "edit account" window; listens to clicks on button to save changes to the user account.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_edit);

        setUpUIelements();

        saveChangesButton.setOnClickListener(
                new OnClickListener() {
                    public void onClick(View view) {
                        saveChanges();
                    }
                });
    }

    /**
     * does not display the last option when user attempts to select from the menu. last option is used as used as a hint.
     */
    class CustomArrayAdapter extends ArrayAdapter {
        /**
         * basic constructor
         * @param context
         * @param resource
         * @param objects
         */
        public CustomArrayAdapter(Context context, int resource, Object[] objects) {
            super(context, resource, objects);
        }

        @Override
        public int getCount() {
            return super.getCount()-1; // you dont display last item. It is used as hint.
        }
    }

    /**
     * initialize and link all UI elements and fields
     */
    protected void setUpUIelements() {
        // init button
        saveChangesButton = (Button) findViewById(R.id.profileEditSaveChangesButton);
        // init the wage
        wage = (EditText) findViewById(R.id.enter_hourly_rate);

       // init the description textview and editview
        desc = (TextView) findViewById(R.id.descTextView);
        description = (EditText) findViewById(R.id.enter_description);

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

               // desc.setText("Description: " + (400 - count) + "/400");

            }

            @Override
            public void afterTextChanged(Editable s) {
                // this will show characters remaining
                desc.setText("Description " + (400 - s.toString().length()) + "/400");


            }
        });

        // init rating bar
        rating_bar = (RatingBar) findViewById(R.id.ratingBar);

        // init text fields
        availability_spinner  = (Spinner) findViewById(R.id.availability_spinner);
        ArrayAdapter<String> availability_menu_adapter = new CustomArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, spinner_options);

        // link spinner and adapters
        availability_menu_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availability_spinner.setAdapter(availability_menu_adapter);
        availability_spinner.setSelection(availability_menu_adapter.getCount()); //display hint
    }

    /**
     * Acts on press of "Save Changes" button. Checks inputs and saves to Parse database if valid.
     */
    protected void saveChanges() {
        final String wageStr = wage.getText().toString();
        double wageDouble = 0;

        // Reset errors.
        wage.setError(null);
        if (!TextUtils.isEmpty(wageStr)) {
            wageDouble = Double.parseDouble(wageStr);

            // Check for a valid email address.
            if (wageDouble < 10.35) {
                wage.setError("The minimum wage is $10.35");
                focusView = wage;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            cancel = false;
            focusView.requestFocus();
        }
    }


}
