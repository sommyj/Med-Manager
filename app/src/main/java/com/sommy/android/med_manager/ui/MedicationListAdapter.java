package com.sommy.android.med_manager.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sommy.android.med_manager.R;

/**
 * Created by somto on 4/9/18.
 */

public class MedicationListAdapter extends RecyclerView.Adapter<MedicationListAdapter.MedicationListAdapterViewHolder> {

    private final Context context;

    /**
     *An on-click handler
     */
    private final MedicationListOnClickHandler mClickHandler;

    private String[] a;
    private String[] b;
    private String[] c;
    private String[] d;
    private String[] e;

    public MedicationListAdapter(Context context, MedicationListOnClickHandler medicationListOnClickHandler, String[] a, String[] b, String[] c, String[] d, String[] e) {
        this.context = context;
        this.mClickHandler = medicationListOnClickHandler;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
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
        String medTitleString = a[position];
        String medIntervalString = b[position];
        String medDescriptionString = c[position];
        String medTimeLeftString = d[position];

        holder.medTitleTextView.setText(medTitleString);
        holder.medIntervalTextView.setText(medIntervalString);
        holder.medDescriptionTextView.setText(medDescriptionString);
        holder.medTimeLeftTextView.setText(medTimeLeftString);
    }

    @Override
    public int getItemCount() {
        if(null == a) {
            return 0;
        }else {
            return a.length;
        }
    }


    /**
     * The interface that receives onClick messages.
     */
    interface MedicationListOnClickHandler {
        void onClick(String[] strings);
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
            String medTitleString = a[position];
            String medIntervalString = b[position];
            String medDescriptionString = c[position];
            String medStartDateString = d[position];
            String medEndDateString = e[position];

            String[] strings = {medTitleString, medIntervalString, medDescriptionString, medStartDateString, medEndDateString};
            mClickHandler.onClick(strings);
        }
    }
}
