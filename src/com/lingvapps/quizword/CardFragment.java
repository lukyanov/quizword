package com.lingvapps.quizword;

import de.marcreichelt.android.RealViewSwitcher;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CardFragment extends Fragment {
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
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return createViewSwitcher(inflater, container, savedInstanceState);
    }

    private View createViewSwitcher(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (cardSet == null) {
            return new View(getActivity());
        }
        RealViewSwitcher view = new RealViewSwitcher(getActivity().getApplicationContext());
        for (Card card : cardSet) {
            RelativeLayout layout = (RelativeLayout) inflater
                    .inflate(R.layout.card_fragment, container, false);
            TextView textView1 = (TextView) layout.findViewById(R.id.card_term);
            TextView textView2 = (TextView) layout
                    .findViewById(R.id.card_definition);
            textView1.setText(card.getTerm());
            textView2.setText(card.getDefinition());
            // textView.setTextSize(30);
            // textView.setTextColor(Color.BLACK);
            // textView.setGravity(Gravity.CENTER);
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

    private final RealViewSwitcher.OnScreenSwitchListener onScreenSwitchListener = new RealViewSwitcher.OnScreenSwitchListener() {
        public void onScreenSwitched(int screen) {
            // TODO: use getUserVisibleHint
            // this method is executed if a screen has been activated, i.e. the
            // screen is completely visible
            // and the animation has stopped (might be useful for removing /
            // adding new views)
            Log.d("RealViewSwitcher", "switched to screen: " + screen);
        }

    };
}
