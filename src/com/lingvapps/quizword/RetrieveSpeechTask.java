package com.lingvapps.quizword;

import org.apache.http.util.ByteArrayBuffer;
import android.content.Context;

class RetrieveSpeechTask extends BackgroundTask<String, String> {

    public RetrieveSpeechTask(Context ctx) {
        super(ctx);
    }

    @Override
    protected void onPreExecute() {
        
    }

    @Override
    protected void onPostExecute(String result) {
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
    }

    protected String doInBackground(String... params) {
        String lang = params[0];
        String text = params[1];
        String id = params[2];
        try {
            ByteArrayBuffer buf = GoogleHTTP.requestTTS(lang, text);
            String fileName = "tts_" + lang + "_" + id + ".mp3";
            CacheManager.cacheData(context, buf.toByteArray(), fileName);
            return context.getCacheDir() + "/" + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
