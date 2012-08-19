package com.lingvapps.quizword;


import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

// TODO: create base class for tasks
class RetrieveMySetsTask extends HTTPTask<String, ArrayAdapter<String>> {

    public RetrieveMySetsTask(Context ctx) {
        super(ctx);
    }

    protected ArrayAdapter<String> doInBackground(String... params) {
        String user = Preferences.getInstance(context).getUserData("user_id");
        String token = Preferences.getInstance(context).getUserData("access_token");
        Log.d("quizlet", "loading my sets");
        try {
            JSONObject result = QuizletHTTP.requestMySets(token, user);
            if (result != null) {
                String[] values = new String[result.length()];
                for (int i = 0; i < result.length(); i++) {
                    values[i] = result.getJSONArray("sets").getJSONObject(i).getString("title");
                }
                return new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, android.R.id.text1, values);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}