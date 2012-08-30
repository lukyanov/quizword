package com.lingvapps.quizword;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CardLayout extends LinearLayout {
    
    static final public int SIDE_TERM = 0;
    static final public int SIDE_DEFINITION = 1;
    static final public int SIDE_BOTH = 2;
    
    private int currentSide = SIDE_BOTH;
    
    private Interpolator accelerator = new AccelerateInterpolator();
    private Interpolator decelerator = new DecelerateInterpolator();

    public CardLayout(Context context) {
        super(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.card, this);
        //setRotationY(-90f);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d("quizword", Integer.valueOf(ev.getAction()).toString());
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            return true;
        case MotionEvent.ACTION_UP:
            if (currentSide != SIDE_BOTH) {
                flip(this);
            }
            return true;
        default:
            return false;
        }
    }
    
    public void setTerm(String term) {
        TextView textView = (TextView) findViewById(R.id.card_term);
        textView.setText(term);
    }
    
    public void setDefinition(String definition) {
        TextView textView = (TextView) findViewById(R.id.card_definition);
        textView.setText(definition);
    }
    
    public void setCurrentSide(int side) {
        if (currentSide == side) {
            return;
        }
        int definitionVisibility = View.VISIBLE;
        switch (side) {
        case SIDE_BOTH:
            if (currentSide == SIDE_DEFINITION) {
                swapSides();
            }
            definitionVisibility = View.VISIBLE;
            break;
        case SIDE_TERM:
            if (currentSide == SIDE_DEFINITION) {
                swapSides();
            }
            definitionVisibility = View.GONE;
            break;
        case SIDE_DEFINITION:
            swapSides();
            definitionVisibility = View.GONE;
            break;
        }
        findViewById(R.id.card_definition).setVisibility(definitionVisibility);
        currentSide = side;
    }
    
    private void swapSides() {
        TextView termView = ((TextView) findViewById(R.id.card_term));
        TextView definitionView = ((TextView) findViewById(R.id.card_definition));
        
        CharSequence term = termView.getText();
        termView.setText(definitionView.getText());
        definitionView.setText(term);

        if (currentSide == SIDE_TERM) {
            currentSide = SIDE_DEFINITION;
        } else {
            currentSide = SIDE_TERM;
        }
    }
    
    @TargetApi(12)
    private void flip(View v) {
        View view = this;
        //float scale = getResources().getDisplayMetrics().density;
        //view.setCameraDistance(1.5f * scale);
        
        ObjectAnimator visToInvis = ObjectAnimator.ofFloat(view, "rotationY", 0f, 90f);
        visToInvis.setDuration(300);
        visToInvis.setInterpolator(accelerator);

        final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(view, "rotationY",
                -90f, 0f);
        invisToVis.setDuration(300);
        invisToVis.setInterpolator(decelerator);
        visToInvis.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                swapSides();
                invisToVis.start();
            }
        });
        visToInvis.start();
    }

}
