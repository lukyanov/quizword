package com.lingvapps.quizword;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

class RetrieveAccessTokenTask extends AsyncTask<String, Void, Void> {
    
    private Activity activity = null;
    
    public RetrieveAccessTokenTask(Activity activity) {
        this.activity = activity;
    }

    protected Void doInBackground(String... params) {
        String verifier = params[0];
        String redirectURI = params[1];
        try {
            // Get the token
            JSONObject token = QuizletHTTP.requestAuthToken(verifier, redirectURI);
            
            Preferences prefs = Preferences.getInstance();
            
            Map<String, Object> data = new HashMap<String, Object>();
 
            data.put("access_token", token.getString("access_token"));
            data.put("expires_in", Integer.valueOf(token.getInt("expires_in")));
            data.put("scope", token.getString("scope"));
            data.put("user_id", token.getString("user_id"));

            prefs.saveUserData(activity, data);
            Log.d("quizlet", "preferences saved");
            
            Intent intent = activity.getIntent();
            activity.finish();
            activity.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
