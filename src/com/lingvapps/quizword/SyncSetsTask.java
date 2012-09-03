package com.lingvapps.quizword;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

class SyncSetsTask extends BackgroundTask<String, Boolean> {

    public SyncSetsTask(Context ctx) {
        super(ctx);
        setMessage(R.string.syncing_message);
    }

    protected Boolean doInBackground(String... params) {
        Preferences prefs = Preferences.getInstance(context);
        String user  = prefs.getUserData("user_id");
        String token = prefs.getUserData("access_token");
        LocalStorageHelper storageHelper = new LocalStorageHelper(context);
        storageHelper.clear_db();
        SQLiteDatabase db = storageHelper.getWritableDatabase();
        try {
            JSONArray sets = QuizletHTTP.requestMySetsFullDetails(token, user);
            storeMySetsFullDetails(db, sets);

            JSONArray groups = QuizletHTTP.requestMyGroups(token, user);
            for (int i = 0; i < groups.length(); i++) {
                JSONObject group = groups.getJSONObject(i);
                storeGroupSets(db, group.getJSONArray("sets"), token);
            }

            sets = QuizletHTTP.requestFavoriteSets(token, user);
            storeFavoriteSets(db, sets);
            
            prefs.setDataSyncedFlag();
            
            db.close();
            
            return true;
        } catch (Exception e) {
            db.close();
            e.printStackTrace();
            return false;
        }
    }

    private void storeMySetsFullDetails(SQLiteDatabase db, JSONArray ss) throws JSONException {
        CardSet cardSet;
        for (int i = 0; i < ss.length(); i++) {
            JSONObject obj = ss.getJSONObject(i);
            cardSet = new CardSet(obj.getInt("id"), obj.getString("title"));
            fillCardSet(cardSet, obj.getJSONArray("terms"));
            storeSetToDatabase(db, cardSet, 1, null, null);
        }
    }

    private void storeFavoriteSets(SQLiteDatabase db, JSONArray ss) throws JSONException {
        CardSet cardSet;
        for (int i = 0; i < ss.length(); i++) {
            JSONObject obj = ss.getJSONObject(i);
            cardSet = new CardSet(obj.getInt("id"), obj.getString("title"));
            fillCardSet(cardSet, obj.getJSONArray("terms"));
            storeSetToDatabase(db, cardSet, null, null, 1);
        }
    }

    private void storeGroupSets(SQLiteDatabase db, JSONArray ss, String token) throws JSONException {
        CardSet cardSet;
        for (int i = 0; i < ss.length(); i++) {
            JSONObject obj = ss.getJSONObject(i);
            cardSet = new CardSet(obj.getInt("id"), obj.getString("title"));
            JSONObject setData = QuizletHTTP.requestSet(token, obj.getInt("id"));
            fillCardSet(cardSet, setData.getJSONArray("terms"));
            storeSetToDatabase(db, cardSet, null, 1, null);
        }
    }

    private void fillCardSet(CardSet cardSet, JSONArray terms) throws JSONException {
        for (int i = 0; i < terms.length(); i++) {
            JSONObject obj = terms.getJSONObject(i);
            cardSet.addCard(new Card(obj.getInt("id"), obj.getString("term"), obj.getString("definition")));
        }
    }

    private void storeSetToDatabase(SQLiteDatabase db, CardSet cardSet, Integer isMy, Integer isInClass, Integer isFavorite) {
        ContentValues values = new ContentValues();
        values.put("id", cardSet.getId());
        if (isMy != null)
            values.put("is_my", isMy);
        if (isInClass != null)
            values.put("is_in_class", isInClass);
        if (isFavorite != null)
            values.put("is_favorite", isFavorite);
        values.put("name", cardSet.getName());
        values.put("term_count", cardSet.size());
        if (db.insert("sets", null, values) == -1) {
            // there already is such a set
            values = new ContentValues();
            if (isInClass != null)
                values.put("is_in_class", isInClass);
            if (isFavorite != null)
                values.put("is_favorite", isFavorite);
            db.update("sets", values, "id = ?", new String[] { cardSet.getId().toString() });
        } else {
            for (Card card : cardSet) {
                values = new ContentValues();
                values.put("id", card.getId());
                values.put("set_id", cardSet.getId());
                values.put("term", card.getTerm());
                values.put("definition", card.getDefinition());
                db.insert("cards", null, values);
            }
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