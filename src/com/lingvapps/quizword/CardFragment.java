package com.lingvapps.quizword;

import de.marcreichelt.android.RealViewSwitcher;
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
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CardFragment extends Fragment {

    private int currentMode = CardLayout.MODE_SINGLE_SIDE;
    private int currentCard = 0;

    private CardSet cardSet = null;

    private ShakeListener shaker;

    static CardFragment newInstance(Integer set_id, String set_name) {
        CardFragment f = new CardFragment();

        Bundle args = new Bundle();
        args.putInt("set_id", set_id);
        args.putString("set_name", set_name);
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

        if (getArguments() != null) {
            readCards(getArguments().getInt("set_id"), getArguments()
                    .getString("set_name"));
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
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (cardSet == null) {
            return new View(getActivity());
        }

        View view = inflater.inflate(R.layout.card_fragment, container, false);
        ViewSwitcher switcher = (ViewSwitcher) view.findViewById(R.id.view_switcher);
        fillViewSwitcher(switcher, inflater);
        view.setBackgroundColor(Color.BLACK);

        setButtonListeners(view);
        updateCounterText(view);

        return view;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("currentMode", currentMode);
        outState.putInt("currentCard", currentCard);
    }

    private void setButtonListeners(View view) {
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
    }

    private void fillViewSwitcher(ViewSwitcher view) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fillViewSwitcher(view, inflater);
    }
    
    private void fillViewSwitcher(ViewSwitcher view, LayoutInflater inflater) {
        view.removeAllViews();
        for (Card card : cardSet) {
            CardLayout layout = new CardLayout(this.getActivity());
            switch (currentMode) {
            case CardLayout.MODE_DEFINITION_FIRST:
                layout.setFace(card.getDefinition());
                layout.setBack(card.getTerm());
                break;
            default:
                layout.setFace(card.getTerm());
                layout.setBack(card.getDefinition());
                break;
            }
            layout.setCurrentMode(currentMode);
            view.addView(layout);
        }

        view.setCurrentScreen(currentCard);
        view.setOnScreenSwitchListener(onScreenSwitchListener);
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
            fillViewSwitcher(getSwitcher());
            updateCounterText(getRootView());
            Toast.makeText(getActivity(), R.string.shuffled_message, Toast.LENGTH_LONG).show();
        }
    }

    @TargetApi(11)
    public void turnEffect() {
        final CardLayout view = (CardLayout) getSwitcher().getChildAt(currentCard);

        ObjectAnimator animFirst = ObjectAnimator.ofFloat(view, "rotation", 0.0f, 540.0f);
        animFirst.setInterpolator(new AccelerateInterpolator());
        animFirst.setDuration(500);

        final ObjectAnimator animSecond = ObjectAnimator.ofFloat(view, "rotation", 540.0f, 3*360.0f);
        animSecond.setDuration(500);
        animSecond.setInterpolator(new DecelerateInterpolator());
        view.setRotation(540.0f);
        

        animSecond.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                fillViewSwitcher(getSwitcher());
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
        Log.d("quizword", "switchModes");
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

        fillViewSwitcher(getSwitcher());
    }

    private ViewGroup getRootView() {
        FrameLayout frame = (FrameLayout) getView();
        return (ViewGroup) frame.getChildAt(0);
    }

    private ViewSwitcher getSwitcher() {
        return (ViewSwitcher) getRootView().getChildAt(0);
    }

    private void readCards(Integer setId, String setName) {
        RetrieveSetTask task = new RetrieveSetTask(getActivity());
        task.setOnPostExecuteListener(new RetrieveSetTask.OnPostExecuteListener<CardSet>() {
            public void onSuccess(CardSet set) {
                cardSet = set;
                refresh();
            }

            public void onFailure() {
            }
        });
        task.execute(setId.toString(), setName);
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

    private final RealViewSwitcher.OnScreenSwitchListener onScreenSwitchListener = new RealViewSwitcher.OnScreenSwitchListener() {
        public void onScreenSwitched(int screen) {
            currentCard = screen;
            updateCounterText(getRootView());
        }

    };
}
