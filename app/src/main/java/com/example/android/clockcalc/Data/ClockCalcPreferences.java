package com.example.android.clockcalc.Data;


public class ClockCalcPreferences {

    public static final String PREFS_CLOCK_CALC = "ClockCalcPrefs";
    public static final String PREFS_TIME_IN_MILIS = "timeInMilis";
    public static final String PREFS_TIME_ZONE_ID = "timeZoneId";

    public static final String PREFS_TIME_FORMAT = "timeformat";

    public static final int PREFS_TIME_FORMAT_24_H = 0;
    public static final int PREFS_TIME_FORMAT_12_H = 1;

    /**
     * Toggle thime format between 12h and 24h format
     * @param
     * @return new format
     */
    public static int toggleTimeFormat(int format){
        switch (format){
            case PREFS_TIME_FORMAT_12_H:
                return PREFS_TIME_FORMAT_24_H;
            case PREFS_TIME_FORMAT_24_H:
                return PREFS_TIME_FORMAT_12_H;
            default:
                return PREFS_TIME_FORMAT_24_H;
        }
    }
}
