package com.lingvapps.quizword.renew;

import com.lingvapps.quizword.core.CardSet;
import com.lingvapps.quizword.renew.R;
import com.lingvapps.quizword.tasks.RetrieveMySetsTask;
import com.lingvapps.quizword.tasks.SyncSetsTask;
import com.lingvapps.quizword.utils.Preferences;

import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MySetsActivity extends ListMenuActivity {

    private int selectionType = Preferences.SELECTION_MY_SETS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBar();
        int titleId = -1;
        Bundle params = getIntent().getExtras();
        selectionType = params.getInt("selectionType");
        switch (selectionType) {
            case Preferences.SELECTION_MY_SETS:
                titleId = R.string.title_activity_my_sets;
                break;
            case Preferences.SELECTION_MY_FOLDERS:
                titleId = R.string.title_activity_my_folders;
                break;
            case Preferences.SELECTION_MY_CLASSES:
                titleId = R.string.title_activity_my_classes;
                break;
            case Preferences.SELECTION_FAVORITE_SETS:
                titleId = R.string.title_activity_favorite_sets;
                break;
        }
        setTitle(titleId);
        loadSets();
    }

    void loadSets() {
        RetrieveMySetsTask task = new RetrieveMySetsTask(this);
        task.setOnPostExecuteListener(new RetrieveMySetsTask.OnPostExecuteListener<ArrayAdapter<CardSet>>() {
            public void onSuccess(ArrayAdapter<CardSet> adapter) {
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
        intent = new Intent(this, CardActivity.class);
        CardSet set = (CardSet) l.getAdapter().getItem(position);
        intent.putExtra("set_id", set.getId());
        intent.putExtra("set_name", set.getName());
        intent.putExtra("lang_terms", set.getLangTerms());
        intent.putExtra("lang_definitions", set.getLangDefinitions());
        startActivity(intent);
    }

    protected void executeSyncTask() {
        SyncSetsTask task = new SyncSetsTask(this);
        task.setOnPostExecuteListener(new SyncSetsTask.OnPostExecuteListener<Boolean>() {
            public void onSuccess(Boolean result) {
                loadSets();
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
