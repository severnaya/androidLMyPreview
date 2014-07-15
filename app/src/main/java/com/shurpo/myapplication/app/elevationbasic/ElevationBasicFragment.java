package com.shurpo.myapplication.app.elevationbasic;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.*;
import android.widget.*;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.shurpo.myapplication.app.R;
import com.shurpo.myapplication.app.activtyscenetransitionbasic.ImageMemoryCache;
import com.shurpo.myapplication.app.activtyscenetransitionbasic.Item;

/**
 * Created by Maksim on 10.07.2014.
 */
public class ElevationBasicFragment extends Fragment {

    private ImageLoader mImageLoader;
    private GridView gridView;
    private GridAdapter gridAdapter;
    private View shape;

    TitleDialogFragment.Communicator communicator = new TitleDialogFragment.Communicator() {
        @Override
        public void onDialogMessage(String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    };

    public ElevationBasicFragment() {
    }

    public static ElevationBasicFragment newInstance() {
        return new ElevationBasicFragment();
    }

    @SuppressLint("Override")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.elevation_view, container, false);
        return rootView;
    }

    @SuppressLint("Override")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Retrieve the ImageLoader we are going to use for NetworkImageView
        mImageLoader = new ImageLoader(Volley.newRequestQueue(getActivity()), ImageMemoryCache.INSTANCE);

        shape = view.findViewById(R.id.floating_shape);
        shape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                TitleDialogFragment dialogFragment = new TitleDialogFragment(communicator);
                dialogFragment.show(manager, "My Dialog");
            }
        });

        // Setup the GridView and set the adapter
        gridView = (GridView) view.findViewById(R.id.my_recycler_view);
        gridAdapter = new GridAdapter();
        gridView.setAdapter(gridAdapter);
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (AbsListView.OnScrollListener.SCROLL_STATE_IDLE == scrollState) {
                    animShape(true);
                } else {
                    animShape(false);
                }

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {

            }
        });
    }

    private void animShape(final boolean visible){
        // get the center for the clipping circle
        int cx = (shape.getLeft() + shape.getRight()) / 2;
        int cy = (shape.getTop() + shape.getBottom()) / 2;

        // get the initial radius for the clipping circle
        int initialRadius = shape.getWidth();

        // create the animation (the final radius is zero)
        ValueAnimator anim = ViewAnimationUtils.createCircularReveal(shape, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (visible) {
                    shape.setVisibility(View.VISIBLE);
                }else {
                    shape.setVisibility(View.INVISIBLE);
                }
            }
        });
        anim.start();

    }

    @Override
    @SuppressLint("Override")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
