package com.lingvapps.quizword;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

class RetrieveAccessTokenTask extends AsyncTask<String, Void, JSONObject> {

    private Activity activity = null;
    private ProgressDialog progressDialog = null;
    private OnPostExecuteListener onPostExecuteListener = null;

    public interface OnPostExecuteListener {
        public void onSuccess();
        public void onFailure();
    }

    public RetrieveAccessTokenTask(Activity activity) {
        this.activity = activity;
    }

    public void setOnPostExecuteListener(OnPostExecuteListener listener) {
        this.onPostExecuteListener = listener;
    }

    protected void onPreExecute() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Authorizing...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    protected JSONObject doInBackground(String... params) {
        String verifier = params[0];
        String redirectURI = params[1];
        return QuizletHTTP.requestAuthToken(verifier, redirectURI);
    }

    protected void onPostExecute(JSONObject token) {
        if (token != null) {
            try {
                Preferences prefs = Preferences.getInstance(activity);

                Map<String, Object> data = new HashMap<String, Object>();

                data.put("access_token", token.getString("access_token"));
                data.put("expires_in", Integer.valueOf(token.getInt("expires_in")));
                data.put("scope", token.getString("scope"));
                data.put("user_id", token.getString("user_id"));

                prefs.saveUserData(data);
                Log.d("quizlet", "preferences saved");

                onPostExecuteListener.onSuccess();
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
