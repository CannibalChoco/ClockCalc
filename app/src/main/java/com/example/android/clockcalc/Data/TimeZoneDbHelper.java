package com.example.android.clockcalc.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Emils on 15.01.2018.
 */

public class TimeZoneDbHelper extends SQLiteOpenHelper {

    /** if the schema is changed, DATABASE_VERSION must be incremented */
    private static final int DATABASE_VERSION = 1;

    /** name of the database file */
    private static final String DATABASE_NAME = "timeZones.db";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS";

    /**
     * String to create table of timezones for current time
     */
    private static final String SQL_CREATE_CURRENT_TIME_TABLE = "CREATE TABLE " +
            TimeZoneContract.CurrentEntry.TABLE_NAME +
            " (" +
            TimeZoneContract.CurrentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TimeZoneContract.CurrentEntry.COLUMN_TIME_ZONE_ID + " TEXT NOT NULL); ";


    /**
     * Constructor
     */
    public TimeZoneDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CURRENT_TIME_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(DROP_TABLE + TimeZoneContract.CurrentEntry.TABLE_NAME);

        onCreate(db);
    }
}
