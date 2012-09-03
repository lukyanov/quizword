package com.lingvapps.quizword;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalStorageHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;
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
            "is_my int DEFAULT 0, " +
            "is_in_class int DEFAULT 0, " +
            "is_favorite int DEFAULT 0, " +
            "name varchar(255), " +
            "term_count int default 0);";
    private static final String SET_INDEX1_CREATE =
            "CREATE INDEX " + SET_TABLE_NAME + "_is_my ON " +
            SET_TABLE_NAME + " (is_my)";
    private static final String SET_INDEX2_CREATE =
            "CREATE INDEX " + SET_TABLE_NAME + "_is_in_class ON " +
            SET_TABLE_NAME + " (is_in_class)";
    private static final String SET_INDEX3_CREATE =
            "CREATE INDEX " + SET_TABLE_NAME + "_is_favorite ON " +
            SET_TABLE_NAME + " (is_favorite)";

    LocalStorageHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SET_TABLE_CREATE);
        db.execSQL(SET_INDEX1_CREATE);
        db.execSQL(SET_INDEX2_CREATE);
        db.execSQL(SET_INDEX3_CREATE);
        
        db.execSQL(CARD_TABLE_CREATE);
        db.execSQL(CARD_INDEX_CREATE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + CARD_TABLE_NAME);
        db.execSQL("DROP TABLE " + SET_TABLE_NAME);
        onCreate(db);
    }
    
    public void clear_db() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(CARD_TABLE_NAME, null, null);
        db.delete(SET_TABLE_NAME, null, null);
    }
}