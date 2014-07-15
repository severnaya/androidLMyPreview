package com.shurpo.myapplication.app.cardview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.graphics.drawable.AnimatedStateListDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.PathInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.shurpo.myapplication.app.R;

/**
 * Created by Maksim on 11.07.2014.
 */
public class MyCardViewFragment extends Fragment {

    public static MyCardViewFragment newInstance(){
        return new MyCardViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_card_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final CardView cardView = (CardView) view.findViewById(R.id.card_view);
        final TextView textHomer = (TextView) view.findViewById(R.id.text_homer);
        final ImageView homerImage = (ImageView) view.findViewById(R.id.homer_image);
        textHomer.setViewName(CardViewActivity.VIEW_NAME_HEADER_CARD_VIEW_TEXT_HOMER);
        homerImage.setViewName(CardViewActivity.VIEW_NAME_HEADER_CARD_VIEW_HOMER);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CardViewActivity.class);
                // create the transition animation - the images in the layouts
                // of both activities are defined with android:viewName="robot"
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                        new Pair<View, String>(textHomer, CardViewActivity.VIEW_NAME_HEADER_CARD_VIEW_TEXT_HOMER),
                        new Pair<View, String>(homerImage, CardViewActivity.VIEW_NAME_HEADER_CARD_VIEW_HOMER));
                // start the new activity
                startActivity(intent, options.toBundle());
            }
        });

        final View viewAnim = view.findViewById(R.id.view_anim);
        viewAnim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Path path = new Path();
                path.moveTo(viewAnim.getX(), viewAnim.getY());
                path.lineTo(viewAnim.getX() + 200, viewAnim.getY());
                path.lineTo(viewAnim.getX() + 200, viewAnim.getY() + 200);
                path.lineTo(viewAnim.getX() - 200, viewAnim.getY() + 200);
                path.lineTo(viewAnim.getX() - 200, viewAnim.getY());

               // PathInterpolator pathInterpolator = new PathInterpolator(path);
                ObjectAnimator oa = ObjectAnimator.ofFloat(viewAnim, View.X, View.Y, path);
                //oa.setInterpolator(pathInterpolator);
                oa.setDuration(3000);
                oa.start();
            }
        });
    }
}
