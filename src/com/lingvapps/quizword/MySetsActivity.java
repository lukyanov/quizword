package com.lingvapps.quizword;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MySetsActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        RetrieveMySetsTask task = new RetrieveMySetsTask(this);
        task.setOnPostExecuteListener(new RetrieveMySetsTask.OnPostExecuteListener<ArrayAdapter<CardSet>>() {
            public void onSuccess(ArrayAdapter<CardSet> adapter) {
                setContentView(R.layout.my_sets);
                setListAdapter(adapter);
            }
            public void onFailure() {
            }
        });
        task.execute();
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent;
        intent = new Intent(this, CardActivity.class);
        CardSet set = (CardSet) getListAdapter().getItem(position);
        intent.putExtra("set_id", set.getId());
        intent.putExtra("set_name", set.getName());
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_sets, menu);
        return true;
    }
}
