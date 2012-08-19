package com.lingvapps.quizword;


import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

// TODO: create base class for tasks
class RetrieveSetTask extends AsyncTask<String, Void, JSONObject> {

    private Activity activity = null;
    private ProgressDialog progressDialog = null;
    private OnPostExecuteListener onPostExecuteListener = null;

    public interface OnPostExecuteListener {
        public void onSuccess(JSONObject result);
        public void onFailure();
    }

    public RetrieveSetTask(Activity activity) {
        this.activity = activity;
    }

    public void setOnPostExecuteListener(OnPostExecuteListener listener) {
        this.onPostExecuteListener = listener;
    }

    protected void onPreExecute() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    protected JSONObject doInBackground(String... params) {
        String token = params[0];
        String setId = params[1];
        Log.d("quizlet", "loading my sets");
        try {
            JSONObject result = QuizletHTTP.requestSet(token, Integer.parseInt(setId));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(JSONObject result) {
        if (result != null) {
            try {
                Log.d("quizlet", "my sets loaded");
                onPostExecuteListener.onSuccess(result);
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
