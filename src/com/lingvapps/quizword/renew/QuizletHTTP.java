package com.lingvapps.quizword.renew;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Base64;

public class QuizletHTTP {

    public static final String REDIRECT_URI = "quizword:/after_auth";
    
    private static final String BASE_URL = "https://quizlet.com";
    private static final String API_BASE_URL = "https://api.quizlet.com";
    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String SECRET_KEY = "SECRET_KEY";

    public static String getAuthorizitionURL(String scope, String state,
            String redirectURI) {
        try {
            return BASE_URL + "/authorize/?" + "scope="
                    + URLEncoder.encode(scope, "UTF-8") + "&" + "client_id="
                    + URLEncoder.encode(CLIENT_ID, "UTF-8") + "&"
                    + "response_type=code&" + "state="
                    + URLEncoder.encode(state, "UTF-8") + "&" + "redirect_uri="
                    + URLEncoder.encode(redirectURI, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return BASE_URL;
        }
    }

    public static JSONObject requestAuthToken(String code, String redirectURI) {
        HttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(API_BASE_URL + "/oauth/token");
        List<NameValuePair> params = new ArrayList<NameValuePair>(3);
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("code", code));
        params.add(new BasicNameValuePair("redirect_uri", redirectURI));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            String basicString = Base64.encodeToString(
                    new String(CLIENT_ID + ":" + SECRET_KEY).getBytes(),
                    Base64.DEFAULT).trim();

            httpPost.addHeader("Authorization", "Basic " + basicString);

            HttpResponse response = httpClient.execute(httpPost);
            String responseString = readResponse(response.getEntity()
                    .getContent());

            return responseToJSONObject(responseString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject requestMySets(String token, String user) {
        String response = request(API_BASE_URL + "/2.0/users/" + user, token);
        return responseToJSONObject(response);
    }

    public static JSONArray requestMyGroups(String token, String user) {
        String response = request(API_BASE_URL + "/2.0/users/" + user + "/groups", token);
        return responseToJSONArray(response);
    }

    public static JSONArray requestMySetsFullDetails(String token, String user) {
        String response = request(API_BASE_URL + "/2.0/users/" + user + "/sets", token);
        return responseToJSONArray(response);
    }

    public static JSONArray requestFavoriteSets(String token, String user) {
        String response = request(API_BASE_URL + "/2.0/users/" + user + "/favorites", token);
        return responseToJSONArray(response);
    }

    public static JSONObject requestSet(String token, Integer setId) {
        String response = request(API_BASE_URL + "/2.0/sets/" + setId.toString(), token);
        return responseToJSONObject(response);
    }

    // TODO: Make it generate exceptions
    private static String request(String URL, String token) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {
            if (token != null) {
                httpGet.addHeader("Authorization", "Bearer " + token);
            }
            HttpResponse response = httpClient.execute(httpGet);
            String responseString = readResponse(response.getEntity()
                    .getContent());
            return responseString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JSONObject responseToJSONObject(String response) {
        try {
            JSONObject JSON = new JSONObject(response);
            if (JSON.isNull("error")) {
                return JSON;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JSONArray responseToJSONArray(String response) {
        try {
            JSONArray JSON = new JSONArray(response);
            return JSON;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getHeadersAsString(Header[] headers) {
        StringBuffer s = new StringBuffer();
        for (Header h : headers)
            s.append(h.toString() + "\n");
        return s.toString();
    }

    private static String readResponse(InputStream stream) {
        String line;
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(stream));
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
