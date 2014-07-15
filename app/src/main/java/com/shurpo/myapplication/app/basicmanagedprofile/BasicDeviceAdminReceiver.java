package com.shurpo.myapplication.app.basicmanagedprofile;

import android.annotation.SuppressLint;
import android.app.admin.DeviceAdminReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Maksim on 09.07.2014.
 */
public class BasicDeviceAdminReceiver extends DeviceAdminReceiver {

    /**
     * Called on the new profile when managed profile provisioning has completed. Managed profile
     * provisioning is the process of setting up the device so that it has a separate profile which
     * is managed by the mobile device management(mdm) application that triggered the provisioning.
     * Note that the managed profile is not fully visible until it is enabled.
     */

    @SuppressLint("Override")
    @Override
    public void onProfileProvisioningComplete(Context context, Intent intent) {
        Intent launch = new Intent(context, EnableProfileActivity.class);
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launch);
    }


    public static ComponentName getComponentName(Context context){
        return new ComponentName(context.getApplicationContext(), BasicDeviceAdminReceiver.class);
    }
}
