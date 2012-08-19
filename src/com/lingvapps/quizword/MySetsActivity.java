package com.lingvapps.quizword;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MySetsActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        RetrieveMySetsTask task = new RetrieveMySetsTask(this);
        task.setOnPostExecuteListener(new RetrieveMySetsTask.OnPostExecuteListener() {
            public void onSuccess(ArrayAdapter<String> adapter) {
                setContentView(R.layout.my_sets);
                setListAdapter(adapter);
            }
            public void onFailure() {
            }
        });
        Preferences.init(this);
        Preferences prefs = Preferences.getInstance();
        String token = prefs.getUserData(this, "access_token");
        String user  = prefs.getUserData(this, "user_id");
        task.execute(token, user);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent;
        intent = new Intent(this, CardActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_sets, menu);
        return true;
    }
}
