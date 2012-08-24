package com.lingvapps.quizword;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MySetsActivity extends ListMenuActivity {

    @TargetApi(11)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final ActionBar bar = getActionBar();
            bar.setDisplayHomeAsUpEnabled(true);
        }

        loadSets();
    }
    
    void loadSets() {
        RetrieveMySetsTask task = new RetrieveMySetsTask(this);
        task.setOnPostExecuteListener(new RetrieveMySetsTask.OnPostExecuteListener<ArrayAdapter<CardSet>>() {
            public void onSuccess(ArrayAdapter<CardSet> adapter) {
                if (adapter.getCount() > 0) {
                    drawMenuList(adapter);
                } else {
                    drawSyncMenu();
                }
            }
            public void onFailure() {
                drawSyncMenu();
            }
        });
        task.execute();
    }

    protected void drawSyncMenu() {
        String item = getString(R.string.menu_sync);
        String[] values = new String[] { item };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        menuListView.setAdapter(adapter);
        menuListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                onSyncMenuListItemClick((ListView) l, v, position, id);
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent;
        intent = new Intent(this, CardActivity.class);
        CardSet set = (CardSet) l.getAdapter().getItem(position);
        intent.putExtra("set_id", set.getId());
        intent.putExtra("set_name", set.getName());
        startActivity(intent);
    }
    
    protected void executeSyncTask() {
        // TODO: move to a separate class/function
        SyncSetsTask task = new SyncSetsTask(this);
        task.setMessage("Syncing...");
        task.setOnPostExecuteListener(new SyncSetsTask.OnPostExecuteListener<Boolean>() {
            public void onSuccess(Boolean result) {
                loadSets();
                Toast.makeText(getApplicationContext(), "Synced",
                        Toast.LENGTH_LONG).show();
            }
            public void onFailure() {
                showErrorMessage(R.string.sync_error_title, R.string.sync_error_message);
            }
        });
        task.execute();
    }

    protected void onSyncMenuListItemClick(ListView l, View v, int position, long id) {
        switch (position) {
        case 0:
            executeSyncTask();
            break;
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startMainMenuActivity();
                return true;
            case R.id.menu_sync:
                executeSyncTask();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_sets, menu);
        return true;
    }
}
