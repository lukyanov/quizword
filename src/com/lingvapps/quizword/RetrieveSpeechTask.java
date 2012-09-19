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
        String id = params[0];
        String text = params[1];
        String lang = params[2];
        try {
            String fileName = "tts_" + id + "_" + lang + ".mp3";
            if (!CacheManager.cacheExists(context, fileName)) {
                ByteArrayBuffer buf = GoogleHTTP.requestTTS(lang, text);
                CacheManager.cacheData(context, buf.toByteArray(), fileName);
            }
            return context.getCacheDir() + "/" + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
