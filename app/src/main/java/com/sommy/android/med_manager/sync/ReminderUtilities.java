package com.sommy.android.med_manager.sync;

import android.content.Context;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by somto on 4/15/18.
 */

public class ReminderUtilities {

    private static final String MEDICATION_JOB_TAG = "medication_reminder_tag";

    private static Driver driver;
    public static FirebaseJobDispatcher dispatcher;

    /**
     * Cancle dispatcher
     * @param id
     */
    public static void cancelDispatcher(String id){
        dispatcher.cancel(MEDICATION_JOB_TAG + id);
    }


    synchronized public static void scheduleMedicationReminder(@NonNull final Context context, int interval, String id) {
        /*
         * Interval at which to remind the user to take medication. Use TimeUnit for convenience, rather
         * than writing out a bunch of multiplication ourselves and risk making a silly mistake.
         */
        final int REMINDER_INTERVAL_HOURS = interval;
        final int REMINDER_INTERVAL_SECONDS = (int) (TimeUnit.HOURS.toSeconds(REMINDER_INTERVAL_HOURS));
        final int SYNC_FLEXTIME_SECONDS = REMINDER_INTERVAL_SECONDS / 2;

        driver = new GooglePlayDriver(context);
        dispatcher = new FirebaseJobDispatcher(driver);

        /* Create the Job to periodically create reminders to take medication */
        Job constraintReminderJob = dispatcher.newJobBuilder()
                /* The Service that will be used to write to preferences */
                .setService(MedicationReminderFirebaseJobService.class)
                /*
                 * Set the UNIQUE tag used to identify this Job.
                 */
                .setTag(MEDICATION_JOB_TAG + id)

                /*
                 * setLifetime sets how long this job should persist. The options are to keep the
                 * Job "forever" or to have it die the next time the device boots up.
                 */
                .setLifetime(Lifetime.FOREVER)
                /*
                 * We want these reminders to continuously happen, so we tell this Job to recur.
                 */
                .setRecurring(true)
                /*
                 * We want the reminders to happen in the interval range. The first argument for
                 * Trigger class's static executionWindow method is the start of the time frame
                 * when the
                 * job should be performed. The second argument is the latest point in time at
                 * which the data should be synced. Please note that this end time is not
                 * guaranteed, but is more of a guideline for FirebaseJobDispatcher to go off of.
                 */
                .setTrigger(Trigger.executionWindow(
                        REMINDER_INTERVAL_SECONDS,
                        REMINDER_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                /*
                 * If a Job with the tag will provided already exists, this new job will replace
                 * the old one.
                 */
                .setReplaceCurrent(true)

                /* Once the Job is ready, call the builder's build method to return the Job */
                .build();

        /* Schedule the Job with the dispatcher */
        dispatcher.schedule(constraintReminderJob);

    }
}
