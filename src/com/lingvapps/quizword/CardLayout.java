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
    
    static final int SIDE_TERM = 0;
    static final int SIDE_DEFINITION = 1;
    static final int SIDE_BOTH = 2;
    
    private int currentSide = SIDE_BOTH;
    
    private Interpolator accelerator = new AccelerateInterpolator();
    private Interpolator decelerator = new DecelerateInterpolator();

    public CardLayout(Context context) {
        super(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.card, this);
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
    
    @TargetApi(12)
    private void flip(View v) {
//        View visibleView;
//        View invisibleView;
        if (currentSide == SIDE_TERM) {
            currentSide = SIDE_DEFINITION;
        } else {
            currentSide = SIDE_TERM;
        }
        View view = this;
        //float scale = getResources().getDisplayMetrics().density;
        //view.setCameraDistance(1.5f * scale);
        
        ObjectAnimator visToInvis = ObjectAnimator.ofFloat(view, "rotationY", 0f, 90f);
        visToInvis.setDuration(300);
        visToInvis.setInterpolator(accelerator);

        TextView textView = (TextView) findViewById(R.id.card_definition);
        setTerm(textView.getText().toString());

        final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(view, "rotationY",
                -90f, 0f);
        invisToVis.setDuration(500);
        invisToVis.setInterpolator(decelerator);
        visToInvis.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                //visibleList.setVisibility(View.GONE);
                invisToVis.start();
                //invisibleList.setVisibility(View.VISIBLE);
            }
        });
        visToInvis.start();
    }

}
