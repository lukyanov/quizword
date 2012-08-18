package com.lingvapps.quizword;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    
    private static Preferences instance = null;
    
    public static void init(Context ctx) {
        if (instance == null) {
            instance = new Preferences();
        }
    }
    
    public static Preferences getInstance() {
        return instance;
    }
    
    public void saveUserData(Context context, Map<String, Object> data) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, (Integer) data.get("expires_in"));
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        String dateString = format.format(cal.getTime());

        prefs.edit()
            .putString("quizlet_access_token", (String) data.get("access_token"))
            .putString("quizlet_token_expire_time", dateString)
            .putString("quizlet_scope", (String) data.get("scope"))
            .putString("quizlet_user_id", (String) data.get("user_id"))
            .commit();
    }
    
    public void clearUserData(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
            .remove("quizlet_access_token")
            .remove("quizlet_token_expire_time")
            .remove("quizlet_scope")
            .remove("quizlet_user_id")
            .commit();
    }

    public String getUserData(Context context, String key) {
        return getUserData(context, key, null);
    }
    
    public String getUserData(Context context, String key, String defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String value = prefs.getString("quizlet_" + key, defaultValue);
        if (key == "access_token" && value != null) {
            try {
                String expireString = prefs.getString("quizlet_token_expire_time", null);
                SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
                Date exipreDate = format.parse(expireString);
                if ((new Date()).before(exipreDate)) {
                    return value;
                } else {
                    return null;
                }
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        } else {
            return value;
        }
    }
}
