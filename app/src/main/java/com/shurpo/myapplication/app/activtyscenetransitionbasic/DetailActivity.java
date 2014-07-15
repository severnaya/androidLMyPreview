package com.shurpo.myapplication.app.activtyscenetransitionbasic;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.TextView;
import com.shurpo.myapplication.app.R;

/**
 * Created by Maksim on 08.07.2014.
 */
public class DetailActivity extends Activity{

    // Extra name for the ID parameter
    public static final String EXTRA_PARAM_ID = "detail:_id";

    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";

    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_TITLE = "detail:header:title";

    private NetworkImageView mHeaderImageView;
    private TextView mHeaderTitle;

    private ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        // Construct an ImageLoader instance so that we can load images from the network
        mImageLoader = new ImageLoader(Volley.newRequestQueue(this), ImageMemoryCache.INSTANCE);

        // Retrieve the correct Item instance, using the ID provided in the Intent
        Item item = Item.getItem(getIntent().getIntExtra(EXTRA_PARAM_ID, 0));

        mHeaderImageView = (NetworkImageView) findViewById(R.id.imageview_header);
        mHeaderTitle = (TextView) findViewById(R.id.textview_title);

        // BEGIN_INCLUDE(detail_set_view_name)
        /**
         * Set the name of the view's which will be transition to, using the static values above.
         * This could be done in the layout XML, but exposing it via static variables allows easy
         * querying from other Activities
         */
        mHeaderImageView.setViewName(VIEW_NAME_HEADER_IMAGE);
        mHeaderTitle.setViewName(VIEW_NAME_HEADER_TITLE);
        // END_INCLUDE(detail_set_view_name)

        loadItem(item);
    }

    private void loadItem(Item item) {
        // Set the title TextView to the item's name and author
        mHeaderTitle.setText(getString(R.string.image_header, item.getName(), item.getAuthor()));

        final ImageMemoryCache cache = ImageMemoryCache.INSTANCE;
        Bitmap thumbnailImage = cache.getBitmapFromUrl(item.getThumbnailUrl());

        // Check to see if we already have the thumbnail sized image in the cache. If so, start
        // loading the full size image and display the thumbnail as a placeholder.
        if (thumbnailImage != null) {
            mHeaderImageView.setImageUrl(item.getPhotoUrl(), mImageLoader);
            mHeaderImageView.setImageBitmap(thumbnailImage);
            return;
        }

        // If we get here then we do not have either the full size or the thumbnail in the cache.
        // Here we just load the full size and make do.
        mHeaderImageView.setImageUrl(item.getPhotoUrl(), mImageLoader);
    }

}
