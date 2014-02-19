package com.lingvapps.quizword.tasks;

import com.lingvapps.quizword.core.Folder;
import com.lingvapps.quizword.utils.LocalStorageHelper;
import com.lingvapps.quizword.utils.Preferences;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

public class RetrieveMyFoldersTask extends BackgroundTask<Integer, ArrayAdapter<Folder>> {

    public RetrieveMyFoldersTask(Context ctx) {
        super(ctx);
    }

    protected ArrayAdapter<Folder> doInBackground(Integer... params) {
        Integer selectionId = params[0];
        try {
            LocalStorageHelper storageHelper = new LocalStorageHelper(context);
            SQLiteDatabase db = storageHelper.getReadableDatabase();
            String[] fields = {"id", "name"};
            String table = "";
            int folder_type = -1;
            switch (selectionId) {
            case Preferences.SELECTION_MY_FOLDERS:
                table = "folders";
                folder_type = Folder.FOLDER_TYPE_FOLDER;
                break;
            case Preferences.SELECTION_MY_CLASSES:
                table = "classes";
                folder_type = Folder.FOLDER_TYPE_CLASS;
                break;
            }
            Cursor cursor = db.query(table, fields, "", null, null, null, null);
            Folder[] folders = new Folder[cursor.getCount()];
            int i = 0;
            while (cursor.moveToNext()) {
                folders[i] = new Folder(folder_type, cursor.getInt(0), cursor.getString(1));
                i++;
            }
            cursor.close();
            db.close();
            return new ArrayAdapter<Folder>(context,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1, folders);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
