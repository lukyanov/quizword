package com.lingvapps.quizword.renew;

import com.lingvapps.quizword.renew.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CardFragment extends Fragment {

    private int currentMode = CardLayout.MODE_SINGLE_SIDE;
    private int currentCard = 0;
    private boolean showQuickTips = true;

    private CardSet cardSet = null;

    private ShakeListener shaker;

    static CardFragment newInstance(Integer setId, String setName,
            String langTerms, String langDefinitions) {
        CardFragment f = new CardFragment();

        Bundle args = new Bundle();
        args.putInt("set_id", setId);
        args.putString("set_name", setName);
        args.putString("lang_terms", langTerms);
        args.putString("lang_definitions", langDefinitions);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (savedInstanceState != null) {
            currentMode = savedInstanceState.getInt("currentMode");
            currentCard = savedInstanceState.getInt("currentCard");
        }

        Bundle args = getArguments();
        if (args != null) {
            cardSet = new CardSet(args.getInt("set_id"),
                    args.getString("set_name"), args.getString("lang_terms"),
                    args.getString("lang_definitions"));
            readCards();
        }

        shaker = new ShakeListener(getActivity());
        shaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
            public void onShake() {
                shuffleCards();
            }
        });
    }

    @Override
    public void onResume() {
        shaker.resume();
        super.onResume();
    }

    @Override
    public void onPause() {
        shaker.pause();
        TextToSpeech.releasePlayer();
        super.onPause();
    }

    @Override
    public void onStop() {
        shaker.pause();
        TextToSpeech.releasePlayer();
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (cardSet == null) {
            return new View(getActivity());
        }

        View view = inflater.inflate(R.layout.card_fragment, container, false);
        CardPager pager = (CardPager) view.findViewById(R.id.card_pager);
        initCardPager(pager);
        view.setBackgroundColor(Color.BLACK);

        updateModeButtonBackground(view);
        updateCounterText(view);

        setButtonListeners(view);

        if (showQuickTips
                && !Preferences.getInstance(getActivity()).isTipsHidden()) {
            drawQuickTips(view, inflater, container);
        }

        return view;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("currentMode", currentMode);
        outState.putInt("currentCard", currentCard);
    }

    private void setButtonListeners(final View view) {
        Button buttonMode = (Button) view.findViewById(R.id.mode_button);
        buttonMode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switchModes();
            }
        });

        Button buttonShuffle = (Button) view.findViewById(R.id.shuffle_button);
        buttonShuffle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                shuffleCards();
            }
        });

        Button buttonBack = (Button) view.findViewById(R.id.navigation_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        Button buttonSpeech = (Button) view.findViewById(R.id.speech_button);
        buttonSpeech.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                say();
            }
        });
    }

    private void drawQuickTips(final View view, LayoutInflater inflater,
            ViewGroup container) {
        View quickTipsView = inflater.inflate(R.layout.quick_tips, container,
                false);
        ((ViewGroup) view).addView(quickTipsView);

        Button buttonCloseTips = (Button) view.findViewById(R.id.close_button);
        buttonCloseTips.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox checkbox = (CheckBox) view
                        .findViewById(R.id.next_time_check_box);
                if (!checkbox.isChecked()) {
                    Preferences.getInstance(getActivity()).setHideTipsFlag();
                }
                showQuickTips = false;
                getRootView().removeView(view.findViewById(R.id.quick_tips));
            }
        });
    }

    private void initCardPager(CardPager pager) {
        pager.setMode(currentMode);
        pager.setCardSet(cardSet);
        pager.reinit();
        pager.setCurrentCard(currentCard);
        pager.setOnPageChangeListener(onPageChangeListener);
    }

    public void shuffleCards() {
        Vibrator vibe = (Vibrator) getActivity().getSystemService(
                Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);
        cardSet.shuffle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            turnEffect();
        } else {
            currentCard = 0;
            initCardPager(getPager());
            updateCounterText(getRootView());
            Toast.makeText(getActivity(), R.string.shuffled_message,
                    Toast.LENGTH_LONG).show();
        }
    }

    @TargetApi(11)
    public void turnEffect() {
        final CardLayout view = getPager().getCurrentCard();

        ObjectAnimator animFirst = ObjectAnimator.ofFloat(view, "rotation",
                0.0f, 540.0f);
        animFirst.setInterpolator(new AccelerateInterpolator());
        animFirst.setDuration(500);

        final ObjectAnimator animSecond = ObjectAnimator.ofFloat(view,
                "rotation", 540.0f, 3 * 360.0f);
        animSecond.setDuration(500);
        animSecond.setInterpolator(new DecelerateInterpolator());
        view.setRotation(540.0f);

        animSecond.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                initCardPager(getPager());
                updateCounterText(getRootView());
            }
        });

        final Card card = cardSet.getCard(0);

        animFirst.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                animSecond.start();
                currentCard = 0;
                switch (currentMode) {
                    case CardLayout.MODE_TERM_FIRST:
                        view.setFace(card.getTerm());
                        break;
                    case CardLayout.MODE_DEFINITION_FIRST:
                        view.setFace(card.getDefinition());
                        break;
                    case CardLayout.MODE_SINGLE_SIDE:
                        view.setFace(card.getTerm());
                        view.setBack(card.getDefinition());
                        break;
                }
            }
        });
        animFirst.start();
    }

    private void switchModes() {
        switch (currentMode) {
            case CardLayout.MODE_SINGLE_SIDE:
                currentMode = CardLayout.MODE_TERM_FIRST;
                break;
            case CardLayout.MODE_TERM_FIRST:
                currentMode = CardLayout.MODE_DEFINITION_FIRST;
                break;
            case CardLayout.MODE_DEFINITION_FIRST:
                currentMode = CardLayout.MODE_SINGLE_SIDE;
                break;
        }

        updateModeButtonBackground(getRootView());
        initCardPager(getPager());
    }

    private void updateModeButtonBackground(View view) {
        Button button = (Button) view.findViewById(R.id.mode_button);
        switch (currentMode) {
            case CardLayout.MODE_SINGLE_SIDE:
                button.setBackgroundResource(R.drawable.mode_ab);
                break;
            case CardLayout.MODE_TERM_FIRST:
                button.setBackgroundResource(R.drawable.mode_a);
                break;
            case CardLayout.MODE_DEFINITION_FIRST:
                button.setBackgroundResource(R.drawable.mode_b);
                break;
        }
    }

    private ViewGroup getRootView() {
        FrameLayout frame = (FrameLayout) getView();
        return (ViewGroup) frame.getChildAt(0);
    }

    private CardPager getPager() {
        return (CardPager) getRootView().getChildAt(0);
    }

    private void readCards() {
        RetrieveSetTask task = new RetrieveSetTask(getActivity());
        task.setOnPostExecuteListener(new RetrieveSetTask.OnPostExecuteListener<CardSet>() {
            public void onSuccess(CardSet set) {
                cardSet = set;
                refresh();
            }

            public void onFailure() {
            }
        });
        task.execute(cardSet);
    }

    private void refresh() {
        getFragmentManager().beginTransaction().detach(this).attach(this)
                .commit();
    }

    private void updateCounterText(View rootView) {
        TextView text = (TextView) rootView.findViewById(R.id.counter_text);
        String i = Integer.valueOf(currentCard + 1).toString();
        String n = Integer.valueOf(cardSet.size()).toString();
        text.setText(i + "/" + n);
    }

    private void say() {
        final Button button = (Button) getRootView().findViewById(
                R.id.speech_button);
        final ProgressBar bar = new ProgressBar(getActivity());
        final RelativeLayout parent = (RelativeLayout) button.getParent();
        final int index = parent.indexOfChild(button);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) button
                .getLayoutParams();
        bar.setLayoutParams(params);

        parent.removeView(button);
        parent.addView(bar, index);
        
        CardLayout cardLayout = getPager().getCurrentCard();

        TextToSpeech.utterCard(getActivity(), cardLayout, currentMode, new TextToSpeech.OnCompletionListener() {
            public void onSuccess() {
                parent.removeView(bar);
                parent.addView(button, index);
            }

            public void onFailure() {
                parent.removeView(bar);
                parent.addView(button, index);
                Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_LONG).show();
            }
        });
    }

    private final CardPager.OnPageChangeListener onPageChangeListener = new CardPager.OnPageChangeListener() {

        public void onPageScrollStateChanged(int arg0) { }

        public void onPageScrolled(int arg0, float arg1, int arg2) { }

        public void onPageSelected(int position) {
            currentCard = position;
            updateCounterText(getRootView());
        }

    };
}
