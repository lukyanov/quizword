package com.lingvapps.quizword;


import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

class RetrieveSetTask extends HTTPTask<String, CardSet> {

    public RetrieveSetTask(Context ctx) {
        super(ctx);
    }

    protected CardSet doInBackground(String... params) {
        String setId = params[0];
        String token = Preferences.getInstance(context).getUserData("access_token");
        Log.d("quizlet", "loading my sets");
        try {
            JSONObject result = QuizletHTTP.requestSet(token, Integer.parseInt(setId));
            if (result != null) {
                CardSet set = new CardSet(result.getInt("id"), result.getString("title"));
                JSONArray terms = result.getJSONArray("terms");
                JSONObject t;
                for (int i = 0; i < result.length(); i++) {
                    t = terms.getJSONObject(i);
                    set.addCard(new Card(t.getString("term"), t.getString("definition")));
                }
                return set;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
