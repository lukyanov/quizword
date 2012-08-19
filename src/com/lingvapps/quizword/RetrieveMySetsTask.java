package com.lingvapps.quizword;


import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

// TODO: create base class for tasks
class RetrieveMySetsTask extends AsyncTask<String, Void, ArrayAdapter<String>> {

    private Activity activity = null;
    private ProgressDialog progressDialog = null;
    private OnPostExecuteListener onPostExecuteListener = null;

    public interface OnPostExecuteListener {
        public void onSuccess(ArrayAdapter<String> adapter);
        public void onFailure();
    }

    public RetrieveMySetsTask(Activity activity) {
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

    protected ArrayAdapter<String> doInBackground(String... params) {
        String user = Preferences.getInstance(activity).getUserData("user_id");
        String token = Preferences.getInstance(activity).getUserData("access_token");
        Log.d("quizlet", "loading my sets");
        try {
            JSONObject result = QuizletHTTP.requestMySets(token, user);
            if (result != null) {
                String[] values = new String[result.length()];
                for (int i = 0; i < result.length(); i++) {
                    values[i] = result.getJSONArray("sets").getJSONObject(i).getString("title");
                }
                return new ArrayAdapter<String>(activity,
                        android.R.layout.simple_list_item_1, android.R.id.text1, values);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(ArrayAdapter<String> result) {
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
