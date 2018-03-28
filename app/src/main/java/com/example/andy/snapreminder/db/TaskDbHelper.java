package com.example.andy.snapreminder.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TaskDbHelper extends SQLiteOpenHelper{
    public TaskDbHelper(Context context) {
        super(context, TaskContract.DB_NAME, null, TaskContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " +
                TaskContract.TaskEntry.TABLE + " ( " +
                TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskContract.TaskEntry.COL_TASK_TITLE + " TEXT NOT NULL, " +
                TaskContract.TaskEntry.COL_TASK_SUBTITLE + " TEXT NOT NULL, " +
                TaskContract.TaskEntry.COL_TASK_HOUR + " INTEGER, " +
                TaskContract.TaskEntry.COL_TASK_MINUTE + " INTEGER, " +
                TaskContract.TaskEntry.COL_TASK_DAY + " INTEGER, " +
                TaskContract.TaskEntry.COL_TASK_MONTH + " INTEGER, " +
                TaskContract.TaskEntry.COL_TASK_YEAR + " INTEGER, " +
                TaskContract.TaskEntry.COL_TASK_CHECK + " INTEGER, " +
                TaskContract.TaskEntry.COL_TASK_INT_MINUTE + " INTEGER, " +
                TaskContract.TaskEntry.COL_TASK_INT_HOUR + " INTEGER, " +
                TaskContract.TaskEntry.COL_TASK_INT_DAY + " INTEGER, " +
                TaskContract.TaskEntry.COL_TASK_PATH + " TEXT NOT NULL);";

        db.execSQL(createTable);

        Log.d(TaskContract.DEBUG_TAG, "Database creating...");
        Log.d(TaskContract.DEBUG_TAG, "Table " + TaskContract.TaskEntry.TABLE + " ver. " + TaskContract.DB_VERSION + " created. ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE);

        Log.d(TaskContract.DEBUG_TAG, "Database updating...");
        Log.d(TaskContract.DEBUG_TAG, "Table " + TaskContract.TaskEntry.TABLE + " updated from ver." + oldVersion + " to ver." + newVersion);
        Log.d(TaskContract.DEBUG_TAG, "All data is lost.");

        onCreate(db);
    }
}
