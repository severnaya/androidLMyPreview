package com.shurpo.myapplication.app.navigationdrawer;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import com.shurpo.myapplication.app.R;
import com.shurpo.myapplication.app.activtyscenetransitionbasic.ScenetransitionBasicFragment;
import com.shurpo.myapplication.app.basicmanagedprofile.BasicManagedProfileFragment;
import com.shurpo.myapplication.app.basicmanagedprofile.SetupProfileFragment;
import com.shurpo.myapplication.app.camera2video.Camera2VideoFragment;
import com.shurpo.myapplication.app.camerabasic.Camera2BasicFragment;
import com.shurpo.myapplication.app.cardview.MyCardViewFragment;
import com.shurpo.myapplication.app.clippingbasic.ClippingBasicFragment;
import com.shurpo.myapplication.app.elevationbasic.ElevationBasicFragment;
import com.shurpo.myapplication.app.elevationdrag.ElevationDragFragment;
import com.shurpo.myapplication.app.jobsheduler.JobServiceFragment;

/**
 * Created by Maksim on 08.07.2014.
 */
public class NavigationDrawerActivity extends Activity {

    public static final String ARG_FEATURE_NUMBER = "ARG_FEATURE_NUMBER";

    public static final int CARD_VIEW_POSITION = 0;
    public static final int ACTIVITY_SCENE_TRANSITION_BASIC_POSITION = 1;
    public static final int BASIC_MANAGED_PROFILE_POSITION = 2;
    public static final int ELEVATION_BASIC_POSITION = 3;
    public static final int ELEVATION_DROP_POSITION = 4;
    public static final int CLIPPING_BASIC_POSITION = 5;
    public static final int JOB_SERVICE_POSITION = 6;

    private DrawerLayout drawerLayout;
    private RecyclerView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    private CharSequence drawerTitle;
    private CharSequence title;
    private String[] featureTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        title = drawerTitle = getTitle();
        featureTitle = getResources().getStringArray(R.array.feature_array);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (RecyclerView)findViewById(R.id.left_drawer);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerList.setHasFixedSize(true);

        drawerList.setLayoutManager(new LinearLayoutManager(this));
        drawerList.setAdapter(new FeatureAdapter(featureTitle, onItemClickListener()));


        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }


        };
        drawerLayout.setDrawerListener(drawerToggle);

        if(savedInstanceState == null){
            selectItem(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectItem(int position){
        Fragment fragment = null;
        switch (position){
            case CARD_VIEW_POSITION:
                fragment = MyCardViewFragment.newInstance();
                break;
            case ACTIVITY_SCENE_TRANSITION_BASIC_POSITION:
                fragment = ScenetransitionBasicFragment.newInstance(position);
                break;
            case BASIC_MANAGED_PROFILE_POSITION:
                DevicePolicyManager manager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
                if (manager.isProfileOwnerApp(getApplicationContext().getPackageName())){
                    // If the managed profile is already set up, we show the main screen.
                    fragment = BasicManagedProfileFragment.newInstance();
                }else {
                    fragment = SetupProfileFragment.newInstance();
                    // If not, we show the set up screen.
                }
                break;
            case ELEVATION_BASIC_POSITION:
                fragment = ElevationBasicFragment.newInstance();
                break;
            case ELEVATION_DROP_POSITION:
                fragment = ElevationDragFragment.newInstance();
                break;
            case CLIPPING_BASIC_POSITION:
                fragment = ClippingBasicFragment.newInstance();
                break;
            case JOB_SERVICE_POSITION:
                fragment = JobServiceFragment.newInstance();
                break;
            /*case CAMERA2_BASIC:
                fragment = Camera2BasicFragment.newInstance();
                break;
            case CAMERA2_VIDEO:
                fragment = Camera2VideoFragment.newInstance();
                break;*/
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();

        setTitle(featureTitle[position]);
        drawerLayout.closeDrawer(drawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        getActionBar().setTitle(title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private FeatureAdapter.OnItemClickListener onItemClickListener(){
        return new FeatureAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                selectItem(position);
            }
        };
    }
}

