package com.shurpo.myapplication.app.activtyscenetransitionbasic;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.shurpo.myapplication.app.R;
import com.shurpo.myapplication.app.navigationdrawer.NavigationDrawerActivity;


public class ScenetransitionBasicFragment extends Fragment implements AdapterView.OnItemClickListener {

    private GridView mGridView;
    private GridAdapter mAdapter;

    private ImageLoader mImageLoader;

    public ScenetransitionBasicFragment() {
    }

    public static Fragment newInstance(int position){
        Fragment fragment = new ScenetransitionBasicFragment();
        Bundle args = new Bundle();
        args.putInt(NavigationDrawerActivity.ARG_FEATURE_NUMBER, position);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    @SuppressLint("Override")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.grid, container, false);
        int i = getArguments().getInt(NavigationDrawerActivity.ARG_FEATURE_NUMBER);
        String feature = getResources().getStringArray(R.array.feature_array)[i];
        getActivity().setTitle(feature);

        // Retrieve the ImageLoader we are going to use for NetworkImageView
        mImageLoader = new ImageLoader(Volley.newRequestQueue(getActivity()), ImageMemoryCache.INSTANCE);

        // Setup the GridView and set the adapter
        mGridView = (GridView) rootView.findViewById(R.id.grid);
        mGridView.setOnItemClickListener(this);
        mAdapter = new GridAdapter();
        mGridView.setAdapter(mAdapter);

        return rootView;
    }

    /**
     * Called when an item in the {@link android.widget.GridView} is clicked. Here will launch the
     * {@link DetailActivity}, using the Scene Transition animation functionality.
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Item item = (Item) adapterView.getItemAtPosition(position);

        // Construct an Intent as normal
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_PARAM_ID, item.getId());

        // BEGIN_INCLUDE(start_activity)
        /**
         * Now create an {@link android.app.ActivityOptions} instance using the
         * {@link android.app.ActivityOptions#makeSceneTransitionAnimation(android.app.Activity, android.util.Pair[])} factory method.
         */
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                getActivity(),

                // Now we provide a list of Pair items which contain the view we can transitioning
                // from, and the name of the view it is transitioning to, in the launched activity
                new Pair<View, String>(
                        view.findViewById(R.id.imageview_item),
                        DetailActivity.VIEW_NAME_HEADER_IMAGE),
                new Pair<View, String>(
                        view.findViewById(R.id.textview_name),
                        DetailActivity.VIEW_NAME_HEADER_TITLE)
        );

        // Now we can start the Activity, providing the activity options as a bundle
        startActivity(intent, activityOptions.toBundle());
        // END_INCLUDE(start_activity)
    }

    /**
     * {@link android.widget.BaseAdapter} which displays items.
     */
    private class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Item.ITEMS.length;
        }

        @Override
        public Item getItem(int position) {
            return Item.ITEMS[position];
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.grid_item, viewGroup, false);
            }

            final Item item = getItem(position);

            // Load the thumbnail image
            NetworkImageView image = (NetworkImageView) view.findViewById(R.id.imageview_item);
            image.setImageUrl(item.getThumbnailUrl(), mImageLoader);

            // Set the TextView's contents
            TextView name = (TextView) view.findViewById(R.id.textview_name);
            name.setText(item.getName());

            // BEGIN_INCLUDE(grid_set_view_name)
            /**
             * As we're in an adapter we need to set each view's name dynamically, using the
             * item's ID so that the names are unique.
             */
            image.setViewName("grid:image:" + item.getId());
            name.setViewName("grid:name:" + item.getId());
            // END_INCLUDE(grid_set_view_name)

            return view;
        }
    }


}
