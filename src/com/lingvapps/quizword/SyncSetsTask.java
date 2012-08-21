package com.lingvapps.quizword;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

class SyncSetsTask extends HTTPTask<String, Boolean> {

    public SyncSetsTask(Context ctx) {
        super(ctx);
    }

    protected Boolean doInBackground(String... params) {
        String user = Preferences.getInstance(context).getUserData("user_id");
        String token = Preferences.getInstance(context).getUserData(
                "access_token");
        LocalStorageHelper storageHelper = new LocalStorageHelper(context);
        SQLiteDatabase db = storageHelper.getWritableDatabase();
        db.delete("cards", null, null);
        db.delete("sets", null, null);
        try {
            JSONArray ss = QuizletHTTP.requestMySetsFullDetails(token, user);
            if (ss != null) {
                for (int i = 0; i < ss.length(); i++) {
                    JSONObject obj = ss.getJSONObject(i);
                    ContentValues values = new ContentValues();
                    values.put("id", obj.getInt("id"));
                    values.put("name", obj.getString("title"));
                    values.put("term_count", obj.getString("term_count"));
                    db.insert("sets", null, values);
                    
                    fillCardsTable(db, obj.getInt("id"), obj.getJSONArray("terms"));
                }
                db.close();
                return true;
            } else {
                db.close();
                return false;
            }
        } catch (Exception e) {
            db.close();
            e.printStackTrace();
            return false;
        }
    }
    
    private void fillCardsTable(SQLiteDatabase db, Integer setId, JSONArray terms) throws JSONException, SQLiteException {
        for (int i = 0; i < terms.length(); i++) {
            JSONObject obj = terms.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put("id", obj.getInt("id"));
            values.put("set_id", setId);
            values.put("term", obj.getString("term"));
            values.put("definition", obj.getString("definition"));
            db.insert("cards", null, values);
        }
    }
    
    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            onPostExecuteListener.onSuccess(result);
        } else {
            onPostExecuteListener.onFailure();
        }
        progressDialog.dismiss();
    }

}