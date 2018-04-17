package com.sommy.android.med_manager.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.sommy.android.med_manager.utilities.NotificationUtils;

/**
 * Created by somto on 4/15/18.
 */

public class MedicationReminderFirebaseJobService extends JobService {

    private AsyncTask mBackgroundTask;

    /**
     * The entry point to your Job. Implementations should offload work to another thread of
     * execution as soon as possible.
     *
     * This is called by the Job Dispatcher to tell us we should start our job. Keep in mind this
     * method is run on the application's main thread, so we need to offload work to a background
     * thread.
     *
     * @return whether there is more work remaining.
     */

    @Override
    public boolean onStartJob(final JobParameters job) {
        mBackgroundTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                Context context = MedicationReminderFirebaseJobService.this;
                NotificationUtils.remindUserToTakeMedication(context);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                /*
                 * Once the AsyncTask is finished, the job is finished. To inform JobManager that
                 * you're done, you call jobFinished with the jobParamters that were passed to your
                 * job and a boolean representing whether the job needs to be rescheduled. This is
                 * usually if something didn't work and you want the job to try running again.
                 */

                jobFinished(job, false);
            }
        };

        mBackgroundTask.execute();
        return true;
    }

    /**
     * Called when the scheduling engine has decided to interrupt the execution of a running job,
     * most likely because the runtime constraints associated with the job are no longer satisfied.
     *
     * @return whether the job should be retried
     * @see Job.Builder#setRetryStrategy(RetryStrategy)
     * @see RetryStrategy
     */
    @Override
    public boolean onStopJob(JobParameters job) {
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }
}
