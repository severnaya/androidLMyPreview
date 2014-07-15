package com.shurpo.myapplication.app.clippingbasic;

import android.app.Fragment;
import android.graphics.Outline;
import android.graphics.Path;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.shurpo.myapplication.app.R;

/**
 * Created by Maksim on 10.07.2014.
 */
public class ClippingBasicFragment extends Fragment {

    /* Store the click count so that we can show a different text on every click. */
    private int mClickCount = 0;

    /* The {@Link Outline} used to clip the image with. */
    private Outline clip;

    /* An array of texts. */
    private String[] sampleTexts;

    /* A reference to a {@Link TextView} that shows different text strings when clicked. */
    private TextView textView;


    public ClippingBasicFragment() {
    }

    public static ClippingBasicFragment newInstance(){
        return new ClippingBasicFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        clip = new Outline();
        sampleTexts = getResources().getStringArray(R.array.sample_texts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.clipping_basic_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* Set the initial text for the TextView. */
        textView = (TextView) view.findViewById(R.id.clipping_text_view);
        changeText();

        /* When the button is clicked, the text is clipped or un-clipped. */
        view.findViewById(R.id.clipping_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View bt) {

                View clippedView = view.findViewById(R.id.clipping_frame);

                /* The view is already clipped if {Link View#getClipToOutline()} returns true. */
                if (clippedView.getClipToOutline()) {
                    /* The Outline is set for the View, but disable clipping. */
                    clippedView.setClipToOutline(false);

                    ((Button) bt).setText(R.string.clip_button);

                } else {
                    /*
                    Sets the dimensions and shape of the {@Link Outline}. A rounded rectangle
                    with a margin determined by the width or height.
                    */
                    int margin = Math.min(clippedView.getWidth(), clippedView.getHeight()) / 10;
                    clip.setRoundRect(margin, margin, clippedView.getWidth() - margin,
                            clippedView.getHeight() - margin, margin / 2);


                    /* Sets the Outline of the View. */
                    clippedView.setOutline(clip);
                    /* Enables clipping on the View. */
                    clippedView.setClipToOutline(true);

                    ((Button) bt).setText(R.string.unclip_button);
                }
            }
        });

        /* When the text is clicked, a new string is shown. */
        view.findViewById(R.id.clipping_text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickCount++;
                changeText();
            }
        });
    }

    private void changeText() {
        /*
        Compute the position of the string in the array using the number of strings
        and the number of clicks.
        */
        String newText = sampleTexts[mClickCount % sampleTexts.length];

        /* Once the text is selected, change the TextView */
        textView.setText(newText);
    }
}
