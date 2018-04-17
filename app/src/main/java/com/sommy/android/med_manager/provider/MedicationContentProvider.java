package com.sommy.android.med_manager.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.sommy.android.med_manager.provider.MedicationContract.MedicationEntry.TABLE_NAME;

/**
 * Created by somto on 4/10/18.
 */

public class MedicationContentProvider extends ContentProvider {

    // Define final integer constants for the directory of medications and a single item.
    // It's convention to use 100, 200, 300, etc for directories,
    // and related ints (101, 102, ..) for items in that directory.
    public static final int MEDICATIONS = 100;
    public static final int MEDICATION_WITH_ID = 101;

    // Declare a static variable for the Uri matcher that you construct
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String TAG = MedicationContentProvider.class.getName();

    // Define a static buildUriMatcher method that associates URI's with their int match
    public static UriMatcher buildUriMatcher() {
        // Initialize a UriMatcher
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Add URI matches
        uriMatcher.addURI(MedicationContract.AUTHORITY, MedicationContract.PATH_MEDICATIONS, MEDICATIONS);
        uriMatcher.addURI(MedicationContract.AUTHORITY, MedicationContract.PATH_MEDICATIONS + "/#", MEDICATION_WITH_ID);
        return uriMatcher;
    }

    // Member variable for a medicationDbHelper that's initialized in the onCreate() method
    private MedicationDbHelper mMedicationDbHelper;

    // Complete onCreate() and initialize a MedicationDbhelper on startup
    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMedicationDbHelper = new MedicationDbHelper(context);
        return true;
    }

    /**
     *Handles requests for data by URI
     *
     * @param uri
     * @param selection
     * @param projection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = mMedicationDbHelper.getReadableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        // Query for the medications directory and write a default case
        switch (match) {
            // Query for the medications directory
            case MEDICATIONS:
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MEDICATION_WITH_ID:
                String value = uri.getPathSegments().get(1);
                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        new String[]{value},
                        null,
                        null,
                        sortOrder);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Handles requests to insert a single new row of data
     *
     * @param uri
     * @param contentValues
     * @return
     */
    @NonNull
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        // Get access to the medication database (to write new data to)
        final SQLiteDatabase db = mMedicationDbHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the medications directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case MEDICATIONS:
                // Insert new values into the database
                // Inserting values into medications table
                long id = db.insert(TABLE_NAME, null, contentValues);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(MedicationContract.MedicationEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;

    }

    /**
     * Implement delete to delete a single row of data
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = mMedicationDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted medications
        int medicationsDeleted; // starts as 0

        // Delete a single row of data
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case MEDICATION_WITH_ID:
                // Get the medication ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                medicationsDeleted = db.delete(TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (medicationsDeleted != 0) {
            // A medication was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of medications deleted
        return medicationsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = mMedicationDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted medications
        int medicationsUpdated; // starts as 0

        // Update a single row of data
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case MEDICATION_WITH_ID:
                // Get the medication ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                medicationsUpdated = db.update(TABLE_NAME, contentValues, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items updated
        if (medicationsUpdated != 0) {
            // A medication was updated, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of medications Updated
        return medicationsUpdated;
    }
}
