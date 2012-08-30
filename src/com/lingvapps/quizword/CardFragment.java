package com.lingvapps.quizword;

import de.marcreichelt.android.RealViewSwitcher;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CardFragment extends Fragment {

    static final int MODE_BOTH_SIDES = 0;
    static final int MODE_TERM_SIDE = 1;
    static final int MODE_DEFINITION_SIDE = 2;

    private int currentMode = MODE_BOTH_SIDES;
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

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (cardSet == null) {
            return new View(getActivity());
        }
        View view = createViewSwitcher(inflater, container, savedInstanceState);
        view.setBackgroundColor(Color.BLACK);
        return view;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("currentMode", currentMode);
        outState.putInt("currentCard", currentCard);
    }

    private View createViewSwitcher(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        ViewSwitcher view = new ViewSwitcher(getActivity()
                .getApplicationContext());
        for (Card card : cardSet) {
            CardLayout layout = new CardLayout(this.getActivity());
            layout.setTerm(card.getTerm());
            layout.setDefinition(card.getDefinition());
            layout.setCurrentSide(getInitialCardSide());
            view.addView(layout);
        }
        
        view.setCurrentScreen(currentCard);
        view.setOnScreenSwitchListener(onScreenSwitchListener);

        return view;
    }
    
    private int getInitialCardSide() {
        switch (currentMode) {
        case MODE_TERM_SIDE:
            return CardLayout.SIDE_TERM;
        case MODE_DEFINITION_SIDE:
            return CardLayout.SIDE_DEFINITION;
        case MODE_BOTH_SIDES:
            return CardLayout.SIDE_BOTH;
        }
        return -1;
    }
    
    @TargetApi(11)
    public void shuffleCards() {
        Vibrator vibe = (Vibrator) getActivity().getSystemService(
                Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);
        ViewSwitcher switcher = getSwitcher();
        CardLayout layout = (CardLayout) switcher.getChildAt(switcher.getCurrentScreen());
        layout.setOnTurnListener(new CardLayout.OnTurnListener() {
            public void onStop() {
                cardSet.shuffle();
                refresh();
                Toast.makeText(getActivity(), R.string.shuffled_message, Toast.LENGTH_LONG).show();
            }
        });
        layout.turn();
    }

    public void switchModes() {
        Log.d("quizword", "switchModes");
        switch (currentMode) {
        case MODE_BOTH_SIDES:
            currentMode = MODE_TERM_SIDE;
            break;
        case MODE_TERM_SIDE:
            currentMode = MODE_DEFINITION_SIDE;
            break;
        case MODE_DEFINITION_SIDE:
            currentMode = MODE_BOTH_SIDES;
            break;
        }

        ViewSwitcher switcher = getSwitcher();
        int count = switcher.getChildCount();
        CardLayout layout;
        for (int i = 0; i < count; i++) {
            layout = (CardLayout) switcher.getChildAt(i);
            layout.setCurrentSide(getInitialCardSide());
        }
    }
    
    private ViewSwitcher getSwitcher() {
        FrameLayout frame = (FrameLayout) getView();
        return (ViewSwitcher) frame.getChildAt(0);
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

    private final RealViewSwitcher.OnScreenSwitchListener onScreenSwitchListener = new RealViewSwitcher.OnScreenSwitchListener() {
        public void onScreenSwitched(int screen) {
            currentCard = screen;
        }

    };
}
