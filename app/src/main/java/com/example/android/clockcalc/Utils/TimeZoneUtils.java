package com.example.android.clockcalc.Utils;

import android.util.Log;

import com.example.android.clockcalc.Data.ClockCalcPreferences;
import com.example.android.clockcalc.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
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

    public static String getFormattedTime(TimeZone timeZone,
                                          long time){
        SimpleDateFormat sdf;
        int format = MainActivity.prefTimeFormat;

        switch (format){
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
}
