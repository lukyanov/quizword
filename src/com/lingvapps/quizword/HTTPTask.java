package com.lingvapps.quizword;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

abstract class HTTPTask<T1, T2> extends AsyncTask<T1, Void, T2> {

    protected Context context = null;
    protected ProgressDialog progressDialog = null;
    protected OnPostExecuteListener<T2> onPostExecuteListener = null;
    protected String message = "Loading...";
    
    public interface OnPostExecuteListener<T> {
        public void onSuccess(T result);
        public void onFailure();
    }

    public HTTPTask(Context ctx) {
        super();
        this.context = ctx;
    }

    public void setOnPostExecuteListener(OnPostExecuteListener<T2> listener) {
        this.onPostExecuteListener = listener;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }

    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    protected void onPostExecute(T2 result) {
        if (result != null) {
            try {
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