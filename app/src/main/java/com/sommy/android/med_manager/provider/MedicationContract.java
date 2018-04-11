package com.sommy.android.med_manager.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by somto on 4/10/18.
 */

public class MedicationContract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.sommy.android.med_manager";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "medications" directory
    public static final String PATH_MEDICATIONS = "medications";

    /* MedicationEntry is an inner class that defines the contents of the medication table */
    public static final class MedicationEntry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MEDICATIONS).build();

        public static final String TABLE_NAME = "medications";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_INTERVAL = "interval";
        public static final String COLUMN_START_DATE = "StartDate";
        public static final String COLUMN_END_DATE = "EndDate";

    }
}
