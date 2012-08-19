package com.lingvapps.quizword;


import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

// TODO: create base class for tasks
class RetrieveSetTask extends HTTPTask<String, JSONObject> {

    public RetrieveSetTask(Context ctx) {
        super(ctx);
    }

    protected JSONObject doInBackground(String... params) {
        String setId = params[0];
        String token = Preferences.getInstance(context).getUserData("access_token");
        Log.d("quizlet", "loading my sets");
        try {
            JSONObject result = QuizletHTTP.requestSet(token, Integer.parseInt(setId));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
