package com.lingvapps.quizword;

import com.mediaportal.ampdroid.controls.AutoResizeTextView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

public class CardLayout extends FrameLayout {

    public static final int MODE_SINGLE_SIDE = 0;
    public static final int MODE_TERM_FIRST = 1;
    public static final int MODE_DEFINITION_FIRST = 2;

    private static final int SIDE_TERM = 3;
    private static final int SIDE_DEFINITION = 4;

    private View faceSide = null;
    private View backSide = null;

    private int currentMode = MODE_SINGLE_SIDE;
    private int currentSide = SIDE_TERM;

    private Interpolator accelerator = new AccelerateInterpolator();
    private Interpolator decelerator = new DecelerateInterpolator();

    public interface OnTurnListener {
        public void onStop();
    }

    public CardLayout(Context context) {
        super(context);
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        faceSide = layoutInflater.inflate(R.layout.card_face, null);
        backSide = layoutInflater.inflate(R.layout.card_back, null);
        initSides();
        addView(faceSide);
        addView(backSide);
        setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {
                if (currentMode != MODE_SINGLE_SIDE) {
                    flip();
                }
            }
        });
    }

    @TargetApi(11)
    private void initSides() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            faceSide.setRotationY(0f);
            backSide.setRotationY(-90f);
        }
        faceSide.setVisibility(View.VISIBLE);
        backSide.setVisibility(View.GONE);
        currentSide = SIDE_TERM;
    }
    
    public void setFace(String text) {
        AutoResizeTextView view = (AutoResizeTextView) faceSide.findViewById(R.id.card_term);
        view.setText(text);
        view.resizeText();
    }

    public void setBack(String text) {
        AutoResizeTextView view = (AutoResizeTextView) faceSide.findViewById(R.id.card_definition);
        view.setText(text);
        view.resizeText();
        view = (AutoResizeTextView) backSide.findViewById(R.id.card_definition_back);
        view.setText(text);
        view.resizeText();
    }

    public void setCurrentMode(int mode) {
        if (currentMode == mode) {
            return;
        }
        int definitionVisibility = View.VISIBLE;
        switch (mode) {
        case MODE_SINGLE_SIDE:
            definitionVisibility = View.VISIBLE;
            break;
        default:
            definitionVisibility = View.GONE;
            break;
        }
        faceSide.findViewById(R.id.card_definition).setVisibility(definitionVisibility);
        currentMode = mode;
    }

    @TargetApi(11)
    private void flip() {
        final View viewVisible;
        final View viewInvisible;
        final int directionSign;

        if (currentSide == SIDE_TERM) {
            viewVisible = faceSide;
            viewInvisible = backSide;
            currentSide = SIDE_DEFINITION;
            directionSign = 1;
        } else {
            viewVisible = backSide;
            viewInvisible = faceSide;
            currentSide = SIDE_TERM;
            directionSign = -   1;
        }

        //float scale = getResources().getDisplayMetrics().density;
        //view.setCameraDistance(1.5f * scale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            ObjectAnimator visToInvis = ObjectAnimator.ofFloat(viewVisible, "rotationY", 0f, directionSign * 90f);
            visToInvis.setDuration(300);
            visToInvis.setInterpolator(accelerator);
            final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(viewInvisible, "rotationY",
                    (-directionSign) * 90f, 0f);
            invisToVis.setDuration(300);
            invisToVis.setInterpolator(decelerator);
            visToInvis.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator anim) {
                    viewVisible.setVisibility(View.GONE);
                    invisToVis.start();
                    viewInvisible.setVisibility(View.VISIBLE);
                }
            });
            visToInvis.start();
        } else {
            viewVisible.setVisibility(View.GONE);
            viewInvisible.setVisibility(View.VISIBLE);
        }
    }
}
