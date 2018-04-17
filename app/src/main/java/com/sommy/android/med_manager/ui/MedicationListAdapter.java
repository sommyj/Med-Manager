package com.sommy.android.med_manager.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sommy.android.med_manager.R;
import com.sommy.android.med_manager.provider.MedicationContract;

import java.text.DateFormat;
import java.util.Date;

import static java.text.DateFormat.getDateInstance;

/**
 * Created by somto on 4/9/18.
 */

public class MedicationListAdapter extends RecyclerView.Adapter<MedicationListAdapter.MedicationListAdapterViewHolder> {

    private final Context context;
    private Cursor mCursor;

    /**
     * An on-click handler
     */
    private final MedicationListOnClickHandler mClickHandler;

//    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("dd MMM");
    private static DateFormat sDateFormat = getDateInstance();


    private static final long MINUTE_MILLIS = 1000 * 60;
    private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final long WEEK_MILLIS = 7 * DAY_MILLIS;

    public MedicationListAdapter(Context context, MedicationListOnClickHandler medicationListOnClickHandler) {
        this.context = context;
        this.mClickHandler = medicationListOnClickHandler;
    }

    @Override
    public MedicationListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutForListItem = R.layout.medication_layout;
        boolean shouldAttachToparentImmediately = false;

        View view = inflater.inflate(layoutForListItem, parent, shouldAttachToparentImmediately);
        return new MedicationListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MedicationListAdapterViewHolder holder, int position) {

        // Indices for the _id, name, description, interval, start_date and end_date columns
        int idIndex = mCursor.getColumnIndex(MedicationContract.MedicationEntry._ID);
        int nameIndex = mCursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_NAME);
        int descriptionIndex = mCursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_DESCRIPTION);
        int intervalIndex = mCursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_INTERVAL);
        int startDateIndex = mCursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_START_DATE);
        int endDateIndex = mCursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_END_DATE);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the wanted data
        final int id = mCursor.getInt(idIndex);
        String name = mCursor.getString(nameIndex);
        String description = mCursor.getString(descriptionIndex);
        int interval = mCursor.getInt(intervalIndex);
        long startDate = mCursor.getLong(startDateIndex);
        long endDate = mCursor.getLong(endDateIndex);

        String date = "";
        long now = System.currentTimeMillis();

        // Change how the date is displayed depending on whether it was written in the last minute,
        // the hour, etc.
        if (endDate > now) {
            if (endDate - now < (WEEK_MILLIS)) {
                if (endDate - now < (DAY_MILLIS)) {
                    if (endDate - now < (HOUR_MILLIS)) {
                        long minutes = Math.round((endDate - now) / MINUTE_MILLIS);
                        date = String.valueOf(minutes) + " " + context.getResources().getString(R.string.minute);
                    } else {
                        long minutes = Math.round((endDate - now) / HOUR_MILLIS);
                        date = String.valueOf(minutes) + " " + context.getResources().getString(R.string.hour);
                    }
                } else {
                    long minutes = Math.round((endDate - now) / DAY_MILLIS);
                    date = String.valueOf(minutes) + " " + context.getResources().getString(R.string.day);
                }
            } else {
                Date dateDate = new Date(endDate);
                date = sDateFormat.format(dateDate);
            }
        } else {
            date = "ended";
        }

        String intervalString = String.valueOf(interval) + " " + context.getResources().getString(R.string.hour);

        //Set values
        holder.itemView.setTag(id);
        holder.medTitleTextView.setText(name);
        holder.medDescriptionTextView.setText(description);
        holder.medIntervalTextView.setText(intervalString);
        holder.medTimeLeftTextView.setText(date);

    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged(); // Force the RecyclerView to refresh
        }
        return temp;
    }

    /**
     * The interface that receives onClick messages.
     */
    interface MedicationListOnClickHandler {
        void onClick(String string);
    }

    /**
     * Cache of the children views for a medication list item.
     */
    class MedicationListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView medTitleTextView;
        private final TextView medIntervalTextView;
        private final TextView medDescriptionTextView;
        private final TextView medTimeLeftTextView;

        public MedicationListAdapterViewHolder(View itemView) {
            super(itemView);

            medTitleTextView = itemView.findViewById(R.id.medTitle_textView);
            medIntervalTextView = itemView.findViewById(R.id.medInterval_textView);
            medDescriptionTextView = itemView.findViewById(R.id.medDescription_textView);
            medTimeLeftTextView = itemView.findViewById(R.id.medTimeLeft_textView);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            mCursor.moveToPosition(position); // get to the right location in the cursor

//            // Determine the values of the wanted data
            int idInt = mCursor.getInt(mCursor.getColumnIndex(MedicationContract.MedicationEntry._ID));
            String idString = String.valueOf(idInt);

            mClickHandler.onClick(idString);
        }
    }
}
