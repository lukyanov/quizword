package com.lingvapps.quizword;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/*
 * Useful URLs:
 * - http://www.vogella.com/articles/AndroidListView/article.html#listview
 * - http://megadarja.blogspot.com/2010/07/android.html
 *
 * TODO:
 * - final
 * - Activity vs ListActivity
 * - Callbacks
 * - new Object() { ... }
 */

public class MainMenuActivity extends ListActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_menu);
        String[] values = new String[] {
                "View My Sets",
                "Find Public Sets",
                "Account Settings"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (position == 0) {
            Intent myIntent = new Intent(l.getContext(), CardActivity.class);
            startActivityForResult(myIntent, 0);
        } else {
            String item = (String) this.getListAdapter().getItem(position);
            Toast.makeText(getApplicationContext(), item + " selected",
                    Toast.LENGTH_LONG).show();
        }
    }
}
