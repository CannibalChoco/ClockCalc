package com.example.android.clockcalc.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
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
    private static final String DEFAULT_TIME_FORMAT = "HH:mm";


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

    public static String getFormattedDestTime (TimeZone timeZone,
                                               long time){
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
        sdf.setTimeZone(timeZone);

        Calendar c = Calendar.getInstance(timeZone);
        c.setTimeInMillis(time);

        String formatted = sdf.format(c.getTime());

        return formatted;
    }

    /**
     * Format time in miliseconds into a String
     */
    public static String getFormattedTime (long milis, Context context){
        int flags = DateUtils.FORMAT_SHOW_TIME;
        return DateUtils.formatDateTime(context, milis, flags);
    }

    /**
     * get current date and time from calendar
     * @return formatted datetime String
     */
    public static String getCurrentDateTime (TimeZone timeZone){
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
        sdf.setTimeZone(timeZone);
        Calendar c = Calendar.getInstance(timeZone);
        String formatted = sdf.format(c.getTime());

        return formatted;
    }

}
