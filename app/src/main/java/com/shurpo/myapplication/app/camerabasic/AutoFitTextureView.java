package com.shurpo.myapplication.app.camerabasic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by Maksim on 11.07.2014.
 */
public class AutoFitTextureView extends TextureView {

    private int rationWidth = 0;
    private int rationHeight = 0;

    public AutoFitTextureView(Context context) {
        super(context, null);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0){
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        rationWidth = width;
        rationHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if(rationHeight == 0 || rationWidth == 0){
            setMeasuredDimension(width, height);
        }else {
            if(width < height * rationWidth / rationHeight){
                setMeasuredDimension(width, width * rationHeight / rationWidth);
            } else {
                setMeasuredDimension(height * rationWidth / rationWidth, height);
            }
        }
    }
}
