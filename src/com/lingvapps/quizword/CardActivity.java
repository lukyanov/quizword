package com.lingvapps.quizword;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.marcreichelt.android.RealViewSwitcher;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        realViewSwitcher = new RealViewSwitcher(
                getApplicationContext());
        if (realViewSwitcher == null) {
            // renderCards();
        }
        //MainActivity.realViewSwitcher.setCurrentScreen(current_card); //
        setContentView(realViewSwitcher);
        renderCards();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_card, menu);
        return true;
    }

    private void renderCards() {
        // TODO: Add cache and use it when changing screen orientation
        try {
            QuizletReaderTask task = new QuizletReaderTask();
            task.setObserver(new QuizletReaderTask.Callback() {

                public void onFailure() {

                }

                public void onComplete(JSONObject JSON) {
                    try {
                        buildCards(JSON);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
            task.execute("https://api.quizlet.com/2.0/sets/12868186?client_id=Sb54cREqMM&whitespace=1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildCards(JSONObject JSON) throws JSONException {

        JSONArray terms = (JSONArray) JSON.get("terms");

        // add some views to it
        JSONObject t;
        for (int i = 0; i < terms.length(); i++) {
            t = terms.getJSONObject(i);
            RelativeLayout layout = (RelativeLayout) getLayoutInflater()
                    .inflate(R.layout.card, null);
            TextView textView1 = (TextView) layout.findViewById(R.id.card_term);
            TextView textView2 = (TextView) layout
                    .findViewById(R.id.card_definition);
            textView1.setText(t.getString("term"));
            textView2.setText(t.getString("definition"));
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // if (MainActivity.realViewSwitcher != null) {
        // outState.putInt("current_card",
        // MainActivity.realViewSwitcher.getCurrentScreen());
        // }
    }
}
