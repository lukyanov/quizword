package com.lingvapps.quizword;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalStorageHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "quizword";

    private static final String CARD_TABLE_NAME = "cards";
    private static final String CARD_TABLE_CREATE =
                "CREATE TABLE " + CARD_TABLE_NAME + " (" +
                "id int primary key, " +
                "set_id int, " +
                "term varchar(255), " +
                "definition varchar(255));";
    private static final String CARD_INDEX_CREATE =
            "CREATE INDEX " + CARD_TABLE_NAME + "_set_id ON " +
            CARD_TABLE_NAME + " (set_id)";

    private static final String SET_TABLE_NAME = "sets";
    private static final String SET_TABLE_CREATE =
            "CREATE TABLE " + SET_TABLE_NAME + " (" +
            "id int primary key, " +
            "name varchar(255), " +
            "term_count int default 0);";

    LocalStorageHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SET_TABLE_CREATE);
        db.execSQL(CARD_TABLE_CREATE);
        db.execSQL(CARD_INDEX_CREATE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL(CARD_INDEX_CREATE);
        }
    }
}