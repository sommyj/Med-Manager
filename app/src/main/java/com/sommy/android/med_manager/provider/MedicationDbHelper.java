package com.sommy.android.med_manager.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by somto on 4/10/18.
 */

public class MedicationDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "medManager.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    public MedicationDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold the medications data
        final String SQL_CREATE_MEDICATIONS_TABLE = "CREATE TABLE " + MedicationContract.MedicationEntry.TABLE_NAME + " (" +
                MedicationContract.MedicationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MedicationContract.MedicationEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                MedicationContract.MedicationEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                MedicationContract.MedicationEntry.COLUMN_INTERVAL + " INTEGER NOT NULL, " +
                MedicationContract.MedicationEntry.COLUMN_START_DATE + " TIMESTAMP NOT NULL, " +
                MedicationContract.MedicationEntry.COLUMN_END_DATE + " TIMESTAMP NOT NULL)";

        sqLiteDatabase.execSQL(SQL_CREATE_MEDICATIONS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MedicationContract.MedicationEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
