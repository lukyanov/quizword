package com.lingvapps.quizword;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

class RetrieveAccessTokenTask extends HTTPTask<String, JSONObject> {

    public RetrieveAccessTokenTask(Context ctx) {
        super(ctx);
    }

    protected JSONObject doInBackground(String... params) {
        String verifier = params[0];
        String redirectURI = params[1];
        return QuizletHTTP.requestAuthToken(verifier, redirectURI);
    }

    protected void onPostExecute(JSONObject token) {
        if (token != null) {
            try {
                Preferences prefs = Preferences.getInstance(context);

                Map<String, Object> data = new HashMap<String, Object>();

                data.put("access_token", token.getString("access_token"));
                data.put("expires_in", Integer.valueOf(token.getInt("expires_in")));
                data.put("scope", token.getString("scope"));
                data.put("user_id", token.getString("user_id"));

                prefs.saveUserData(data);
                Log.d("quizlet", "preferences saved");

                onPostExecuteListener.onSuccess(token);
            } catch (Exception e) {
                onPostExecuteListener.onFailure();
                e.printStackTrace();
            }
        } else {
            onPostExecuteListener.onFailure();
        }

        progressDialog.dismiss();
    }
}
