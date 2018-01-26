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
     * Path to time zone db table
     */
    public static final String PATH_TABLE_TIME_ZONES = "timeZones";

    /**
     * Inner class that defines constant values for the timezones database table.
     */
    public static abstract class TimeZonesEntry implements BaseColumns {

        /** The content URI to access the timezones data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TABLE_TIME_ZONES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of timezones.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TABLE_TIME_ZONES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single timezone.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TABLE_TIME_ZONES;

        /** Name of database table for timezones */
        public static final String TABLE_NAME = "timeZones";

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

        /**
         * Differentiates between time zones selected for current time and
         * custom time
         *
         * Type: INTEGER
         */
        public static final String COLUMN_TIME_DIFF= "difference";

        /**
         * Possible values to differentiate between current time and custom time
         */
        public static final int DIFF_CURRENT = 0;
        public static final int DIFF_CUSTOM = 1;

    }

}
