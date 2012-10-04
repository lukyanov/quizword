package com.lingvapps.quizword.renew;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.json.JSONObject;

import com.lingvapps.quizword.R;

import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AccountSettingsActivity extends ListMenuActivity {
    
    private static String quizletAuthState = new BigInteger(32, new SecureRandom()).toString();
    
    private Boolean flagAuthErrorOccured = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBar();
        drawMenuList();
    }

    private void drawMenuList() {
        Preferences prefs = Preferences.getInstance(this);
        String token = prefs.getUserData("access_token");
        
        ArrayAdapter<CharSequence> adapter;
        
        if (token != null) {
            String[] array = getResources().getStringArray(R.array.settings_menu_logged_in);
            array[0] = array[0] + " (" + prefs.getUserData("user_id", "") + ")";
            adapter = new ArrayAdapter<CharSequence>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, array);
        } else {
            adapter = ArrayAdapter.createFromResource(this,
                    R.array.settings_menu, android.R.layout.simple_list_item_1);
        }
        
        drawMenuList(adapter);
    }
    
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        flagAuthErrorOccured = false;

        Uri data = intent.getData();
        if (data == null) {
            return;
        }

        if (data.getPath().equals("/after_auth")) {
            if (data.getQueryParameter("state").equals(quizletAuthState) &&
                    data.getQueryParameter("error") == null) {
                String code = intent.getData().getQueryParameter("code");
                retrieveAccessToken(code);
            } else {
                flagAuthErrorOccured = true;
            }
        }
    }
    
    private void retrieveAccessToken(String code) {
        RetrieveAccessTokenTask task = new RetrieveAccessTokenTask(this);
        task.setMessage(R.string.authorizing_message);
        task.setOnPostExecuteListener(new RetrieveAccessTokenTask.OnPostExecuteListener<JSONObject>() {
            public void onSuccess(JSONObject result) {
                drawMenuList();
            }
            public void onFailure() {
                showErrorMessage(R.string.auth_error_title, R.string.auth_error_message);
            }
        });
        task.execute(code, QuizletHTTP.REDIRECT_URI);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (flagAuthErrorOccured) {
            showErrorMessage(R.string.auth_error_title, R.string.auth_error_message);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Preferences prefs = Preferences.getInstance(this);
        String token = prefs.getUserData("access_token");
        switch (position) {
        case 0:
            if (token == null) {
                login();
            } else {
                logout();
            }
            break;
        case 1:
            if (token != null) {
                executeSyncTask();
            }
            break;
        }
    }
    
    protected void login() {
        Intent intent;
        intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        String authURL = QuizletHTTP.getAuthorizitionURL("read",
                quizletAuthState, QuizletHTTP.REDIRECT_URI);
        intent.setData(Uri.parse(authURL));
        startActivity(intent);
    }
    
    protected void logout() {
        // TODO: move deletion to Utils?
        Preferences prefs = Preferences.getInstance(this);
        prefs.clearUserData();
        prefs.clearDataSyncedFlagAll();
        LocalStorageHelper storageHelper = new LocalStorageHelper(this.getApplicationContext());
        storageHelper.clear_all();
        CacheManager.clearCache(this);
        drawMenuList();
    }
    
    protected void executeSyncTask() {
        SyncSetsTask task = new SyncSetsTask(this);
        task.setOnPostExecuteListener(new SyncSetsTask.OnPostExecuteListener<Boolean>() {
            public void onSuccess(Boolean result) {
                drawMenuList();
            }
            public void onFailure() {
                showErrorMessage(R.string.sync_error_title, R.string.sync_error_message);
            }
        });
        task.execute(Preferences.SELECTION_ALL_SETS);
    }
}
