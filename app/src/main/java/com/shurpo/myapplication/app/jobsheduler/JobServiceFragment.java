package com.shurpo.myapplication.app.jobsheduler;

import android.app.Fragment;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.shurpo.myapplication.app.R;

/**
 * Created by Maksim on 11.07.2014.
 */
public class JobServiceFragment extends Fragment {

    public static final int MSG_UNCOLOUR_START = 0;
    public static final int MSG_UNCOLOUR_STOP = 1;
    public static final int MSG_SERVICE_OBJ = 2;

    // UI fields.
    int defaultColor;
    int startJobColor;
    int stopJobColor;

    private static int kJobId = 0;

    private TextView showStartView;
    private TextView showStopView;
    private TextView paramsTextView;
    private EditText delayEditText;
    private EditText deadlineEditText;
    private RadioButton wiFiConnectivityRadioButton;
    private RadioButton anyConnectivityRadioButton;
    private CheckBox requiresChargingCheckBox;
    private CheckBox requiresIdleCheckbox;
    private Button finishedButton;
    private Button scheduleButton;
    private Button cancelButton;

    private ComponentName serviceComponent;
    private TestJobService testJobService;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_UNCOLOUR_START:
                    showStartView.setBackgroundColor(defaultColor);
                    break;
                case MSG_UNCOLOUR_STOP:
                    showStopView.setBackgroundColor(defaultColor);
                    break;
                case MSG_SERVICE_OBJ:
                    testJobService = (TestJobService) msg.obj;
                    testJobService.setUiCallback(JobServiceFragment.this);
                    break;
            }
        }
    };

    public JobServiceFragment() {
    }

    public static JobServiceFragment newInstance(){
        return new JobServiceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.job_service_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Resources res = getResources();
        defaultColor = res.getColor(R.color.none_received);
        startJobColor = res.getColor(R.color.start_received);
        stopJobColor = res.getColor(R.color.stop_received);

        // Set up UI.

        showStartView = (TextView) view.findViewById(R.id.onstart_textview);
        showStopView = (TextView) view.findViewById(R.id.onstop_textview);
        paramsTextView = (TextView) view.findViewById(R.id.task_params);
        delayEditText = (EditText) view.findViewById(R.id.delay_time);
        deadlineEditText = (EditText) view.findViewById(R.id.deadline_time);
        wiFiConnectivityRadioButton = (RadioButton) view.findViewById(R.id.checkbox_unmetered);
        anyConnectivityRadioButton = (RadioButton) view.findViewById(R.id.checkbox_any);
        requiresChargingCheckBox = (CheckBox) view.findViewById(R.id.checkbox_charging);
        requiresIdleCheckbox = (CheckBox) view.findViewById(R.id.checkbox_idle);
        finishedButton = (Button) view.findViewById(R.id.finished_button);
        scheduleButton = (Button) view.findViewById(R.id.schedule_button);
        cancelButton = (Button) view.findViewById(R.id.cancel_button);
        finishedButton.setOnClickListener(onClickListener());
        scheduleButton.setOnClickListener(onClickListener());
        cancelButton.setOnClickListener(onClickListener());

        serviceComponent = new ComponentName(getActivity(), TestJobService.class);
        // Start service and provide it a way to communicate with us.
        Intent startServiceIntent = new Intent(getActivity(), TestJobService.class);
        startServiceIntent.putExtra("messenger", new Messenger(handler));
        getActivity().startService(startServiceIntent);
    }

    private View.OnClickListener onClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.finished_button:
                        finishJob(view);
                        break;
                    case R.id.schedule_button:
                        scheduleJob(view);
                        break;
                    case R.id.cancel_button:
                        cancelAllJobs(view);
                        break;
                }
            }
        };
    }
    /**
     * Receives callback from the service when a job has landed
     * on the app. Colours the UI and post a message to
     * uncolour it after a second.
     */
    public void onReceivedStartJob(JobParameters params) {
        showStartView.setBackgroundColor(startJobColor);
        Message m = Message.obtain(handler, MSG_UNCOLOUR_START);
        handler.sendMessageDelayed(m, 1000L); // uncolour in 1 second.
        paramsTextView.setText("Executing: " + params.getJobId() + " " + params.getExtras());
    }

    /**
     * Receives callback from the service when a job that
     * previously landed on the app must stop executing.
     * Colours the UI and post a message to uncolour it after a
     * second.
     */
    public void onReceivedStopJob() {
        showStopView.setBackgroundColor(stopJobColor);
        Message m = Message.obtain(handler, MSG_UNCOLOUR_STOP);
        handler.sendMessageDelayed(m, 1000L); // uncolour in 2 second.
        paramsTextView.setText("");
    }

    private boolean ensureTestService() {
        if (testJobService == null) {
            Toast.makeText(getActivity(), "Service null, never got callback?",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void finishJob(View v) {
        if(!ensureTestService()){
            return;
        }
        testJobService.callJobFinished();
        paramsTextView.setText("");
    }

    public void cancelAllJobs(View v) {
        JobScheduler tm = (JobScheduler) getActivity().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.cancelAll();
    }

    public void scheduleJob(View v) {
        if(!ensureTestService()){
            return;
        }

        JobInfo.Builder builder = new JobInfo.Builder(kJobId++, serviceComponent);

        String delay = delayEditText.getText().toString();
        if(delay != null && !TextUtils.isEmpty(delay)){
            builder.setMinimumLatency(Long.valueOf(delay));
        }

        String deadline = deadlineEditText.getText().toString();
        if(deadline !=  null && !TextUtils.isEmpty(deadline)){
            builder.setOverrideDeadline(Long.valueOf(deadline));
        }

        boolean requiresUnmetered = wiFiConnectivityRadioButton.isChecked();
        boolean requiresAnyConnectivity = anyConnectivityRadioButton.isAccessibilityFocused();

        if(requiresUnmetered){
            builder.setRequiredNetworkCapabilities(JobInfo.NetworkType.UNMETERED);
        }else if (requiresAnyConnectivity){
            builder.setRequiredNetworkCapabilities(JobInfo.NetworkType.ANY);
        }
        builder.setRequiresDeviceIdle(requiresIdleCheckbox.isChecked());
        builder.setRequiresCharging(requiresChargingCheckBox.isChecked());
        testJobService.scheduleJob(builder.build());
    }
}
