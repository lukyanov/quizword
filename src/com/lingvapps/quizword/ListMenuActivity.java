package com.lingvapps.quizword;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

abstract class ListMenuActivity extends FragmentActivity {

    protected ListView menuListView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_menu);
        menuListView = (ListView) findViewById(android.R.id.list);
    }
    
    @TargetApi(11)
    protected void setActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final ActionBar bar = getActionBar();
            bar.setDisplayHomeAsUpEnabled(true);
        }
    }
    
    protected void drawMenuList(ArrayAdapter<?> adapter) {
        menuListView.setAdapter(adapter);
        menuListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                onListItemClick((ListView) l, v, position, id);
            }
        });
    }
    
    abstract protected void onListItemClick(ListView l, View v, int position, long id);

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

    protected void startMainMenuActivity() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        // TODO: fix back behavior
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void restart() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    protected void showErrorMessage(int title, int message) {
        DialogFragment newFragment = AlertDialogFragment.newInstance(title, message);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }
}