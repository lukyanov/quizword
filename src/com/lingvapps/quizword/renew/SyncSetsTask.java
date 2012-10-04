package com.lingvapps.quizword.renew;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lingvapps.quizword.renew.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

class SyncSetsTask extends BackgroundTask<Integer, Boolean> {

    public SyncSetsTask(Context ctx) {
        super(ctx);
        setMessage(R.string.syncing_message);
    }

    protected Boolean doInBackground(Integer... params) {
        int selectionType = params[0];
        Preferences prefs = Preferences.getInstance(context);
        String user  = prefs.getUserData("user_id");
        String token = prefs.getUserData("access_token");
        LocalStorageHelper storageHelper = new LocalStorageHelper(context);
        SQLiteDatabase db = storageHelper.getWritableDatabase();
        CacheManager.clearCache(context);
        try {
            switch (selectionType) {
            case Preferences.SELECTION_MY_SETS:
                storageHelper.clear_my_sets(db);
                syncMySets(db, user, token);
                prefs.setDataSyncedFlag(selectionType);
                break;
            case Preferences.SELECTION_MY_CLASSES_SETS:
                storageHelper.clear_my_classes_sets(db);
                syncClassesSets(db, user, token);
                prefs.setDataSyncedFlag(selectionType);
                break;
            case Preferences.SELECTION_FAVORITE_SETS:
                storageHelper.clear_favorites(db);
                syncFavoriteSets(db, user, token);
                prefs.setDataSyncedFlag(selectionType);
                break;
            default:
                storageHelper.clear_all(db);
                syncMySets(db, user, token);
                syncClassesSets(db, user, token);
                syncFavoriteSets(db, user, token);
                prefs.setDataSyncedFlag(Preferences.SELECTION_MY_SETS);
                prefs.setDataSyncedFlag(Preferences.SELECTION_MY_CLASSES_SETS);
                prefs.setDataSyncedFlag(Preferences.SELECTION_FAVORITE_SETS);
            }
            
            db.close();
            
            return true;
        } catch (Exception e) {
            db.close();
            e.printStackTrace();
            return false;
        }
    }

    private void syncMySets(SQLiteDatabase db, String user, String token) throws JSONException {
        JSONArray ss = QuizletHTTP.requestMySetsFullDetails(token, user);
        CardSet cardSet;
        for (int i = 0; i < ss.length(); i++) {
            JSONObject obj = ss.getJSONObject(i);
            cardSet = createCardSet(obj);
            fillCardSet(cardSet, obj.getJSONArray("terms"));
            storeSetToDatabase(db, cardSet, 1, null, null);
        }
    }

    private void syncFavoriteSets(SQLiteDatabase db, String user, String token) throws JSONException {
        CardSet cardSet;
        JSONArray ss = QuizletHTTP.requestFavoriteSets(token, user);
        for (int i = 0; i < ss.length(); i++) {
            JSONObject obj = ss.getJSONObject(i);
            cardSet = createCardSet(obj);
            fillCardSet(cardSet, obj.getJSONArray("terms"));
            storeSetToDatabase(db, cardSet, null, null, 1);
        }
    }

    private void syncClassesSets(SQLiteDatabase db, String user, String token) throws JSONException {
        CardSet cardSet;
        JSONArray groups = QuizletHTTP.requestMyGroups(token, user);
        for (int i = 0; i < groups.length(); i++) {
            JSONObject group = groups.getJSONObject(i);
            JSONArray ss = group.getJSONArray("sets");
            for (int j = 0; j < ss.length(); j++) {
                JSONObject obj = ss.getJSONObject(j);
                JSONObject setData = QuizletHTTP.requestSet(token, obj.getInt("id"));
                cardSet = createCardSet(setData);
                fillCardSet(cardSet, setData.getJSONArray("terms"));
                storeSetToDatabase(db, cardSet, null, 1, null);
            }
        }
    }

    private CardSet createCardSet(JSONObject obj) throws JSONException {
        return new CardSet(obj.getInt("id"), obj.getString("title"), obj.getString("lang_terms"), obj.getString("lang_definitions"));
    }

    private void fillCardSet(CardSet cardSet, JSONArray terms) throws JSONException {
        for (int i = 0; i < terms.length(); i++) {
            JSONObject obj = terms.getJSONObject(i);
            cardSet.addCard(new Card(cardSet, obj.getInt("id"), obj.getString("term"), obj.getString("definition")));
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
        values.put("lang_terms", cardSet.getLangTerms());
        values.put("lang_definitions", cardSet.getLangDefinitions());
        values.put("term_count", cardSet.size());

        if (db.insert("sets", null, values) == -1) {
            // there already is such a set
            values = new ContentValues();
            if (isMy != null)
                values.put("is_my", isMy);
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
