package com.lingvapps.quizword;

import java.math.BigInteger;
import java.security.SecureRandom;
import android.net.Uri;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
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
    public void onNewIntent(Intent intent) {
        Log.d("auth", "intent");
        super.onNewIntent(intent);
        Uri data = intent.getData();
        if (data == null) {
            return;
        }
        Log.d("auth", "data: " + data.toString());
        if (data.getPath().equals("/after_auth")) {
            Log.d("auth", "my state: " + quizletAuthState +
                    ", request state: " + data.getQueryParameter("state"));
            if (data.getQueryParameter("state").equals(quizletAuthState)) {
                String code = intent.getData().getQueryParameter("code");
                new RetrieveAccessTokenTask(this).execute(code, redirectURI);
            }
        }
    }
    
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch(id) {
        case DIALOG_ERROR_ID:
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.auth_error))
                   .setCancelable(false)
                   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                       }
                   })
                   .setNegativeButton("No", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                       }
                   });
            dialog = builder.create();
            break;
        default:
            dialog = null;
        }
        return dialog;
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
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
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
            }
            break;
        default:
            String item = (String) this.getListAdapter().getItem(position);
            Toast.makeText(getApplicationContext(), item + " selected",
                    Toast.LENGTH_LONG).show();
        }
    }
    
    protected void showErrorMessage() {
        showDialog(DIALOG_ERROR_ID);
        //DialogFragment newFragment = AlertDialogFragment.newInstance(R.string.auth_error);
        //newFragment.show(getFragmentManager(), "dialog");
    }
}
