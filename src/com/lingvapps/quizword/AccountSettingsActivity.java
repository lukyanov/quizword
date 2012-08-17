package com.lingvapps.quizword;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class AccountSettingsActivity extends ListActivity {
    
    static final int DIALOG_ERROR_ID = 0;
    
    private static String quizletAuthState = new BigInteger(32, new SecureRandom()).toString();
    private String redirectURI;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        redirectURI = getString(R.string.app_id) + ":/after_auth";
        Log.d("auth", "my state: " + quizletAuthState);
        
        setContentView(R.layout.account_settings);
        
        String[] values;
        ArrayAdapter<String> adapter;
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String token = prefs.getString("quizlet_access_token", null);
        
        // TODO: check expire time as well
        if (token != null) {
            values = new String[] {
                    "Logout (" + prefs.getString("quizlet_user_id", "") + ")"
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
            Intent httpIntent = new Intent(Intent.ACTION_VIEW);
            httpIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            String authURL = QuizletHTTP.getAuthorizitionURL("read",
                    quizletAuthState, redirectURI);
            Log.d("quizlet", "Quizlet Auth URL: " + authURL);
            httpIntent.setData(Uri.parse(authURL));
            startActivity(httpIntent);   
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
