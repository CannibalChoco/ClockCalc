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
     * get current date and time from calendar
     * @return formatted datetime String
     */
    public static String getCurrentDateTime (TimeZone timeZone, SimpleDateFormat defaultFormat){
        Calendar c = Calendar.getInstance(timeZone);
        String formatted = defaultFormat.format(c.getTime());

        return formatted;
    }

    /**
     * Get DateTime from custom time
     * @param sourceDateTime source datetime String
     * @return formatted date converted to destination time zone
     */
    public static String getDestinationDateTime(String sourceDateTime, SimpleDateFormat defaultFormat,
                                                TimeZone destinationTimeZone) {
        try {
            Date date = defaultFormat.parse(sourceDateTime);
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
