package com.lingvapps.quizword;

import java.util.Calendar;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

class RetrieveAccessTokenTask extends AsyncTask<String, Void, Void> {
    
    private Context context = null;
    
    public RetrieveAccessTokenTask(Context context) {
        this.context = context;
    }

    protected Void doInBackground(String... params) {
        String verifier = params[0];
        String redirectURI = params[1];
        try {
            // Get the token
            JSONObject token = QuizletHTTP.requestAuthToken(verifier, redirectURI);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

            SharedPreferences.Editor editor = prefs.edit();

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, token.getInt("expires_in"));
            Log.d("quizlet", "token expire time: " + cal.getTime().toString());

            editor
                .putString("quizlet_access_token", token.getString("access_token"))
                .putString("quizlet_token_expire_time", cal.getTime().toString())
                .putString("quizlet_scope", token.getString("scope"))
                .putString("quizlet_user_id", token.getString("user_id"));

            editor.commit();
            Log.d("quizlet", "preferences saved");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
