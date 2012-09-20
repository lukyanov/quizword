package com.lingvapps.quizword;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import android.util.Log;

public class GoogleHTTP {

    private static final String BASE_URL = "http://translate.google.com";

    public static ByteArrayBuffer requestTTS(String lang, String text) {
        try {
            String URL = BASE_URL + "/translate_tts?ie=UTF-8&tl=" + URLEncoder.encode(lang, "UTF-8") + "&q=" + URLEncoder.encode(text, "UTF-8");
            Log.d("google", URL);
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(URL);
            HttpResponse response = httpClient.execute(httpGet);
            return readBuffer(response.getEntity().getContent());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ByteArrayBuffer readBuffer(InputStream stream) {
        try {
            BufferedInputStream bis = new BufferedInputStream(stream);
            ByteArrayBuffer buf = new ByteArrayBuffer(1024);
            int current = 0;
            while ((current = bis.read()) != -1) {
                buf.append((byte) current);
            }
            return buf;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
