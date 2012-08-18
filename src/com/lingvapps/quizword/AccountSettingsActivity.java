package com.lingvapps.quizword;

import java.math.BigInteger;
import java.security.SecureRandom;
import android.net.Uri;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class AccountSettingsActivity extends ListActivity {
    
    static final int DIALOG_ERROR_ID = 0;
    
    private static String quizletAuthState = new BigInteger(32, new SecureRandom()).toString();
    private String redirectURI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        redirectURI = getString(R.string.app_id) + ":/after_auth";
        Log.d("auth", "my state: " + quizletAuthState);
        
        setContentView(R.layout.account_settings);
        
        Preferences.init(this);
        
        String[] values;
        ArrayAdapter<String> adapter;
        
        Preferences prefs = Preferences.getInstance();
        String token = prefs.getUserData(this, "access_token");
        
        if (token != null) {
            values = new String[] {
                    "Logout (" + prefs.getUserData(this, "user_id", "") + ")"
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
        setListAdapter(adapter);
    }
    
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
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
                new RetrieveAccessTokenTask(this).execute(code, redirectURI);
            } else {
                showErrorMessage();
            }
        }
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        switch (position) {
        case 0:
            Preferences prefs = Preferences.getInstance();
            String token = prefs.getUserData(this, "access_token");
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
                prefs.clearUserData(this);
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                overridePendingTransition(0,0);
            }
            break;
        default:
            String item = (String) this.getListAdapter().getItem(position);
            Toast.makeText(getApplicationContext(), item + " selected",
                    Toast.LENGTH_LONG).show();
        }
    }
    
    protected void showErrorMessage() {
        //DialogFragment newFragment = AlertDialogFragment.newInstance(R.string.auth_error);
        //newFragment.show(getSupportFragmentManager(), "dialog");
    }
}
