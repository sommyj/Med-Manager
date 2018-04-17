package com.sommy.android.med_manager.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.sommy.android.med_manager.R;
import com.sommy.android.med_manager.utilities.PreferenceUtilities;

public class UpdateProfileActivity extends AppCompatActivity {

    private TextView mProfileFirstName;
    private TextView mProfileLastName;
    private TextView mProfileUserName;
    private TextView mProfileEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ActionBar actionBar = getSupportActionBar();
        // Set the action bar back button to look like an up button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mProfileFirstName = findViewById(R.id.profileFirstName);
        mProfileLastName = findViewById(R.id.profileLastName);
        mProfileUserName = findViewById(R.id.profileUserName);
        mProfileEmail = findViewById(R.id.profileEmail);

        String firstName = PreferenceUtilities.getFirstName(this);
        String lastName = PreferenceUtilities.getLastName(this);
        String userName = PreferenceUtilities.getDisplayName(this);
        String email = PreferenceUtilities.getEmailAddress(this);

        mProfileFirstName.setText(firstName);
        mProfileLastName.setText(lastName);
        mProfileUserName.setText(userName);
        mProfileEmail.setText(email);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemThatWasSelected = menuItem.getItemId();
        if (itemThatWasSelected == R.id.action_save) {

            PreferenceUtilities.setFirstName(this, mProfileFirstName.getText().toString());
            PreferenceUtilities.setLastName(this, mProfileLastName.getText().toString());
            PreferenceUtilities.setDisplayName(this, mProfileUserName.getText().toString());
            PreferenceUtilities.setEmailAddress(this, mProfileEmail.getText().toString());

            Toast.makeText(getBaseContext(), getResources().getString(R.string.update_success), Toast.LENGTH_LONG).show();

            // Finish activity (this returns back to MainActivity)
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
