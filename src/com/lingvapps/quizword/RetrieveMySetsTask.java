package com.lingvapps.quizword;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

class RetrieveMySetsTask extends HTTPTask<String, ArrayAdapter<CardSet>> {

    public RetrieveMySetsTask(Context ctx) {
        super(ctx);
    }

    protected ArrayAdapter<CardSet> doInBackground(String... params) {
        String user = Preferences.getInstance(context).getUserData("user_id");
        String token = Preferences.getInstance(context).getUserData(
                "access_token");
        Log.d("quizlet", "loading my sets");
        try {
            JSONObject result = QuizletHTTP.requestMySets(token, user);
            if (result != null) {
                JSONArray ss = result.getJSONArray("sets");
                CardSet[] sets = new CardSet[ss.length()];
                for (int i = 0; i < ss.length(); i++) {
                    JSONObject obj = ss.getJSONObject(i);
                    sets[i] = new CardSet(obj.getInt("id"),
                            obj.getString("title"), obj.getInt("term_count"));
                }
                return new ArrayAdapter<CardSet>(context,
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1, sets);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}