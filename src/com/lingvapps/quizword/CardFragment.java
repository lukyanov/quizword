package com.lingvapps.quizword;

import de.marcreichelt.android.RealViewSwitcher;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class CardFragment extends Fragment {

    static final int MODE_BOTH_SIDES = 0;
    static final int MODE_TERM_SIDE = 1;
    static final int MODE_DEFINITION_SIDE = 2;
    
    private int currentMode = MODE_BOTH_SIDES;

    private CardSet cardSet = null;
    
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
        if (getArguments() != null) {
            readCards(getArguments().getInt("set_id"), getArguments().getString("set_name"));
        }
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

    private View createViewSwitcher(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewSwitcher view = new ViewSwitcher(getActivity().getApplicationContext());
        for (Card card : cardSet) {
            CardLayout layout = new CardLayout(this.getActivity());
            layout.setTerm(card.getTerm());
            layout.setDefinition(card.getDefinition());
            view.addView(layout);
        }

        view.setOnScreenSwitchListener(onScreenSwitchListener);
        
        return view;
    }
    
    public void switchModes() {
        Log.d("quizword", "switchModes");
        int currentSide = -1;
        switch (currentMode) {
        case MODE_BOTH_SIDES:
            currentMode = MODE_TERM_SIDE;
            currentSide = CardLayout.SIDE_TERM;
            break;
        case MODE_TERM_SIDE:
            currentMode = MODE_DEFINITION_SIDE;
            currentSide = CardLayout.SIDE_DEFINITION;
            break;
        case MODE_DEFINITION_SIDE:
            currentMode = MODE_BOTH_SIDES;
            currentSide = CardLayout.SIDE_BOTH;
        }
        FrameLayout frame = (FrameLayout) getView();
        ViewSwitcher switcher = (ViewSwitcher) frame.getChildAt(0);
        
        int count = switcher.getChildCount();
        CardLayout layout;
        for (int i = 0; i < count; i++) {
            layout = (CardLayout) switcher.getChildAt(i);
            layout.setCurrentSide(currentSide);
        }
    }

    private void readCards(Integer setId, String setName) {
        RetrieveSetTask task = new RetrieveSetTask(getActivity());
        task.setOnPostExecuteListener(new RetrieveSetTask.OnPostExecuteListener<CardSet>() {
            public void onSuccess(CardSet set) {
                cardSet = set;
                refresh();
            }
            public void onFailure() { }
        });
        task.execute(setId.toString(), setName);
    }

    private void refresh() {
        getFragmentManager().beginTransaction()
            .detach(this)
            .attach(this)
            .commit();
    }

    private final RealViewSwitcher.OnScreenSwitchListener onScreenSwitchListener = new RealViewSwitcher.OnScreenSwitchListener() {
        public void onScreenSwitched(int screen) {
            // this method is executed if a screen has been activated, i.e. the
            // screen is completely visible
            // and the animation has stopped (might be useful for removing /
            // adding new views)
            Log.d("RealViewSwitcher", "switched to screen: " + screen);
        }

    };
}
