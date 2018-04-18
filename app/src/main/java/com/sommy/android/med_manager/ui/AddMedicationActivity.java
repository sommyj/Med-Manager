package com.sommy.android.med_manager.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sommy.android.med_manager.R;
import com.sommy.android.med_manager.provider.MedicationContract;
import com.sommy.android.med_manager.sync.ReminderUtilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static java.text.DateFormat.getDateTimeInstance;

public class AddMedicationActivity extends AppCompatActivity {
    private EditText mNameEditText;
    private EditText mDescriptionEditText;
    private EditText mIntervalEditText;
    private EditText mStartDateEditText;
    private EditText mEndDateEditText;

    private String[] valuesFromMedicationDetailsActivity;
    private Calendar startDate;
    private Calendar endDate;
    private Context context;
    private String updateId;

    private DateFormat sDateFormat = getDateTimeInstance();

    private static final String TAG = AddMedicationActivity.class.getSimpleName();
    private static final int ADD_MEDICATION_DETAILS_LOADER_ID = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mediction);

        ActionBar actionBar = getSupportActionBar();
        // Set the action bar back button to look like an up button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mNameEditText = findViewById(R.id.name_EditText);
        mDescriptionEditText = findViewById(R.id.description_EditText);
        mIntervalEditText = findViewById(R.id.interval_EditText);
        mStartDateEditText = findViewById(R.id.startDate_EditText);
        mEndDateEditText = findViewById(R.id.endDate_EditText);

        context = this;


        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();

        //To hide input state eg. keyboard on creation
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setFocusOnStartDateEditText(mStartDateEditText);

        setFocusOnEndDateEditText(mEndDateEditText);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity.hasExtra("valuesForAddmedicationActivity")) {
            setTitle(R.string.editMedication_label);
            valuesFromMedicationDetailsActivity = intentThatStartedThisActivity.getStringArrayExtra("valuesForAddmedicationActivity");

            updateId = valuesFromMedicationDetailsActivity[0];
            //set values on view
            mNameEditText.setText(valuesFromMedicationDetailsActivity[1]);
            mDescriptionEditText.setText(valuesFromMedicationDetailsActivity[2]);
            mIntervalEditText.setText(valuesFromMedicationDetailsActivity[3]);
            mStartDateEditText.setText(valuesFromMedicationDetailsActivity[4]);
            mEndDateEditText.setText(valuesFromMedicationDetailsActivity[5]);
        }

    }

    private void setFocusOnStartDateEditText(EditText startDateEditText) {
        startDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (hasFocus) {
                    final Calendar currentDate = Calendar.getInstance();
                    new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            startDate.set(year, monthOfYear, dayOfMonth);
                            new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    startDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    startDate.set(Calendar.MINUTE, minute);
                                    Log.v(TAG, "The choosen one " + startDate.getTime());

                                    String startDateString = sDateFormat.format(startDate.getTimeInMillis());
                                    mStartDateEditText.setText(startDateString);
                                }
                            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
                        }
                    }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
                }
            }
        });

    }

    private void setFocusOnEndDateEditText(EditText endDateEditText) {
        mStartDateEditText.clearFocus();
        endDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (hasFocus) {
                    final Calendar currentDate = Calendar.getInstance();
                    new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            endDate.set(year, monthOfYear, dayOfMonth);
                            new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    endDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    endDate.set(Calendar.MINUTE, minute);
                                    Log.v(TAG, "The choosen one " + endDate.getTime());

                                    String endDateString = sDateFormat.format(endDate.getTimeInMillis());
                                    mEndDateEditText.setText(endDateString);
                                }
                            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
                        }
                    }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
                }
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemThatWasSelected = menuItem.getItemId();
        if (itemThatWasSelected == R.id.action_save) {
            // If the EditText input is empty -> don't create an entry
            String nameString = mNameEditText.getText().toString();
            String descriptionString = mDescriptionEditText.getText().toString();
            String intervalString = mIntervalEditText.getText().toString();
            String startDateString = mStartDateEditText.getText().toString();
            String endDateString = mEndDateEditText.getText().toString();

            if (nameString.length() == 0 || descriptionString.length() == 0 || intervalString.length() == 0
                    || startDateString.length() == 0 || endDateString.length() == 0) {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.addMediction_validation), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return false;
            }

            Date startDate = null;
            Date endDate = null;
            try {
                startDate = sDateFormat.parse(startDateString);
                endDate = sDateFormat.parse(endDateString);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), getResources().getString(R.string.date_error), Toast.LENGTH_LONG).show();
                return false;
            }

            //Converting to there appropriate values for db insertion
            int intervalInt = Integer.parseInt(intervalString);
            long startDateLong = startDate.getTime();
            long endDateLong = endDate.getTime();
            // Insert new task data via a ContentResolver
            // Create new empty ContentValues object
            ContentValues contentValues = new ContentValues();
            // Put the medication values into the ContentValues
            contentValues.put(MedicationContract.MedicationEntry.COLUMN_NAME, nameString);
            contentValues.put(MedicationContract.MedicationEntry.COLUMN_DESCRIPTION, descriptionString);
            contentValues.put(MedicationContract.MedicationEntry.COLUMN_INTERVAL, intervalInt);
            contentValues.put(MedicationContract.MedicationEntry.COLUMN_START_DATE, startDateLong);
            contentValues.put(MedicationContract.MedicationEntry.COLUMN_END_DATE, endDateLong);

            if (valuesFromMedicationDetailsActivity == null) {
                // Insert the content values via a ContentResolver
                Uri addUri = getContentResolver().insert(MedicationContract.MedicationEntry.CONTENT_URI, contentValues);

                // Display the status that's returned with a Toast
                if (addUri != null) {
                    Log.v(TAG, addUri.toString());
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.insert_success), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.insert_error), Toast.LENGTH_LONG).show();
                    return false;
                }

                String addId = addUri.getPathSegments().get(1);

                Log.d(TAG, addId + "++++++++++");
                ReminderUtilities.scheduleMedicationReminder(this, intervalInt, addId);
            } else {
                Uri updateUri = MedicationContract.MedicationEntry.CONTENT_URI;
                updateUri = updateUri.buildUpon().appendPath(valuesFromMedicationDetailsActivity[0]).build();

                int updatedCount = getContentResolver().update(updateUri, contentValues, null, null);
                if (updatedCount == 1) {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.update_success), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.update_error), Toast.LENGTH_LONG).show();
                    return false;
                }
                Log.d(TAG, "++++++++++" + updateId + "++++++++++");
                ReminderUtilities.scheduleMedicationReminder(this, intervalInt, updateId);
            }


            // Finish activity (this returns back to MainActivity)
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
