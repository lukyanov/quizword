package com.lingvapps.quizword.tasks;


import com.lingvapps.quizword.renew.R;
import com.lingvapps.quizword.renew.R.string;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

abstract class BackgroundTask<T1, T2> extends AsyncTask<T1, Void, T2> {

    protected Context context = null;
    protected ProgressDialog progressDialog = null;
    protected OnPostExecuteListener<T2> onPostExecuteListener = null;
    protected int messageId = R.string.load_message;
    
    public interface OnPostExecuteListener<T> {
        public void onSuccess(T result);
        public void onFailure();
    }

    public BackgroundTask(Context ctx) {
        super();
        this.context = ctx;
    }

    public void setOnPostExecuteListener(OnPostExecuteListener<T2> listener) {
        this.onPostExecuteListener = listener;
    }
    
    public void setMessage(int messageId) {
        this.messageId = messageId;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(messageId));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
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
