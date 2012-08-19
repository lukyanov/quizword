package com.lingvapps.quizword;

import de.marcreichelt.android.RealViewSwitcher;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CardActivity extends Activity {
    private RealViewSwitcher realViewSwitcher = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Bundle extras = getIntent().getExtras();
        renderCards(extras.getInt("set_id"), extras.getString("set_name"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_card, menu);
        return true;
    }

    private void renderCards(Integer setId, String setName) {
        // TODO: Add cache and use it when changing screen orientation
        RetrieveSetTask task = new RetrieveSetTask(this);
        task.setOnPostExecuteListener(new RetrieveSetTask.OnPostExecuteListener<CardSet>() {

            public void onSuccess(CardSet set) {
                realViewSwitcher = new RealViewSwitcher(getApplicationContext());
                setContentView(realViewSwitcher);
                fillViewSwitcher(set);
                // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            public void onFailure() {

            }

        });
        task.execute(setId.toString());
    }

    private void fillViewSwitcher(CardSet set) {

        for (Card card : set) {
            RelativeLayout layout = (RelativeLayout) getLayoutInflater()
                    .inflate(R.layout.card, null);
            TextView textView1 = (TextView) layout.findViewById(R.id.card_term);
            TextView textView2 = (TextView) layout
                    .findViewById(R.id.card_definition);
            textView1.setText(card.getTerm());
            textView2.setText(card.getDefinition());
            // textView.setTextSize(30);
            // textView.setTextColor(Color.BLACK);
            // textView.setGravity(Gravity.CENTER);
            realViewSwitcher.addView(layout);
        }

        realViewSwitcher.setOnScreenSwitchListener(onScreenSwitchListener);
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
