package com.shurpo.myapplication.app.cardview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.shurpo.myapplication.app.R;

/**
 * Created by Maksim on 12.07.2014.
 */
public class CardViewActivity extends Activity {

    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_CARD_VIEW_TEXT_HOMER = "detail:header:card:view:text:homer";
    public static final String VIEW_NAME_HEADER_CARD_VIEW_HOMER = "detail:header:card:view:homer";

    private TextView titleHomer;
    private ImageView homerImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_view_activity);

        homerImage = (ImageView) findViewById(R.id.homer_image1);
        homerImage.setViewName(VIEW_NAME_HEADER_CARD_VIEW_HOMER);

        titleHomer = (TextView) findViewById(R.id.title_homer);
        titleHomer.setViewName(VIEW_NAME_HEADER_CARD_VIEW_TEXT_HOMER);



        final Bitmap homerBitmap = ((BitmapDrawable)homerImage.getDrawable()).getBitmap();
        Palette palette = Palette.generate(homerBitmap);
        int bgColor = palette.getDarkMutedColor().getRgb();

        titleHomer.setBackgroundColor(setColorAlpha(bgColor, 192));
        titleHomer.setTextColor(palette.getLightMutedColor().getRgb());


    }

    private static int setColorAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00ffffff);
    }
}
