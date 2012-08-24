package com.lingvapps.quizword;

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
 * - http://tools.android.com/recent/lintapicheck/
 * - http://vlad8.com/tech/2011/07/android-intents-tutorial/
 *
 */

public class MainMenuActivity extends ListMenuActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.main_menu, android.R.layout.simple_list_item_1);
        drawMenuList(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent;
        switch (position) {
        case 0:
            Preferences prefs = Preferences.getInstance(this);
            if (prefs.getUserData("user_id") != null) {
                intent = new Intent(this, MySetsActivity.class);
            } else {
                intent = new Intent(this, AccountSettingsActivity.class);
            }
            startActivity(intent);
            break;
        case 2:
            intent = new Intent(this, AccountSettingsActivity.class);
            startActivity(intent);
            break;
        default:
            String item = (String) menuListView.getAdapter().getItem(position);
            Toast.makeText(getApplicationContext(), item + " selected",
                    Toast.LENGTH_LONG).show();
        }
    }
}
