package com.example.android.clockcalc.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Emils on 15.01.2018.
 */

public class TimeZoneProvider extends ContentProvider {

    private TimeZoneDbHelper dbHelper;

    private final static String LOG_TAG = TimeZoneProvider.class.getSimpleName();

    /**
     * URIMatcher code for the content URI for the time zone(current time) table
     */
    private static final int TIMEZONES_CURRENT = 100;

    /**
     * URIMatcher code for the content URI for a single timezone in the current time table
     */
    private static final int ID_TIMEZONE_CURRENT = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     * Static initializer. This is run the first time anything is called from this class.
     */
    static {
        sUriMatcher.addURI(TimeZoneContract.CONTENT_AUTHORITY, TimeZoneContract.PATH_CURRENT,
                TIMEZONES_CURRENT);
        sUriMatcher.addURI(TimeZoneContract.CONTENT_AUTHORITY, TimeZoneContract.PATH_CURRENT
                + "/#", ID_TIMEZONE_CURRENT);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new TimeZoneDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // hold result of the query
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match){
            case TIMEZONES_CURRENT:
                cursor = db.query(TimeZoneContract.CurrentEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ID_TIMEZONE_CURRENT:
                 selection = TimeZoneContract.CurrentEntry._ID + "=?";
                 selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(TimeZoneContract.CurrentEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long id;

        int match = sUriMatcher.match(uri);
        switch (match){
            case TIMEZONES_CURRENT:
                // check if timezone id is provided
                String timeZoneId = contentValues.getAsString(
                        TimeZoneContract.CurrentEntry.COLUMN_TIME_ZONE_ID);
                if (timeZoneId == null){
                    throw new IllegalArgumentException("Time zone requires an ID");
                }
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                id = db.insert(TimeZoneContract.CurrentEntry.TABLE_NAME, null,
                        contentValues);

                if (id == -1){
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }

                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
