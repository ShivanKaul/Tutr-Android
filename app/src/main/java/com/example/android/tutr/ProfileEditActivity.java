package com.example.android.tutr;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Used to update user password and name on the Parse database.
 */
public class ProfileEditActivity extends AppCompatActivity implements View.OnClickListener {
    ParseUser currentUser = ParseUser.getCurrentUser();
    Button saveChangesButton;
    // UI references.
    private EditText wage;
    private EditText description;
    private EditText phone;
    private EditText subjects;
    private TextView desc;
    private TextView rating_title;
    private TextView rating_count;
    // Keep track of whether registering has been cancelled
    private boolean cancel = false;
    private View focusView = null;

    private ImageView pro_pic;
    Button upload_image;
    private static final int RESULT_LOAD_IMAGE = 1;
    /**
     * drop down menu.
     * if user selects nothing. spinner.getValue is equal to String "Select"
     * however, when user attempts to select anything only "yes" and "no" options are available
     */
    Spinner availability_spinner;
    final String[] spinner_options = new String[]{"Yes", "No", "Select"};

    RatingBar rating_bar;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * Overidden definition of the default onCreate method.
     * Opens "edit account" window; listens to clicks on button to save changes to the user account.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_edit);
        if (currentUser == null) {
            return;
        }
        setUpUIelements();

        pro_pic.setOnClickListener(this);
        upload_image.setOnClickListener(this);

        saveChangesButton.setOnClickListener(
                new OnClickListener() {
                    public void onClick(View view) {
                        try {
                            saveChanges();
                        } catch (Exception e) {
                            startActivity(new Intent(ProfileEditActivity.this, LoginActivity.class));
                        }
                    }
                });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        loadProfilePicFromStorage();
    }

    private void loadProfilePicFromStorage() {
        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File f = new File(directory.getAbsolutePath(), "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            pro_pic.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    //Select and upload image from gallery
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pro_pic:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                break;
            case R.id.upload_image:
                //Parse Code to actually save image to database
                if (pro_pic.getDrawable() != null) {
                    Bitmap bitmap = ((BitmapDrawable) pro_pic.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
                    byte[] image = stream.toByteArray();
                    ParseFile file = new ParseFile(currentUser.getObjectId() + currentUser.getUsername() + "PROFILE.jpeg", image);
                    file.saveInBackground();
                    currentUser.put("profilePicture", file);
                    currentUser.saveInBackground();
                    Toast.makeText(ProfileEditActivity.this, "Profile Picture uploaded", Toast.LENGTH_LONG).show();
                    try {
                        saveToInternalStorage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ProfileEditActivity.this, "No Profile Picture to upload", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void saveToInternalStorage(Bitmap bitmap) throws IOException {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            pro_pic.setImageURI(selectedImage);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ProfileEdit Page", // TODO: Define a title for the content shown.
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
                "ProfileEdit Page", // TODO: Define a title for the content shown.
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
     * does not display the last option when user attempts to select from the menu. last option is used as used as a hint.
     */
    class CustomArrayAdapter extends ArrayAdapter {
        /**
         * basic constructor
         *
         * @param context
         * @param resource
         * @param objects
         */
        public CustomArrayAdapter(Context context, int resource, Object[] objects) {
            super(context, resource, objects);
        }

        @Override
        public int getCount() {
            return super.getCount() - 1; // you don't display last item. It is used as hint.
        }
    }

    /**
     * initialize and link all UI elements and fields
     */
    protected void setUpUIelements() {
        try {
            currentUser.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(ProfileEditActivity.this, "Unable to access Parse Server", Toast.LENGTH_LONG).show();
        }
        saveChangesButton = (Button) findViewById(R.id.profileEditSaveChangesButton);
        wage = (EditText) findViewById(R.id.enter_hourly_rate);
        phone = (EditText) findViewById(R.id.enter_phone);
        subjects = (EditText) findViewById(R.id.enter_subjects);
        desc = (TextView) findViewById(R.id.descTextView);
        description = (EditText) findViewById(R.id.enter_description);

        pro_pic = (ImageView) findViewById(R.id.pro_pic);
        upload_image = (Button) findViewById(R.id.upload_image);


        if (currentUser.getList("courses") != null) {
            List<String> courses = currentUser.getList("courses");
            StringBuilder stringBuilder = new StringBuilder();
            for (String course : courses) {
                stringBuilder.append(",").append(course);
            }
            //remove the starting ','
            stringBuilder.deleteCharAt(0);
            subjects.setText(stringBuilder.toString());
        }
        if (currentUser.getString("phone") != null) {
            phone.setText(currentUser.getString("phone"));
        }
        wage.setText(String.valueOf(currentUser.getDouble("hourlyRate")));
        if (wage.getText().toString().isEmpty())
            wage.setText(0 + "." + 0);
        if (currentUser.getString("description") != null) {
            description.setText(currentUser.getString("description"));
        }
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // this will show characters remaining
                desc.setText("Description " + (400 - s.toString().length()) + "/400");
            }
        });
        // init rating bar
        rating_bar = (RatingBar) findViewById(R.id.ratingBar);

        // Get rating from rating table
        double rating = 0;
        int rateCount = 0;
        ParseObject userRating;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Ratings");
        query.whereEqualTo("username", currentUser.getUsername());
        try {
            Log.w("username is ", currentUser.getUsername());
            userRating = query.find().get(0);
            rating = userRating.getDouble("rating");
            rateCount = userRating.getInt("ratingCount");
        } catch (Exception p) {
            Toast.makeText(ProfileEditActivity.this, "Problem fetching data from Ratings table" + p, Toast.LENGTH_LONG).show();
        }

        Log.w("rating", String.valueOf(rating));
        rating_title = (TextView) findViewById(R.id.rating_title);
        rating_count = (TextView) findViewById(R.id.rate_count);
        rating_title.setText("Rating (" + new DecimalFormat("##.#").format(rating) + " / 5.0)");
        rating_count.setText("(" + rateCount + ")");
        rating_bar.setRating((float) rating);
        rating_bar.setIsIndicator(true);
        // init text fields
        availability_spinner = (Spinner) findViewById(R.id.availability_spinner);
        ArrayAdapter<String> availability_menu_adapter = new CustomArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spinner_options);
        // link spinner and adapters
        availability_menu_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availability_spinner.setAdapter(availability_menu_adapter);
        availability_spinner.setSelection(availability_menu_adapter.getPosition("No"));

        if (currentUser.getString("available") != null &&
                currentUser.getString("available").equalsIgnoreCase("Yes")) {
            availability_spinner.setSelection(availability_menu_adapter.getPosition("Yes"));
        }

    }

    /**
     * Acts on press of "Save Changes" button. Checks inputs and saves to Parse database if valid.
     */
    protected void saveChanges() {
        final String wageStr = wage.getText().toString();
        double wageDouble = 0;
        String[] courses = subjects.getText().toString().toLowerCase().replaceAll("\\s+", "").split(",");
        subjects.setError(null);
        for (String c : courses) {
            if (!CourseValidator.isValidCourse(c) && !c.isEmpty()) {
                subjects.setError("At least one of the subjects is not valid ");
                cancel = true;
                focusView = subjects;
            }
        }
        // Reset errors.
        wage.setError(null);
        if (!TextUtils.isEmpty(wageStr)) {
            wageDouble = Double.parseDouble(wageStr);

            // Check for the wage.
            if (wageDouble < 10.35 && wageDouble != 0) {
                wage.setError("The minimum wage is $10.35");
                focusView = wage;
                cancel = true;
            }
        }
        if (availability_spinner.getSelectedItem().toString().equalsIgnoreCase("select")) {
            TextView errorText = (TextView) availability_spinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Please select your availability!");
            focusView = availability_spinner;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            cancel = false;
            Toast.makeText(ProfileEditActivity.this, "Please verify your input values again", Toast.LENGTH_LONG).show();
            focusView.requestFocus();
            return;
        } else {
            currentUser.put("courses", Arrays.asList(courses));
            currentUser.put("description", description.getText().toString());
            currentUser.put("hourlyRate", wageDouble);
            currentUser.put("phone", phone.getText().toString());
            currentUser.put("available", availability_spinner.getSelectedItem().toString().toLowerCase());
            Toast.makeText(ProfileEditActivity.this, "Changed profile successfully", Toast.LENGTH_LONG).show();
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    finish();
                }
            });
        }
    }
}
