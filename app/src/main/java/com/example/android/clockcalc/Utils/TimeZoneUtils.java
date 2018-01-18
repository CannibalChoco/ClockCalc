package com.example.android.clockcalc.Utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
 * mRawOffset:
 *
 */

public class TimeZoneUtils {

    private static final String DEFAULT_DATETIME_FORMAT = "dd/MM/yyyy HH:mm";
    private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";

    /**
     * get current date and time from calendar
     * @return formatted datetime String
     */
    public static String getCurrentDateTime (TimeZone timeZone){
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
        Calendar c = Calendar.getInstance(timeZone);
        String formatted = sdf.format(c.getTime());

        return formatted;
    }

    /**
     * get current date from calendar
     * @return formatted date String
     */
    public static String getCurrentDate (TimeZone timeZone){
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        sdf.setTimeZone(timeZone);
        Calendar c = Calendar.getInstance(timeZone);
        String formatted = sdf.format(c.getTime());

        return formatted;
    }

    /**
     * Get DateTime from custom time
     * @param sourceDateTime source datetime String
     * @return formatted date converted to destination time zone
     */
    public static String getDestinationDateTime(String sourceDateTime, SimpleDateFormat timeFormat,
                                                TimeZone destinationTimeZone) {
        try {
            Date date = timeFormat.parse(sourceDateTime);
            SimpleDateFormat destFormat = timeFormat;
            destFormat.setTimeZone(destinationTimeZone);

            Log.v("Destination DateTime", destFormat.format(date));
            return destFormat.format(date);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String convertTimeToDestTimeZone (int timeInMilis, String destId,
                                                    SimpleDateFormat timeFormat){
        Calendar destTimeZone = new GregorianCalendar(TimeZone.getTimeZone(destId));
        destTimeZone.setTimeInMillis(timeInMilis);

        return timeFormat.format(destTimeZone.getTime());
    }


}
