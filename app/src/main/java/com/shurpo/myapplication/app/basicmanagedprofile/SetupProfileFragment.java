package com.shurpo.myapplication.app.basicmanagedprofile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.shurpo.myapplication.app.R;

/**
 * Created by Maksim on 09.07.2014.
 */
public class SetupProfileFragment extends Fragment {

    public SetupProfileFragment() {
    }

    public static SetupProfileFragment newInstance(){
        return new SetupProfileFragment();
    }


    @SuppressLint("Override")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup_profile, container, false);
    }

    @SuppressLint("Override")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.set_up_profile).setOnClickListener(onClickListener());
    }

    private View.OnClickListener onClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.set_up_profile:
                        provisionManagerProfile();
                        break;
                }
            }
        };
    }

    /**
     * Initiates the managed profile provisioning. If we already have a managed profile set up on
     * this device, we will get an error dialog in the following provisioning phase.
     */
    private void provisionManagerProfile(){
        Activity activity = getActivity();
        if (activity == null) return;

        Intent intent = new Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE);
        intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME, activity.getApplicationContext().getPackageName());
        intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_DEFAULT_MANAGED_PROFILE_NAME, "Sample Managed Profile");
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, BasicDeviceAdminReceiver.getComponentName(activity));
        if(intent.resolveActivity(activity.getPackageManager()) != null){
            startActivity(intent);
            activity.finish();
        }else {
            Toast.makeText(activity, "Device provisioning is not enabled. Stopping.",
                    Toast.LENGTH_SHORT).show();
        }

    }
}
