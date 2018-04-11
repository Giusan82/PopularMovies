package com.example.android.popularmovies.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.DataContract.DataEntry;

public class DataDbHelper extends SQLiteOpenHelper {
    //Name of the database file
    private static final String DATABASE_NAME = "DataTracker.db";
    //Database version.
    private static final int DATABASE_VERSION = 1;

    // this contains the SQL statement to create the table
    private static final String SQL_CREATE_TABLE_DATA = "CREATE TABLE " + DataEntry.TABLE_NAME + " ("
            + DataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DataEntry.COLUMN_NAME + " TEXT NOT NULL, "
            + DataEntry.COLUMN_CREATION_DATE + " REAL NOT NULL, "
            + DataEntry.COLUMN_TASK_ID + " INTEGER NOT NULL, "
            + DataEntry.COLUMN_X + " REAL NOT NULL, "
            + DataEntry.COLUMN_Y + " REAL NOT NULL); ";

    public DataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_TABLE_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DataEntry.TABLE_NAME);
        onCreate(db);
    }
}
