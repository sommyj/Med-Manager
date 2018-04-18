package com.sommy.android.med_manager.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sommy.android.med_manager.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private List<Integer> id = new ArrayList<>();
    private List<String> name = new ArrayList<>();
    private List<String> description = new ArrayList<>();
    private List<Integer> interval = new ArrayList<>();
    private List<Long> startDate = new ArrayList<>();
    private List<Long> endDate = new ArrayList<>();


    /**
     * Creates a MedicationListAdapter.
     *
     * @param context
     * @param medicationListOnClickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public MedicationListAdapter(Context context, MedicationListOnClickHandler medicationListOnClickHandler) {
        this.context = context;
        this.mClickHandler = medicationListOnClickHandler;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     *@param parent The ViewGroup that these ViewHolders are contained within.
     * @param viewType The ViewType integer is used to provide a different layout,
     *                 if the RecyclerView has more than one type of item (which ours does).
     * @return A new UserListAdapterViewHolder that holds the View for each list item
     */
    @Override
    public MedicationListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutForListItem = R.layout.medication_layout;
        boolean shouldAttachToparentImmediately = false;

        View view = inflater.inflate(layoutForListItem, parent, shouldAttachToparentImmediately);
        return new MedicationListAdapterViewHolder(view);
    }


    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the Medication
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MedicationListAdapterViewHolder holder, int position) {

        final int id = this.id.get(position);
        String name = this.name.get(position);
        String description = this.description.get(position);;
        int interval = this.interval.get(position);
        long startDate =this.startDate.get(position);
        long endDate =this.endDate.get(position);

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
        if (id == null) return 0;
        return id.size();
    }

    /**
     * This method is used to set the medication details on a MedicationListAdapter if we've already
     * created one. This is handy when we get new data from the database but don't want to create a
     * new MedicationListAdapter to display it.
     *
     * @param id
     * @param name
     * @param description
     * @param interval
     * @param startDate
     * @param endDate
     */
    void setUpMedicationData(List<Integer> id, List<String> name, List<String> description, List<Integer> interval, List<Long> startDate, List<Long> endDate){

        this.id = id;
        this.name = name;
        this.description = description;
        this.interval = interval;
        this.startDate = startDate;
        this.endDate = endDate;
        notifyDataSetChanged();
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

        /**
         * passing the id when the recycler view is clicked
         * @param view
         */
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            int idInt = id.get(position);
            String idString = String.valueOf(idInt);

            mClickHandler.onClick(idString);
        }
    }
}
