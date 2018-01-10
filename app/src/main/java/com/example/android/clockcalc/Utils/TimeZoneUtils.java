package com.example.android.clockcalc.Utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Emils on 10.01.2018.
 */

/**
 * Time zone info returned by the TimeZone class is as follows:
 *
 * time zone:           libcore.util.ZoneInfo[id="Europe/Riga",mRawOffset=7200000,mEarliestRawOffset=5794000,mUseDst=true,mDstSavings=3600000,transitions=127]
 * ID:                  Europe/Riga
 * display name:        GMT+02:00
 *
 */

public class TimeZoneUtils {

    /**
     * get local date and time from calendar
     * @return formatted local datetime String
     */
    public static String getLocalDateTime (SimpleDateFormat defaultFormat){
        Calendar c = Calendar.getInstance();
        String formatted = defaultFormat.format(c.getTime());

        return formatted;
    }

    /**
     * Convert local time zone datetime to the destination datetime
     * @param localDateTime local datetime String
     * @return formatted date converted to destination time zone
     */
    public static String getDestinationDateTime(String localDateTime, SimpleDateFormat defaultFormat, TimeZone destinationTimeZone) {
        try {
            Date date = defaultFormat.parse(localDateTime);
            SimpleDateFormat destFormat = defaultFormat;
            destFormat.setTimeZone(destinationTimeZone);

            Log.v("Destination DateTime", destFormat.format(date));
            return destFormat.format(date);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
