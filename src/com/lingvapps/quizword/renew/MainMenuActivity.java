package com.lingvapps.quizword.renew;

import com.lingvapps.quizword.renew.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainMenuActivity extends ListMenuActivity {

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
        Preferences prefs = Preferences.getInstance(this);
        switch (position) {
        case 0:
            if (prefs.getUserData("user_id") != null) {
                intent = new Intent(this, MySetsActivity.class);
                intent.putExtra("selectionType", Preferences.SELECTION_MY_SETS);
            } else {
                intent = new Intent(this, AccountSettingsActivity.class);
            }
            startActivity(intent);
            break;
        case 1:
            if (prefs.getUserData("user_id") != null) {
                intent = new Intent(this, MySetsActivity.class);
                intent.putExtra("selectionType", Preferences.SELECTION_MY_CLASSES_SETS);
            } else {
                intent = new Intent(this, AccountSettingsActivity.class);
            }
            startActivity(intent);
            break;
        case 2:
            if (prefs.getUserData("user_id") != null) {
                intent = new Intent(this, MySetsActivity.class);
                intent.putExtra("selectionType", Preferences.SELECTION_FAVORITE_SETS);
            } else {
                intent = new Intent(this, AccountSettingsActivity.class);
            }
            startActivity(intent);
            break;
        case 3:
            intent = new Intent(this, AccountSettingsActivity.class);
            startActivity(intent);
            break;
        }
    }
}
