package com.lingvapps.quizword.tasks;

import org.apache.http.util.ByteArrayBuffer;

import com.lingvapps.quizword.utils.CacheManager;
import com.lingvapps.quizword.utils.GoogleHTTP;

import android.content.Context;

public class RetrieveSpeechTask extends BackgroundTask<String, String> {

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
            String filePath = context.getCacheDir() + "/" + fileName;
            if (!CacheManager.cacheExists(context, fileName)) {
                ByteArrayBuffer buf = GoogleHTTP.requestTTS(lang, text);
                CacheManager.cacheData(context, buf.toByteArray(), fileName);
            }
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
