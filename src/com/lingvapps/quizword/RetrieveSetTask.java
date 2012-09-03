package com.lingvapps.quizword;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class RetrieveSetTask extends BackgroundTask<String, CardSet> {

    public RetrieveSetTask(Context ctx) {
        super(ctx);
    }

    protected CardSet doInBackground(String... params) {
        String setId = params[0];
        String setName = params[0];
        try {
            LocalStorageHelper storageHelper = new LocalStorageHelper(context);
            SQLiteDatabase db = storageHelper.getWritableDatabase();
            String[] fields = {"id", "term", "definition"};
            String[] whereArgs = {setId};
            Cursor cursor = db.query("cards", fields, "set_id = ?", whereArgs, null, null, null);
            CardSet set = new CardSet(Integer.parseInt(setId), setName);
            while (cursor.moveToNext()) {
                set.addCard(new Card(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
            }
            db.close();
            return set;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
