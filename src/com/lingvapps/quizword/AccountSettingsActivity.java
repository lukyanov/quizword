package com.lingvapps.quizword;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.json.JSONObject;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class AccountSettingsActivity extends FragmentActivity {
    
    static ListView menuListView;
    
    private static String quizletAuthState = new BigInteger(32, new SecureRandom()).toString();
    private String redirectURI;
    
    private Boolean flagAuthErrorOccured = false;

    @TargetApi(11)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        redirectURI = getString(R.string.app_id) + ":/after_auth";
        Log.d("auth", "my state: " + quizletAuthState);
        
        setContentView(R.layout.account_settings);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final ActionBar bar = getActionBar();
            bar.setDisplayHomeAsUpEnabled(true);
        }
        
        String[] values;
        ArrayAdapter<String> adapter;
        
        Preferences prefs = Preferences.getInstance(this);
        String token = prefs.getUserData("access_token");
        
        if (token != null) {
            values = new String[] {
                    "Logout (" + prefs.getUserData("user_id", "") + ")",
                    "Sync"
            };
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, values);
        } else {
            values = new String[] {
                    "Login / Register"
            };
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, values);
        }
        menuListView = (ListView) findViewById(R.id.account_settings_menu);
        menuListView.setAdapter(adapter);
        menuListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                onListItemClick((ListView) l, v, position, id);
            }
        });
    }
    
    private void startMainMenuActivity() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    
    @Override
    public void onBackPressed() {
        startMainMenuActivity();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startMainMenuActivity();
                return true;
            default:
                return false;
        }
    }
    
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        flagAuthErrorOccured = false;

        Uri data = intent.getData();
        if (data == null) {
            return;
        }
        Log.d("quizlet", "data: " + data.toString());

        if (data.getPath().equals("/after_auth")) {
            Log.d("quizlet", "my state: " + quizletAuthState +
                    ", request state: " + data.getQueryParameter("state"));
            if (data.getQueryParameter("state").equals(quizletAuthState) &&
                    data.getQueryParameter("error") == null) {
                String code = intent.getData().getQueryParameter("code");
                RetrieveAccessTokenTask task = new RetrieveAccessTokenTask(this);
                task.setMessage("Authorizing...");
                task.setOnPostExecuteListener(new RetrieveAccessTokenTask.OnPostExecuteListener<JSONObject>() {
                    public void onSuccess(JSONObject result) {
                        restart();
                    }
                    public void onFailure() {
                        showErrorMessage(R.string.auth_error_title, R.string.auth_error_message);
                    }
                });
                task.execute(code, redirectURI);
            } else {
                flagAuthErrorOccured = true;
            }
        }
    }
    
    public void restart() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
        overridePendingTransition(0,0);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (flagAuthErrorOccured) {
            showErrorMessage(R.string.auth_error_title, R.string.auth_error_message);
        }
    }
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Preferences prefs = Preferences.getInstance(this);
        String token = prefs.getUserData("access_token");
        switch (position) {
        case 0:
            if (token == null) {            
                Intent intent;
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                String authURL = QuizletHTTP.getAuthorizitionURL("read",
                        quizletAuthState, redirectURI);
                Log.d("quizlet", "Quizlet Auth URL: " + authURL);
                intent.setData(Uri.parse(authURL));
                startActivity(intent);
            } else {
                prefs.clearUserData();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                overridePendingTransition(0,0);
            }
            break;
        case 1:
            if (token != null) {
                SyncSetsTask task = new SyncSetsTask(this);
                task.setMessage("Syncing...");
                task.setOnPostExecuteListener(new SyncSetsTask.OnPostExecuteListener<Boolean>() {
                    public void onSuccess(Boolean result) {
                        restart();
                        Toast.makeText(getApplicationContext(), "Synced",
                                Toast.LENGTH_LONG).show();
                    }
                    public void onFailure() {
                        showErrorMessage(R.string.sync_error_title, R.string.sync_error_message);
                    }
                });
                task.execute();
            }
            break;
        default:
            String item = (String) menuListView.getAdapter().getItem(position);
            Toast.makeText(getApplicationContext(), item + " selected",
                    Toast.LENGTH_LONG).show();
        }
    }
    
    protected void showErrorMessage(int title, int message) {
        DialogFragment newFragment = AlertDialogFragment.newInstance(title, message);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }
}
