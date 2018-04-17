package com.sommy.android.med_manager.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.sommy.android.med_manager.R;
import com.sommy.android.med_manager.provider.MedicationContract;

import java.text.DateFormat;
import java.util.Date;

import static com.sommy.android.med_manager.provider.MedicationContract.MedicationEntry.COLUMN_START_DATE;
import static com.sommy.android.med_manager.provider.MedicationContract.MedicationEntry.CONTENT_URI;
import static java.text.DateFormat.getDateTimeInstance;

public class MedicationDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MEDICATIONDETAILS_LOADER_ID = 101;

    private TextView mDetailTitleTextView;
    private TextView mDetailDescriptionTextView;
    private TextView mDetailIntervalTextView;
    private TextView mDetailFromTimeTextView;
    private TextView mDetailToTimeTextView;

    private String medId;
    private String[] stringsForAddMedictionActivity;

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
            medId = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);

            Bundle medIdBundle = new Bundle();
            medIdBundle.putString("medId", medId);

            getSupportLoaderManager().initLoader(MEDICATIONDETAILS_LOADER_ID, medIdBundle, this);
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
            startAddMedicationActivityIntent.putExtra(Intent.EXTRA_TEXT, true);
            startAddMedicationActivityIntent.putExtra("valuesForAddmedicationActivity", stringsForAddMedictionActivity);
            startActivity(startAddMedicationActivityIntent);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {

            case MEDICATIONDETAILS_LOADER_ID:
                Uri medication_uri;

                    medication_uri = CONTENT_URI;

                    medication_uri = medication_uri.buildUpon().appendPath(args.getString("medId")).build();

                return new CursorLoader(this, medication_uri, null, "_id=?", null, COLUMN_START_DATE+" DESC");

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();

        setValuesOnThisActivity(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * To set values on each view in MedicationDetailsActivity
     * @param data
     */
    private void setValuesOnThisActivity(Cursor data){
        // Indices for the _id, name, description, interval, start_date and end_date columns
        int idIndex = data.getColumnIndex(MedicationContract.MedicationEntry._ID);
        int nameIndex = data.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_NAME);
        int descriptionIndex = data.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_DESCRIPTION);
        int intervalIndex = data.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_INTERVAL);
        int startDateIndex = data.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_START_DATE);
        int endDateIndex = data.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_END_DATE);

        // Determine the values of the wanted data
        String nameString = data.getString(nameIndex);
        String descriptionString = data.getString(descriptionIndex);
        int intervalInt = data.getInt(intervalIndex);
        long startDateLong = data.getLong(startDateIndex);
        long endDateLong = data.getLong(endDateIndex);

//        SimpleDateFormat sDateFormat =new SimpleDateFormat("E, MMM dd yyyy HH:mm:ss");
        DateFormat sDateFormat = getDateTimeInstance();
        Date stateDate = new Date(startDateLong);
        Date endDate = new Date(endDateLong);

        //Converting to there appropriate values
        String intervalString = intervalInt +" "+this.getResources().getString(R.string.hour);
        String startDateString = sDateFormat.format(stateDate);
        String endDateString = sDateFormat.format(endDate);

        stringsForAddMedictionActivity = new String[]{medId, nameString, descriptionString, String.valueOf(intervalInt), startDateString, endDateString};

        mDetailTitleTextView.setText(nameString);
        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/Maximum.ttf");
        mDetailTitleTextView. setTypeface(font);
        mDetailDescriptionTextView.setText(descriptionString);
        mDetailIntervalTextView.setText(intervalString);
        mDetailFromTimeTextView.setText(startDateString);
        mDetailToTimeTextView.setText(endDateString);

    }
}
