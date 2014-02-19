package com.lingvapps.quizword.renew;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

class RetrieveMySetsTask extends BackgroundTask<Integer, ArrayAdapter<CardSet>> {

    public RetrieveMySetsTask(Context ctx) {
        super(ctx);
    }

    protected ArrayAdapter<CardSet> doInBackground(Integer... params) {
        Integer selectionId = params[0];
        try {
            LocalStorageHelper storageHelper = new LocalStorageHelper(context);
            SQLiteDatabase db = storageHelper.getReadableDatabase();
            String[] fields = {"id", "name", "lang_terms", "lang_definitions", "term_count"};
            String selection = "";
            switch (selectionId) {
            case Preferences.SELECTION_MY_SETS:
                selection = "bag = 'my_sets'";
                break;
            case Preferences.SELECTION_MY_FOLDERS:
                selection = "bag = 'my_folders'";
                break;
            case Preferences.SELECTION_MY_CLASSES:
                selection = "bag = 'my_classes'";
                break;
            case Preferences.SELECTION_FAVORITE_SETS:
                selection = "bag = 'favorite'";
                break;
            }
            Cursor cursor = db.query("sets", fields, selection, null, null, null, null);
            CardSet[] sets = new CardSet[cursor.getCount()];
            int i = 0;
            while (cursor.moveToNext()) {
                sets[i] = new CardSet(cursor.getInt(0),
                        cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4));
                i++;
            }
            cursor.close();
            db.close();
            return new ArrayAdapter<CardSet>(context,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1, sets);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
