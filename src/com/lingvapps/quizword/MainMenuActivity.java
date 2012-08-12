package com.lingvapps.quizword;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainMenuActivity extends ListActivity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        
        String[] values = new String[] {
        		"View My Sets",
        		"Find Public Sets",
        		"Account Settings"
        };
        // Use your own layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, android.R.id.text1, values);
        ListView list = (ListView) this.findViewById(R.id.main_menu_list);
        list.setAdapter(adapter);
    }
}
