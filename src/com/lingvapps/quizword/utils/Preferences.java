package com.lingvapps.quizword.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

    public static final int SELECTION_ALL_SETS = 0;
    public static final int SELECTION_MY_SETS = 1;
    public static final int SELECTION_MY_FOLDERS = 2;
    public static final int SELECTION_MY_CLASSES = 3;
    public static final int SELECTION_FAVORITE_SETS = 4;

    private static Preferences instance = null;

    private Context context = null;

    public static Preferences getInstance(Context ctx) {
        if (instance == null) {
            instance = new Preferences(ctx);
        }
        return instance;
    }

    private Preferences(Context ctx) {
        context = ctx.getApplicationContext();
    }

    public void saveUserData(Map<String, Object> data) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, (Integer) data.get("expires_in"));
        SimpleDateFormat format = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z");
        String dateString = format.format(cal.getTime());

        prefs.edit()
                .putString("quizlet_access_token",
                        (String) data.get("access_token"))
                .putString("quizlet_token_expire_time", dateString)
                .putString("quizlet_scope", (String) data.get("scope"))
                .putString("quizlet_user_id", (String) data.get("user_id"))
                .commit();
    }

    public void clearUserData() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        prefs.edit().remove("quizlet_access_token")
                .remove("quizlet_token_expire_time").remove("quizlet_scope")
                .remove("quizlet_user_id").commit();
    }

    public String getUserData(String key) {
        return getUserData(key, null);
    }

    public String getUserData(String key, String defaultValue) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String value = prefs.getString("quizlet_" + key, defaultValue);
        if (key == "access_token" && value != null) {
            try {
                String expireString = prefs.getString(
                        "quizlet_token_expire_time", null);
                SimpleDateFormat format = new SimpleDateFormat(
                        "EEE, dd MMM yyyy HH:mm:ss z");
                Date exipreDate = format.parse(expireString);
                if ((new Date()).before(exipreDate)) {
                    return value;
                } else {
                    return null;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return value;
        }
    }

    public void setDataSyncedFlag(int selection) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(getSyncedKey(selection), true).commit();
    }

    public Boolean isDataSynced(int selection) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return prefs.getBoolean(getSyncedKey(selection), false);
    }

    public void clearDataSyncedFlag(int selection) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        prefs.edit().remove(getSyncedKey(selection)).commit();
    }

    public void clearDataSyncedFlagAll() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        prefs.edit()
            .remove(getSyncedKey(SELECTION_MY_SETS))
            .remove(getSyncedKey(SELECTION_MY_FOLDERS))
            .remove(getSyncedKey(SELECTION_MY_CLASSES))
            .remove(getSyncedKey(SELECTION_FAVORITE_SETS))
            .remove(getSyncedKey(SELECTION_ALL_SETS))
            .commit();
    }

    public void setHideTipsFlag() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        prefs.edit().putBoolean("hide_quick_tips", true).commit();
    }

    public Boolean isTipsHidden() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return prefs.getBoolean("hide_quick_tips", false);
    }

    private String getSyncedKey(int selection) {
        String key;
        switch (selection) {
            case SELECTION_MY_SETS:
                key = "synced_my_sets";
                break;
            case SELECTION_MY_FOLDERS:
                key = "synced_my_folders";
                break;
            case SELECTION_MY_CLASSES:
                key = "synced_my_classes";
                break;
            case SELECTION_FAVORITE_SETS:
                key = "synced_favorite_sets";
                break;
            default:
                key = "synced_all";
        }
        return key;
    }
}
