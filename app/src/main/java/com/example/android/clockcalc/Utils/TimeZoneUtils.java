package com.example.android.clockcalc.Utils;

import android.content.Context;
import android.text.format.DateUtils;

import com.example.android.clockcalc.Data.ClockCalcPreferences;
import com.example.android.clockcalc.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

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

    private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    public static final String TIME_FORMAT_24_H = "HH:mm";
    public static final String TIME_FORMAT_12_H = "h:mm a";


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

    public static String getFormattedCustomTime(TimeZone timeZone,
                                          long time){
        SimpleDateFormat sdf;
        int prefTimeFormat = MainActivity.prefTimeFormat;

        switch (prefTimeFormat){
            case ClockCalcPreferences.PREFS_TIME_FORMAT_12_H:
                sdf= new SimpleDateFormat(TIME_FORMAT_12_H);
                break;
            case ClockCalcPreferences.PREFS_TIME_FORMAT_24_H:
                sdf = new SimpleDateFormat(TIME_FORMAT_24_H);
                break;
            default:
                sdf = new SimpleDateFormat(TIME_FORMAT_24_H);
                break;
        }

        sdf.setTimeZone(timeZone);

        Calendar c = Calendar.getInstance(timeZone);
        c.setTimeInMillis(time);

        String formatted = sdf.format(c.getTime());

        return formatted;
    }

    public static String getFormattedLocalTime(TimeZone timeZone,
                                          long time, Context context){
//        SimpleDateFormat sdf;
//        int prefTimeFormat = MainActivity.prefTimeFormat;
//
//        switch (prefTimeFormat){
//            case ClockCalcPreferences.PREFS_TIME_FORMAT_12_H:
//                sdf= new SimpleDateFormat(TIME_FORMAT_12_H);
//                break;
//            case ClockCalcPreferences.PREFS_TIME_FORMAT_24_H:
//                sdf = new SimpleDateFormat(TIME_FORMAT_24_H);
//                break;
//            default:
//                sdf = new SimpleDateFormat(TIME_FORMAT_24_H);
//                break;
//        }
//
//        sdf.setTimeZone(timeZone);
//
//        Calendar c = Calendar.getInstance(timeZone);
//        c.setTimeInMillis(time);
//
//        String formatted = sdf.format(c.getTime());
//
//        return formatted;

        Calendar c = Calendar.getInstance(timeZone);
        c.setTimeInMillis(time);

        return DateUtils.formatDateTime(context, c.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
    }
}
