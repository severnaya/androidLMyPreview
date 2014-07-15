package com.shurpo.myapplication.app.jobsheduler;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.LinkedList;

/**
 * Created by Maksim on 11.07.2014.
 */
public class TestJobService extends JobService {

    private static final String TAG = "SyncService";

    private JobServiceFragment jobServiceFragment;
    private LinkedList<JobParameters> jobParamsMap = new LinkedList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Service destroyed");
    }


    /**
     * When the app's MainActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCalback()"
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Messenger callback = intent.getParcelableExtra("messenger");
        Message m = Message.obtain();
        m.what = jobServiceFragment.MSG_SERVICE_OBJ;
        m.obj = this;
        try {
            callback.send(m);
        } catch (RemoteException e) {
            Log.e(TAG, "Error passing service object back to activity.");
        }
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        // We don't do any real 'work' in this sample app. All we'll
        // do is track which jobs have landed on our service, and
        // update the UI accordingly.
        jobParamsMap.add(jobParameters);
        if (jobServiceFragment != null){
            jobServiceFragment.onReceivedStartJob(jobParameters);
        }
        Log.i(TAG, "on start job: " + jobParameters.getJobId());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        jobParamsMap.remove(jobParameters);
        if(jobServiceFragment != null){
            jobServiceFragment.onReceivedStopJob();
        }
        Log.i(TAG, "on stop job: " + jobParameters.getJobId());
        return true;
    }

    public void setUiCallback(JobServiceFragment fragment){
        jobServiceFragment = fragment;
    }

    /**
     * Not currently used, but as an exercise you can hook this
     * up to a button in the UI to finish a job that has landed
     * in onStartJob().
     */
    public boolean callJobFinished() {
        JobParameters params = jobParamsMap.poll();
        if (params == null){
            return false;
        } else {
            Log.d(TAG, "job finished");
            jobFinished(params, false);
            return true;
        }
    }

    /** Send job to the JobScheduler. */
    public void scheduleJob(JobInfo t) {
        Log.d(TAG, "Scheduling job");
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(t);
    }
}
