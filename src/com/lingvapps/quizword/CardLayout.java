package com.lingvapps.quizword;

import com.mediaportal.ampdroid.controls.AutoResizeTextView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

public class CardLayout extends FrameLayout {

    public static final int MODE_SINGLE_SIDE = 0;
    public static final int MODE_TERM_FIRST = 1;
    public static final int MODE_DEFINITION_FIRST = 2;

    private static final int SIDE_FACE = 3;
    private static final int SIDE_BACK = 4;

    private Card card;

    private View faceSide = null;
    private View backSide = null;

    private int mode = MODE_SINGLE_SIDE;

    // make sense only for double side modes
    private int currentSide;
    private String currentSideType;
    private String currentSideText;
    private String currentSideLang;

    private Interpolator accelerator = new AccelerateInterpolator();
    private Interpolator decelerator = new DecelerateInterpolator();

    public interface OnTurnListener {
        public void onStop();
    }

    public CardLayout(Context context, Card card, final int mode) {
        super(context);
        
        this.card = card;
        this.mode = mode;
        
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        faceSide = layoutInflater.inflate(R.layout.card_face, null);
        backSide = layoutInflater.inflate(R.layout.card_back, null);
        addView(faceSide);
        addView(backSide);
        
        setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mode != MODE_SINGLE_SIDE) {
                    flip();
                }
            }
        });

        init();
    }

    @TargetApi(11)
    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            faceSide.setRotationY(0f);
            backSide.setRotationY(-90f);
        }
        faceSide.setVisibility(View.VISIBLE);
        backSide.setVisibility(View.GONE);
        initMode();
    }

    private void initMode() {
        int definitionVisibility = View.VISIBLE;
        switch (mode) {
        case MODE_SINGLE_SIDE:
            setFace(card.getTerm());
            setBack(card.getDefinition());
            currentSide = -1;
            setSingleSide();
            definitionVisibility = View.VISIBLE;
            break;
        case MODE_TERM_FIRST:
            setFace(card.getTerm());
            setBack(card.getDefinition());
            currentSide = SIDE_FACE;
            setTermSide();
            definitionVisibility = View.GONE;
            break;
        case MODE_DEFINITION_FIRST:
            setFace(card.getDefinition());
            setBack(card.getTerm());
            currentSide = SIDE_FACE;
            setDefinitionSide();
            definitionVisibility = View.GONE;
            break;
        }
        faceSide.findViewById(R.id.card_definition).setVisibility(definitionVisibility);
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

    public String getCurrentSideText() {
        return currentSideText;
    }

    public String getCurrentSideLang() {
        return currentSideLang;
    }
    
    public String getCurrentSideType() {
        return currentSideType;
    }
    
    private void setTermSide() {
        currentSideType = "term";
        currentSideText = card.getTerm();
        currentSideLang = card.getCardSet().getLangTerms();
    }
    
    private void setDefinitionSide() {
        currentSideType = "definition";
        currentSideText = card.getDefinition();
        currentSideLang = card.getCardSet().getLangDefinitions();
    }
    
    private void setSingleSide() {
        currentSideType = null;
        currentSideText = null;
        currentSideLang = null;
    }

    private void flip() {
        View viewVisible;
        View viewInvisible;
        int directionSign;

        if (currentSide == SIDE_FACE) {
            viewVisible = faceSide;
            viewInvisible = backSide;
            currentSide = SIDE_BACK;
            if (mode == MODE_TERM_FIRST) {
                setDefinitionSide();
            } else {
                setTermSide();
            }
            directionSign = 1;
        } else {
            viewVisible = backSide;
            viewInvisible = faceSide;
            currentSide = SIDE_FACE;
            if (mode == MODE_TERM_FIRST) {
                setTermSide();
            } else {
                setDefinitionSide();
            }
            directionSign = -1;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            runFlipAnimation(viewVisible, viewInvisible, directionSign);
        } else {
            viewVisible.setVisibility(View.GONE);
            viewInvisible.setVisibility(View.VISIBLE);
        }
    }

    @TargetApi(11)
    private void runFlipAnimation(final View viewVisible, final View viewInvisible, final int directionSign) {

        //float scale = getResources().getDisplayMetrics().density;
        //view.setCameraDistance(1.5f * scale);

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
    }
}
