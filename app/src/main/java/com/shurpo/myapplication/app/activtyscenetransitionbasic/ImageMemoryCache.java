package com.shurpo.myapplication.app.activtyscenetransitionbasic;

import android.graphics.Bitmap;
import android.util.LruCache;
import com.android.volley.toolbox.ImageLoader;

import java.util.Map;

/**
 * Created by Maksim on 08.07.2014.
 */
public class ImageMemoryCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {

    /**
     * Singleton instance which has it's maximum size set to be 1/8th of the allowed memory size.
     */
    public static final ImageMemoryCache INSTANCE = new ImageMemoryCache(
            (int) (Runtime.getRuntime().maxMemory() / 8));

    // Cache the last created snapshot
    private Map<String, Bitmap> mLastSnapshot;

    private ImageMemoryCache(int maxSize) {
        super(maxSize);
    }

    public Bitmap getBitmapFromUrl(String url) {
        // If we do not have a snapshot to use, generate one
        if (mLastSnapshot == null) {
            mLastSnapshot = snapshot();
        }

        // Iterate through the snapshot to find any entries which match our url
        for (Map.Entry<String, Bitmap> entry : mLastSnapshot.entrySet()) {
            if (url.equals(extractUrl(entry.getKey()))) {
                // We've found an entry with the same url, return the bitmap
                return entry.getValue();
            }
        }

        // We didn't find an entry, so return null
        return null;
    }

    @Override
    public Bitmap getBitmap(String key) {
        return get(key);
    }

    @Override
    public void putBitmap(String key, Bitmap bitmap) {
        put(key, bitmap);

        // An entry has been added, so invalidate the snapshot
        mLastSnapshot = null;
    }

    @Override
    protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);

        // An entry has been removed, so invalidate the snapshot
        mLastSnapshot = null;
    }

    private static String extractUrl(String key) {
        return key.substring(key.indexOf("http"));
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getAllocationByteCount();
    }
}
