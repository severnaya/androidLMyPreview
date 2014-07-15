package com.shurpo.myapplication.app.basicmanagedprofile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.shurpo.myapplication.app.R;

import java.util.List;

/**
 * Created by Maksim on 09.07.2014.
 */
public class BasicManagedProfileFragment extends Fragment {

    /** Package names of calculator */
    private static final String[] PACKAGE_NAMES_CALCULATOR = {
            "com.android.calculator2"
    };

    /** Package names of Chrome */
    private static final String[] PACKAGE_NAMES_CHROME = {
            "com.android.chrome",
            "com.google.android.apps.chrome_dev",
            "com.chrome.canary",
            "com.chrome.beta",
    };

    private Button buttonRemoveProfile;

    /** Whether the calculator app is enabled in this profile */
    private boolean calculatorEnabled;

    /** Whether Chrome is enabled in this profile */
    private boolean chromeEnabled;

    public BasicManagedProfileFragment() {
    }

    public static BasicManagedProfileFragment newInstance(){
        return new BasicManagedProfileFragment();
    }

    @SuppressLint("Override")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    @SuppressLint("Override")
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Gets an instance of DevicePolicyManager
        DevicePolicyManager manager =
                (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        // Retrieves whether the calculator app is enabled in this profile
        calculatorEnabled = !manager.isApplicationBlocked(BasicDeviceAdminReceiver.getComponentName(activity), PACKAGE_NAMES_CALCULATOR[0]);
        // Retrieves whether Chrome is enabled in this profile
        chromeEnabled = false;
        for (String packageName : PACKAGE_NAMES_CHROME) {
            if (!manager.isApplicationBlocked(
                    BasicDeviceAdminReceiver.getComponentName(activity), packageName)) {
                chromeEnabled = true;
                return;
            }
        }
    }

    @Override
    @SuppressLint("Override")
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.set_chrome_restrictions).setOnClickListener(onClickListener());
        view.findViewById(R.id.enable_forwarding).setOnClickListener(onClickListener());
        view.findViewById(R.id.disable_forwarding).setOnClickListener(onClickListener());
        view.findViewById(R.id.send_intent).setOnClickListener(onClickListener());
        buttonRemoveProfile = (Button) view.findViewById(R.id.remove_profile);
        buttonRemoveProfile.setOnClickListener(onClickListener());
        Switch toggleCalculator = (Switch) view.findViewById(R.id.toggle_calculator);
        toggleCalculator.setChecked(calculatorEnabled);
        toggleCalculator.setOnCheckedChangeListener(onCheckedChangeListener());
        Switch toggleChrome = (Switch) view.findViewById(R.id.toggle_chrome);
        toggleChrome.setChecked(chromeEnabled);
        toggleCalculator.setOnCheckedChangeListener(onCheckedChangeListener());

    }

    private View.OnClickListener onClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.set_chrome_restrictions:
                        setChromeRestrictions();
                        break;
                    case R.id.enable_forwarding:
                        enableForwarding();
                        break;
                    case R.id.disable_forwarding:
                        disableForwarding();
                        break;
                    case R.id.send_intent:
                        sendIntent();
                        break;
                    case R.id.remove_profile:
                        buttonRemoveProfile.setEnabled(false);
                        removeProfile();
                        break;
                }
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener(){
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                switch (compoundButton.getId()){
                    case R.id.toggle_calculator:
                        setAppEnable(PACKAGE_NAMES_CALCULATOR, checked);
                        calculatorEnabled = checked;
                        break;
                    case R.id.toggle_chrome:
                        setAppEnable(PACKAGE_NAMES_CHROME, checked);
                        chromeEnabled = checked;
                        break;
                }
            }
        };
    }

    /**
     * Enables or disables the specified app in this profile.
     *
     * @param packageNames The package names of the target app.
     * @param enabled Pass true to enable the app.
     */
    private void setAppEnable(String[] packageNames, boolean enabled){
        Activity activity = getActivity();
        if(null == activity){
            return;
        }

        DevicePolicyManager manager = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        for (String packageName : packageNames){
            manager.setApplicationBlocked(BasicDeviceAdminReceiver.getComponentName(activity), packageName, !enabled);
        }
        Toast.makeText(activity, enabled ? "Enabled" : "Disabled", Toast.LENGTH_SHORT).show();
    }

    /**
     * Sets restrictions to Chrome
     */
    private void setChromeRestrictions(){
        final Activity activity = getActivity();
        if (activity == null){
            return;
        }

        final DevicePolicyManager manager = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        final Bundle settings = new Bundle();
        settings.putString("EditBookmarksEnabled", "false");
        settings.putString("IncognitoModeAvailability", "1");
        settings.putString("ManagedBookmarks",
                "[{\"name\": \"Chromium\", \"url\": \"http://chromium.org\"}, " +
                        "{\"name\": \"Google\", \"url\": \"https://www.google.com\"}]");
        settings.putString("DefaultSearchProviderEnabled", "true");
        settings.putString("DefaultSearchProviderName", "\"LMGTFY\"");
        settings.putString("DefaultSearchProviderSearchURL",
                "\"http://lmgtfy.com/?q={searchTerms}\"");
        settings.putString("URLBlacklist", "[\"m.vk.com\", \"vk.com\"]");
        StringBuilder message = new StringBuilder("Setting Chrome restrictions:");
        for (String key : settings.keySet()){
            message.append("\n");
            message.append(key);
            message.append(": ");
            message.append(settings.getString(key));
        }
        ScrollView view = new ScrollView(activity);
        TextView text = new TextView(activity);
        text.setText(message);
        int size = (int) activity.getResources().getDimension(R.dimen.horizontal_page_margin);
        view.setPadding(size, size, size, size);
        view.addView(text);
        new AlertDialog.Builder(activity).setView(view)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (String packageName : PACKAGE_NAMES_CHROME) {
                            // This is how you can set restrictions to an app.
                            // The format for settings in Bundle differs from app to app.
                            manager.setApplicationRestrictions
                                    (BasicDeviceAdminReceiver.getComponentName(activity),
                                            packageName, settings);
                            Toast.makeText(activity, "Restrictions set.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }

    /**
     * Enables forwarding of share intent between private account and managed profile.
     */
    private void enableForwarding(){
        Activity activity = getActivity();
        if(activity == null || activity.isFinishing()){
            return;
        }

        DevicePolicyManager manager = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        try {
            IntentFilter filter = new IntentFilter(Intent.ACTION_SEND);
            filter.addDataType("text/plain");
            filter.addDataType("image/jpeg");
            // This is how you can register an IntentFilter as allowed pattern of Intent forwarding
            manager.addForwardingIntentFilter(BasicDeviceAdminReceiver.getComponentName(activity),
                    filter,
                    DevicePolicyManager.FLAG_TO_PRIMARY_USER | DevicePolicyManager.FLAG_TO_MANAGED_PROFILE);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disables forwarding of all intents.
     */
    private void disableForwarding(){
        Activity activity = getActivity();
        if (null == activity || activity.isFinishing()) {
            return;
        }
        DevicePolicyManager manager = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        manager.clearForwardingIntentFilters(BasicDeviceAdminReceiver.getComponentName(activity));
    }

    /**
     * Sends a sample intent.
     */
    private void sendIntent(){
        Activity activity = getActivity();
        if (null == activity || activity.isFinishing()) {
            return;
        }
        DevicePolicyManager manager = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, manager.isProfileOwnerApp(activity.getApplicationContext().getPackageName())
                ? "From the managed account" : "From the primary account");
        startActivity(intent);
    }

    /**
     * Wipes out all the data related to this managed profile.
     */
    private void removeProfile(){
        Activity activity = getActivity();
        if (null == activity || activity.isFinishing()) {
            return;
        }
        DevicePolicyManager manager = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        manager.wipeData(0);
        // The screen turns off here
    }
}
