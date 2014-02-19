package com.lingvapps.quizword.renew;

import com.lingvapps.quizword.renew.R;

import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MyFoldersActivity extends ListMenuActivity {

    private int selectionType = Preferences.SELECTION_MY_FOLDERS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBar();
        int titleId = -1;
        Bundle params = getIntent().getExtras();
        selectionType = params.getInt("selectionType");
        switch (selectionType) {
            case Preferences.SELECTION_MY_FOLDERS:
                titleId = R.string.title_activity_my_folders;
                break;
            case Preferences.SELECTION_MY_CLASSES:
                titleId = R.string.title_activity_my_classes;
                break;
        }
        setTitle(titleId);
        loadFolders();
    }

    void loadFolders() {
        RetrieveMyFoldersTask task = new RetrieveMyFoldersTask(this);
        task.setOnPostExecuteListener(new RetrieveMyFoldersTask.OnPostExecuteListener<ArrayAdapter<Folder>>() {
            public void onSuccess(ArrayAdapter<Folder> adapter) {
                Preferences prefs = Preferences
                        .getInstance(getApplicationContext());
                if (prefs.isDataSynced(selectionType)) {
                    drawMenuList(adapter);
                } else {
                    drawSyncMenu();
                }
            }

            public void onFailure() {
                drawSyncMenu();
            }
        });
        task.execute(selectionType);
    }

    // TODO: move this to some base class
    protected void drawSyncMenu() {
        String item = getString(R.string.menu_sync);
        String[] values = new String[] { item };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        menuListView.setAdapter(adapter);
        menuListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> l, View v, int position,
                    long id) {
                onSyncMenuListItemClick((ListView) l, v, position, id);
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent;
        intent = new Intent(this, MySetsActivity.class);
        intent.putExtra("selectionType", selectionType);
        startActivity(intent);
    }

    // TODO: move to base class
    protected void executeSyncTask() {
        SyncSetsTask task = new SyncSetsTask(this);
        task.setOnPostExecuteListener(new SyncSetsTask.OnPostExecuteListener<Boolean>() {
            public void onSuccess(Boolean result) {
                loadFolders();
            }

            public void onFailure() {
                showErrorMessage(R.string.sync_error_title,
                        R.string.sync_error_message);
            }
        });
        task.execute(selectionType);
    }

    protected void onSyncMenuListItemClick(ListView l, View v, int position,
            long id) {
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
                onBackPressed();
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
