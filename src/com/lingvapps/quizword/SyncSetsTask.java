package com.lingvapps.quizword;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

class SyncSetsTask extends BackgroundTask<String, Boolean> {

    public SyncSetsTask(Context ctx) {
        super(ctx);
        setMessage(R.string.syncing_message);
    }

    protected Boolean doInBackground(String... params) {
        String user = Preferences.getInstance(context).getUserData("user_id");
        String token = Preferences.getInstance(context).getUserData(
                "access_token");
        LocalStorageHelper storageHelper = new LocalStorageHelper(context);
        storageHelper.clear_db();
        SQLiteDatabase db = storageHelper.getWritableDatabase();
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
            Toast.makeText(context, R.string.synced_message, Toast.LENGTH_LONG).show();
        } else {
            onPostExecuteListener.onFailure();
        }
        progressDialog.dismiss();
    }

}