package com.lingvapps.quizword;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.marcreichelt.android.RealViewSwitcher;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Useful URLs:
 * - http://www.vogella.com/articles/AndroidListView/article.html#listview
 * - http://megadarja.blogspot.com/2010/07/android.html
 *
 * TODO:
 * - final
 * - Activity vs ListActivity
 * - Callbacks
 * - new Object() { ... }
 */

public class MainMenuActivity extends ListActivity {
    private static RealViewSwitcher realViewSwitcher = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // if (null == savedInstanceState) {
        setContentView(R.layout.main_menu);
        String[] values = new String[] { "View My Sets", "Find Public Sets",
                "Account Settings" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        setListAdapter(adapter);
        // } else {
        /*
         * requestWindowFeature(Window.FEATURE_NO_TITLE);
         * getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
         * WindowManager.LayoutParams.FLAG_FULLSCREEN); if
         * (MainMenuActivity.realViewSwitcher == null) { // renderCards(); }
         * //MainActivity.realViewSwitcher.setCurrentScreen(current_card); //
         * TODO: take control to another activity
         * setContentView(MainMenuActivity.realViewSwitcher);
         */
        // }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (position == 0) {
            // renderCards();
            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Intent myIntent = new Intent(l.getContext(), CardActivity.class);
            startActivityForResult(myIntent, 0);
        } else {
            String item = (String) this.getListAdapter().getItem(position);
            Toast.makeText(getApplicationContext(), item + " selected",
                    Toast.LENGTH_LONG).show();
        }
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

        MainMenuActivity.realViewSwitcher = new RealViewSwitcher(
                getApplicationContext());

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
            MainMenuActivity.realViewSwitcher.addView(layout);
        }

        MainMenuActivity.realViewSwitcher
                .setOnScreenSwitchListener(onScreenSwitchListener);
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
