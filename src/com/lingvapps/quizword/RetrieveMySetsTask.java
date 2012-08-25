package com.lingvapps.quizword;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

class RetrieveMySetsTask extends BackgroundTask<String, ArrayAdapter<CardSet>> {

    public RetrieveMySetsTask(Context ctx) {
        super(ctx);
    }

    protected ArrayAdapter<CardSet> doInBackground(String... params) {
        try {
            LocalStorageHelper storageHelper = new LocalStorageHelper(context);
            SQLiteDatabase db = storageHelper.getReadableDatabase();
            String[] fields = {"id", "name", "term_count"};
            Cursor cursor = db.query("sets", fields, null, null, null, null, null);
            CardSet[] sets = new CardSet[cursor.getCount()];
            int i = 0;
            while (cursor.moveToNext()) {
                sets[i] = new CardSet(cursor.getInt(0),
                        cursor.getString(1), cursor.getInt(2));
                i++;
            }
            return new ArrayAdapter<CardSet>(context,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1, sets);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}