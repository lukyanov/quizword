package com.lingvapps.quizword;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

class RetrieveSetTask extends HTTPTask<String, CardSet> {

    public RetrieveSetTask(Context ctx) {
        super(ctx);
    }

    protected CardSet doInBackground(String... params) {
        String setId = params[0];
        String setName = params[0];
        Log.d("quizlet", "loading my sets");
        try {
            LocalStorageHelper storageHelper = new LocalStorageHelper(context);
            SQLiteDatabase db = storageHelper.getWritableDatabase();
            String[] fields = {"term", "definition"};
            String[] whereArgs = {setId};
            // TODO: create index for set_id
            Cursor cursor = db.query("cards", fields, "set_id = ?", whereArgs, null, null, null);
            CardSet set = new CardSet(Integer.parseInt(setId), setName);
            while (cursor.moveToNext()) {
                set.addCard(new Card(cursor.getString(0), cursor.getString(1)));
            }
            return set;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
