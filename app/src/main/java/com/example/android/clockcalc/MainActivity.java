package com.example.android.clockcalc;

import android.databinding.DataBindingUtil;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.android.clockcalc.databinding.ActivityMainBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static final String DEFAULT_DATETIME_FORMAT = "dd/MM/yyyy HH:mm";

    // placeholder destination time zone
    private static final String DEST_TIME_ZONE = "Africa/El_Aaiun";

    private SimpleDateFormat mDefaultFormat;

    private TimeZone mSourceTimeZone;
    private TimeZone mDestinationTimeZone;

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // simple format for the default time
        mDefaultFormat = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);

        // local time zone
        mSourceTimeZone = TimeZone.getDefault();
        // placeholder time zone
        mDestinationTimeZone = TimeZone.getTimeZone(DEST_TIME_ZONE);

        mDefaultFormat.setTimeZone(mSourceTimeZone);
        setTimeZonesInUi();
        //logTimeZoneInfo();

        String localDateTime = getLocalDateTime();
        setLocalDateTimeInUi(localDateTime);
        setDestDateTimeInUi(getDestinationDate(localDateTime));
    }

    /**
     * get local date and time from calendar
     * @return formatted local datetime String
     */
    private String getLocalDateTime (){
        Calendar c = Calendar.getInstance();
        String formatted = mDefaultFormat.format(c.getTime());

        return formatted;
    }

    /**
     * Convert local time zone datetime to the destination datetime
     * @param localDateTime local datetime String
     * @return formatted date converted to destination time zone
     */
    private String getDestinationDate(String localDateTime) {
        try {
            Date date = mDefaultFormat.parse(localDateTime);
            SimpleDateFormat destFormat = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
            destFormat.setTimeZone(mDestinationTimeZone);

            return destFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * set the destination time zone date and time in TextViews
     *
     * @param dateTime
     */
    private void setDestDateTimeInUi(String dateTime){
        String[] destDateTimeArr = dateTime.split(" ");
        mBinding.destDate.setText(destDateTimeArr[0]);
        mBinding.destTime.setText(destDateTimeArr[1]);
    }

    /**
     * set local date and time in the TextViews
     * @param localDateTime
     */
    private void setLocalDateTimeInUi (String localDateTime){
        String[] localDateTimeArr = localDateTime.split(" ");
        mBinding.sourceDate.setText(localDateTimeArr[0]);
        mBinding.sourceTime.setText(localDateTimeArr[1]);
    }

    /**
     * set time zone ID's in TextViews
     */
    private void setTimeZonesInUi (){
        String sourceId = mSourceTimeZone.getID();
        String sourceDisplayName = mSourceTimeZone.getDisplayName(false, TimeZone.SHORT);
        String destId = mDestinationTimeZone.getID();
        String destDisplayName = mDestinationTimeZone.getDisplayName(false, TimeZone.SHORT);
        mBinding.sourceTimeZoneId.setText(sourceId);
        mBinding.sourceDisplayName.setText(sourceDisplayName);
        mBinding.destTimeZoneId.setText(destId);
        mBinding.destDisplayName.setText(destDisplayName);
    }

    /**
     * Helper method to try out and log different TimeZone methods
     *
     * time zone:           libcore.util.ZoneInfo[id="Europe/Riga",mRawOffset=7200000,mEarliestRawOffset=5794000,mUseDst=true,mDstSavings=3600000,transitions=127]
     * ID:                  Europe/Riga
     * display name:        GMT+02:00
     *
     */

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void logTimeZoneInfo (){

        /*********
         * LOCAL *
         *********/
        TimeZone localTimeZone = TimeZone.getDefault();
        String localTImeZoneId = localTimeZone.getID();
        String localTimeZoneDisplayName = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT);


        Log.v("INFO", "LOCAL TIME ZONE: " + localTimeZone.toString());
        Log.v("INFO", "LOCAL TIME ZONE ID: " + localTImeZoneId);
        Log.v("INFO", "LOCAL TIME ZONE DISPLAY NAME: " + localTimeZoneDisplayName + "\n\n\n");


        /********
         * DEST *
         ********/
        TimeZone destTimeZone = TimeZone.getTimeZone(DEST_TIME_ZONE);
        String destTimeZoneId = destTimeZone.getID();
        String destTimeZoneDisplayName = destTimeZone.getDisplayName(false, TimeZone.SHORT);

        Log.v("INFO", "DEST TIME ZONE: " + destTimeZone.toString());
        Log.v("INFO", "DEST TIME ZONE ID: " + destTimeZoneId);
        Log.v("INFO", "DEST TIME ZONE DISPLAY NAME: " + destTimeZoneDisplayName);
    }

}
