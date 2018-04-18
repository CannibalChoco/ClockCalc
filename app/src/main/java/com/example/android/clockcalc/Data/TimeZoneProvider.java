package com.example.android.clockcalc.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class TimeZoneProvider extends ContentProvider {

    private TimeZoneDbHelper dbHelper;

    private final static String LOG_TAG = TimeZoneProvider.class.getSimpleName();

    /**
     * URIMatcher code for the content URI for the time zone table
     */
    private static final int TIMEZONES = 100;

    /**
     * URIMatcher code for the content URI for a single timezone
     */
    private static final int ID_TIMEZONE = 101;

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
        sUriMatcher.addURI(TimeZoneContract.CONTENT_AUTHORITY,
                TimeZoneContract.PATH_TABLE_TIME_ZONES, TIMEZONES); // 100
        sUriMatcher.addURI(TimeZoneContract.CONTENT_AUTHORITY,
                TimeZoneContract.PATH_TABLE_TIME_ZONES + "/#", ID_TIMEZONE); // 101
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
        Log.i("INFO", "URI: " + String.valueOf(uri));
        switch (match){
            case TIMEZONES:
                cursor = db.query(TimeZoneContract.TimeZonesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ID_TIMEZONE:
                 selection = TimeZoneContract.TimeZonesEntry._ID + "=?";
                 selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(TimeZoneContract.TimeZonesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case TIMEZONES:
                return TimeZoneContract.TimeZonesEntry.CONTENT_LIST_TYPE;
            case ID_TIMEZONE:
                return TimeZoneContract.TimeZonesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long id;
        SQLiteDatabase db;

        int match = sUriMatcher.match(uri);
        switch (match){
            case TIMEZONES:
                db = dbHelper.getWritableDatabase();
                id = db.insert(TimeZoneContract.TimeZonesEntry.TABLE_NAME, null,
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
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;

        int match = sUriMatcher.match(uri);
        switch (match){
            case TIMEZONES:
                rowsDeleted = db.delete(TimeZoneContract.TimeZonesEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case ID_TIMEZONE:
                selection = TimeZoneContract.TimeZonesEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(TimeZoneContract.TimeZonesEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }
}
