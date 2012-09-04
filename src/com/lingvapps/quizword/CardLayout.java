package com.lingvapps.quizword;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
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

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d("quizword", Integer.valueOf(ev.getAction()).toString());
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            return true;
        case MotionEvent.ACTION_UP:
            if (currentMode != MODE_SINGLE_SIDE) {
                flip();
            }
            return true;
        default:
            return false;
        }
    }

    public void setFace(String text) {
        ((TextView) faceSide.findViewById(R.id.card_term))
        .setText(text);
    }

    public void setBack(String text) {
        ((TextView) faceSide.findViewById(R.id.card_definition))
        .setText(text);
        ((TextView) backSide.findViewById(R.id.card_definition_back))
        .setText(text);
    }

    public void setCurrentMode(int mode) {
        if (currentMode == mode) {
            return;
        }
        int definitionVisibility = View.VISIBLE;
        switch (mode) {
        case MODE_SINGLE_SIDE:
            if (currentMode == MODE_DEFINITION_FIRST) {
                swapSides();
            }
            definitionVisibility = View.VISIBLE;
            break;
        case MODE_TERM_FIRST:
            if (currentMode == MODE_DEFINITION_FIRST) {
                swapSides();
            }
            definitionVisibility = View.GONE;
            break;
        case MODE_DEFINITION_FIRST:
            swapSides();
            definitionVisibility = View.GONE;
            break;
        }
        faceSide.findViewById(R.id.card_definition).setVisibility(definitionVisibility);
        currentMode = mode;

        if (currentSide == SIDE_DEFINITION) {
            initSides();
        }
    }

    private void swapSides() {
        TextView termView = (TextView) faceSide.findViewById(R.id.card_term);
        TextView defView = (TextView) faceSide.findViewById(R.id.card_definition);
        TextView defViewBack = (TextView) backSide.findViewById(R.id.card_definition_back);

        CharSequence term = termView.getText();
        termView.setText(defView.getText());
        defView.setText(term);
        defViewBack.setText(term);
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
