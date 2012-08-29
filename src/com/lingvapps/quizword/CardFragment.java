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
import android.widget.Button;
import android.widget.TextView;

public class CardFragment extends Fragment {

    static final int MODE_BOTH_SIDES = 0;
    static final int MODE_SINGLE_SIDE = 1;
    
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
        View view = createViewSwitcher(inflater, container, savedInstanceState);
        //view.setRotationY(-90f);
        Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switchModes();
            }
        });
        return view;
    }

    private View createViewSwitcher(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (cardSet == null) {
            return new View(getActivity());
        }
        final ViewSwitcher view = new ViewSwitcher(getActivity().getApplicationContext());
        view.setBackgroundColor(Color.BLACK);
        for (Card card : cardSet) {
            CardLayout layout = new CardLayout(this.getActivity());
            layout.setTerm(card.getTerm());
            layout.setDefinition(card.getDefinition());
            view.addView(layout);
        }

        view.setOnScreenSwitchListener(onScreenSwitchListener);
        return view;
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
    
    
    private void switchModes() {
        Log.d("quizword", "switchModes");
        if (currentMode == MODE_BOTH_SIDES) {
            makeSingleSide();
        } else {
            makeBothSides();
        }
    }
    
    private void makeSingleSide() {
        currentMode = MODE_SINGLE_SIDE;
        findViewById(R.id.card_definition).setVisibility(View.GONE);
    }

    private void makeBothSides() {
        currentMode = MODE_BOTH_SIDES;
        findViewById(R.id.card_definition).setVisibility(View.VISIBLE);
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
