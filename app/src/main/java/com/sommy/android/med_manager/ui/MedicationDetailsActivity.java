package com.sommy.android.med_manager.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.sommy.android.med_manager.R;

public class MedicationDetailsActivity extends AppCompatActivity {

    private TextView mDetailTitleTextView;
    private TextView mDetailDescriptionTextView;
    private TextView mDetailIntervalTextView;
    private TextView mDetailFromTimeTextView;
    private TextView mDetailToTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_details);

        ActionBar actionBar= getSupportActionBar();
        // Set the action bar back button to look like an up button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDetailTitleTextView = findViewById(R.id.detailTitle_textView);
        mDetailDescriptionTextView = findViewById(R.id.detailDescription_textView);
        mDetailIntervalTextView = findViewById(R.id.detailInterval_textView);
        mDetailFromTimeTextView = findViewById(R.id.detailFromTime_textView);
        mDetailToTimeTextView = findViewById(R.id.detailToTime_textView);

        Intent intentThatStartedThisActivity = getIntent();

        if(intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)){
            String[] strings = intentThatStartedThisActivity.getStringArrayExtra(Intent.EXTRA_TEXT);
            String medTitleString = strings[0];
            String medIntervalString = strings[1];
            String medDescriptionString = strings[2];
            String medStartDateString = strings[3];
            String medEndDateString = strings[4];

            mDetailTitleTextView.setText(medTitleString);
            Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/Maximum.ttf");
            mDetailTitleTextView. setTypeface(font);
            mDetailDescriptionTextView.setText(medDescriptionString);
            mDetailIntervalTextView.setText(medIntervalString);
            mDetailFromTimeTextView.setText(medStartDateString);
            mDetailToTimeTextView.setText(medEndDateString);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem){
        int itemThatWasSelected = menuItem.getItemId();
        if(itemThatWasSelected == R.id.action_edit) {
            Intent startAddMedicationActivityIntent = new Intent(this, AddMedictionActivity.class);
            startActivity(startAddMedicationActivityIntent);
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
