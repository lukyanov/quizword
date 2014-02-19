package com.lingvapps.quizword.renew;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalStorageHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 12;
    private static final String DATABASE_NAME = "quizword";

    private static final String SET_TABLE_NAME = "sets";
    private static final String SET_TABLE_CREATE =
            "CREATE TABLE " + SET_TABLE_NAME + " (" +
            "id int primary key, " +
            "bag varchar(15) NOT NULL, " +
            "name varchar(255) NOT NULL, " +
            "lang_terms char(2) NOT NULL, " +
            "lang_definitions char(2) NOT NULL, " +
            "term_count int NOT NULL default 0);";
    private static final String SET_INDEX1_CREATE =
            "CREATE INDEX " + SET_TABLE_NAME + "_bag ON " +
            SET_TABLE_NAME + " (bug)";

    private static final String FOLDER_TABLE_NAME = "folders";
    private static final String FOLDER_TABLE_CREATE =
            "CREATE TABLE " + FOLDER_TABLE_NAME + " (" +
            "id int primary key, " +
            "name varchar(255) NOT NULL, " +
            "set_count int NOT NULL default 0);";

    private static final String CLASS_TABLE_NAME = "classes";
    private static final String CLASS_TABLE_CREATE =
            "CREATE TABLE " + CLASS_TABLE_NAME + " (" +
            "id int primary key, " +
            "name varchar(255) NOT NULL, " +
            "set_count int NOT NULL default 0);";
    
    private static final String CARD_TABLE_NAME = "cards";
    private static final String CARD_TABLE_CREATE =
            "CREATE TABLE " + CARD_TABLE_NAME + " (" +
            "id int primary key, " +
            "set_id int NOT NULL, " +
            "term varchar(255) NOT NULL, " +
            "definition varchar(255) NOT NULL, " +
            "FOREIGN KEY (set_id) REFERENCES " + SET_TABLE_NAME + "(id) ON DELETE CASCADE);";
    private static final String CARD_INDEX_CREATE =
            "CREATE INDEX " + CARD_TABLE_NAME + "_set_id ON " +
            CARD_TABLE_NAME + " (set_id)";

    private Context context;

    LocalStorageHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SET_TABLE_CREATE);
        db.execSQL(SET_INDEX1_CREATE);
        db.execSQL(FOLDER_TABLE_CREATE);
        db.execSQL(CLASS_TABLE_CREATE);
        db.execSQL(CARD_TABLE_CREATE);
        db.execSQL(CARD_INDEX_CREATE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + CARD_TABLE_NAME);
        db.execSQL("DROP TABLE " + SET_TABLE_NAME);
        db.execSQL("DROP TABLE " + FOLDER_TABLE_NAME);
        db.execSQL("DROP TABLE " + CLASS_TABLE_NAME);
        Preferences.getInstance(context).clearDataSyncedFlagAll();
        onCreate(db);
    }
    
    public void clear_all() {
        SQLiteDatabase db = getWritableDatabase();
        clear_all(db);
        db.close();
    }
    
    public void clear_all(SQLiteDatabase db) {
        db.delete(CARD_TABLE_NAME, null, null);
        db.delete(SET_TABLE_NAME, null, null);
        db.delete(FOLDER_TABLE_NAME, null, null);
        db.delete(CLASS_TABLE_NAME, null, null);
   }

    public void clear_sets(SQLiteDatabase db, String what) {
        db.execSQL("PRAGMA foreign_keys = ON");
        db.delete(SET_TABLE_NAME, "bag = '" + what + "'", null);
    }
}
