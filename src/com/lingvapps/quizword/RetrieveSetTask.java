package com.lingvapps.quizword;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class RetrieveSetTask extends BackgroundTask<CardSet, CardSet> {

    public RetrieveSetTask(Context ctx) {
        super(ctx);
    }

    protected CardSet doInBackground(CardSet... params) {
        CardSet set = params[0];
        try {
            LocalStorageHelper storageHelper = new LocalStorageHelper(context);
            SQLiteDatabase db = storageHelper.getWritableDatabase();
            String[] fields = {"id", "term", "definition"};
            String[] whereArgs = {set.getId().toString()};
            Cursor cursor = db.query("cards", fields, "set_id = ?", whereArgs, null, null, null);
            while (cursor.moveToNext()) {
                set.addCard(new Card(set, cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
            }
            cursor.close();
            db.close();
            return set;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
