package com.example.android.clockcalc.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Emils on 15.01.2018.
 */

public class TimeZoneContract {

    private TimeZoneContract(){}

    /**
     * CONTENT_AUTHORITY - the name for the entire content provider
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.clockcalc";

    /**
     * Base of all URI's
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     */
    public static final String PATH_CURRENT = "current";

    /**
     * Inner class that defines constant values for the current time timezones database table.
     */
    public static abstract class CurrentEntry implements BaseColumns {

        /** The content URI to access the timezones for current time data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CURRENT);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of timezones.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CURRENT;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single timezone.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CURRENT;

        /** Name of database table for current time timezones */
        public static final String TABLE_NAME = "current";

        /**
         * Unique ID number for the TimeZone (only for use in the database table).
         *
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * TimeZone ID
         *
         * Type: TEXT
         */
        public static final String COLUMN_TIME_ZONE_ID= "timeZone";

    }
}
