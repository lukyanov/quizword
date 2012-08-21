package com.lingvapps.quizword;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MySetsActivity extends FragmentActivity {

    private static ListView menuListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // TODO: not to load data again after screen orientation change
        
        // TODO: make common layout for all menu-like views
        setContentView(R.layout.my_sets);
        menuListView = (ListView) findViewById(R.id.my_sets_menu);

        RetrieveMySetsTask task = new RetrieveMySetsTask(this);
        task.setOnPostExecuteListener(new RetrieveMySetsTask.OnPostExecuteListener<ArrayAdapter<CardSet>>() {
            public void onSuccess(ArrayAdapter<CardSet> adapter) {
                if (adapter.getCount() > 0) {
                    menuListView.setAdapter(adapter);
                    menuListView.setOnItemClickListener(new OnItemClickListener() {
                        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                            onListItemClick((ListView) l, v, position, id);
                        }
                    });
                } else {
                    showSyncMenu();
                }
            }
            public void onFailure() {
                showSyncMenu();
            }
        });
        task.execute();
    }

    protected void showSyncMenu() {
        String[] values = new String[] {
                "Sync"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        menuListView.setAdapter(adapter);
        menuListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                onSyncMenuListItemClick((ListView) l, v, position, id);
            }
        });
        // TODO: maybe this is the case not to restart activity
        //menuListView.invalidateViews();
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent;
        intent = new Intent(this, CardActivity.class);
        CardSet set = (CardSet) l.getAdapter().getItem(position);
        intent.putExtra("set_id", set.getId());
        intent.putExtra("set_name", set.getName());
        startActivity(intent);
    }

    protected void onSyncMenuListItemClick(ListView l, View v, int position, long id) {
        switch (position) {
        case 0:
            // TODO: move to a separate class/function
            SyncSetsTask task = new SyncSetsTask(this);
            task.setMessage("Syncing...");
            task.setOnPostExecuteListener(new SyncSetsTask.OnPostExecuteListener<Boolean>() {
                public void onSuccess(Boolean result) {
                    // TODO: try not to do restart
                    restart();
                    Toast.makeText(getApplicationContext(), "Synced",
                            Toast.LENGTH_LONG).show();
                }
                public void onFailure() {
                    showErrorMessage(R.string.sync_error_title, R.string.sync_error_message);
                }
            });
            task.execute();
            break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_sets, menu);
        return true;
    }

    public void restart() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    // TODO: remove duplicated function
    protected void showErrorMessage(int title, int message) {
        DialogFragment newFragment = AlertDialogFragment.newInstance(title, message);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

}
