package com.shurpo.myapplication.app.basicmanagedprofile;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.shurpo.myapplication.app.R;
import com.shurpo.myapplication.app.navigationdrawer.NavigationDrawerActivity;

/**
 * Created by Maksim on 09.07.2014.
 */
public class EnableProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState){
            enableProfile();
        }
        setContentView(R.layout.activity_setup);
        findViewById(R.id.icon).setOnClickListener(onClickListener());
    }

    private void enableProfile() {
        DevicePolicyManager manager =
                (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        // We enable the profile here.
        manager.setProfileEnabled(BasicDeviceAdminReceiver.getComponentName(this));
    }

    private View.OnClickListener onClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EnableProfileActivity.this, NavigationDrawerActivity.class));
                finish();
            }
        };
    }

}
