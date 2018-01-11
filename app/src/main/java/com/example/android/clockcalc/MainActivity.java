package com.example.android.clockcalc;


import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextClock;
import android.widget.Toast;

import com.example.android.clockcalc.Utils.TimeZoneUtils;
import com.example.android.clockcalc.databinding.ActivityMainBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements
                                                DatePickerFragment.DialogDateListener,
                                                TimePickerFragment.DialogTimeListener,
                                                TimeZonePickerFragment.DialogTimeZoneListener{

    private static final String DEFAULT_DATETIME_FORMAT = "dd/MM/yyyy HH:mm";

    // placeholder destination time zone
    private static final String DEST_TIME_ZONE = "Africa/El_Aaiun";

    // keys for SavedInstanceState
    private static final String DEST_TIMEZONE_ID_KEY = "destTimeZoneString";

    //private SimpleDateFormat mDefaultFormat;
    private SimpleDateFormat mSourceFormat;
    private SimpleDateFormat mDestinationFormat;

    private TimeZone mSourceTimeZone;
    private TimeZone mDestinationTimeZone;

    private ActivityMainBinding mBinding;

    private View.OnClickListener sourceTimeZoneClickListener;
    private View.OnClickListener destTimeZoneClickListener;
    private View.OnClickListener timeClickListener;
    private View.OnClickListener dateClickListener;

    TextClock mDestClock;

    private boolean hasChangedSourceTimeZone;
    private boolean hasChangedDestTimeZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mDestClock = findViewById(R.id.destTime);

        hasChangedDestTimeZone = false;
        hasChangedSourceTimeZone = false;

        // simple format for the default time
        mSourceFormat = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
        mDestinationFormat = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);

        // local time zone
        mSourceTimeZone = TimeZone.getDefault();

        // placeholder time zone
        mDestinationTimeZone = TimeZone.getTimeZone(DEST_TIME_ZONE);

        mSourceFormat.setTimeZone(mSourceTimeZone);
        mDestinationFormat.setTimeZone(mDestinationTimeZone);
        setInitialTimeZonesInUi();

        String localDateTime = TimeZoneUtils.getCurrentDateTime(mSourceTimeZone, mSourceFormat);
        setLocalDateTimeInUi(localDateTime);
        String destDateTime = TimeZoneUtils.getCurrentDateTime(mDestinationTimeZone, mDestinationFormat);
        setDestDateTimeInUi(destDateTime);

        /*
        // TODO: place hasChangedSourceTimeZone in more appropriate place;
        // set it to true after the time zone actually has been selected
        sourceTimeZoneClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hasChangedSourceTimeZone = true;
                showTimeZonePickerDialog(view);
            }
        };
        */

        // TODO: place hasChangedDestTimeZone in more appropriate place;
        // set it to true after the time zone actually has been selected
        destTimeZoneClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hasChangedDestTimeZone = true;
                showTimeZonePickerDialog(view);
            }
        };

        /*
        timeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        };

        dateClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        };
        */
        setAllClickListeners();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(DEST_TIMEZONE_ID_KEY, String.valueOf(mDestinationTimeZone.getID()));
        Log.v("TEST outState", String.valueOf(mDestinationTimeZone.getID()));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        String id = savedInstanceState.getString(DEST_TIMEZONE_ID_KEY);
        TimeZone tz = TimeZone.getTimeZone(id);
        mDestinationTimeZone = tz;
        mDestClock.setTimeZone(id);
        setSelectedTimeZoneInUi(id);
    }

    private void setAllClickListeners (){
        // Time zones
        //mBinding.sourceTimeZoneField.setOnClickListener(sourceTimeZoneClickListener);
        mBinding.destTimeZoneField.setOnClickListener(destTimeZoneClickListener);

        // time
        //mBinding.sourceTime.setOnClickListener(timeClickListener);
        //mBinding.destTime.setOnClickListener(timeClickListener);

        // date
        //mBinding.sourceDate.setOnClickListener(dateClickListener);
        //mBinding.destDate.setOnClickListener(dateClickListener);
    }

    /**
     * set the destination time zone date and time in TextViews
     *
     * @param dateTime
     */
    private void setDestDateTimeInUi(String dateTime){
        String[] destDateTimeArr = dateTime.split(" ");
        mBinding.destDate.setText(destDateTimeArr[0]);
        //mBinding.destTime.setText(destDateTimeArr[1]);
        mDestClock.setTimeZone(mDestinationTimeZone.getID());
    }

    /**
     * set local date and time in the TextViews
     * @param localDateTime
     */
    private void setLocalDateTimeInUi (String localDateTime){
        String[] localDateTimeArr = localDateTime.split(" ");
        mBinding.sourceDate.setText(localDateTimeArr[0]);
        //mBinding.sourceTime.setText(localDateTimeArr[1]);
    }

    /**
     * set time zone ID's in TextViews
     */
    private void setInitialTimeZonesInUi (){
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
     * Set the users selected time zones in ui
     */
    private void setSelectedTimeZoneInUi(String id){
        TimeZone timeZone = TimeZone.getTimeZone(id);
        String displayName = timeZone.getDisplayName(false, TimeZone.SHORT);

        if (hasChangedSourceTimeZone){
            mSourceTimeZone = timeZone;
            mSourceFormat.setTimeZone(mSourceTimeZone);

            mBinding.sourceTimeZoneId.setText(id);
            mBinding.sourceDisplayName.setText(displayName);

            hasChangedSourceTimeZone = false;
        } else {
            mDestinationTimeZone = timeZone;
            mDestinationFormat.setTimeZone(mDestinationTimeZone);

            mBinding.destTimeZoneId.setText(id);
            mBinding.destDisplayName.setText(displayName);

            hasChangedDestTimeZone = false;
        }
    }

    public void showTimePickerDialog(View v) {
        TimePickerFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "timePicker");
        timePicker.setTimeListener(this);
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
        datePicker.setDateListener(this);
    }

    public void showTimeZonePickerDialog(View v) {
        TimeZonePickerFragment timeZonePicker = new TimeZonePickerFragment();
        timeZonePicker.show(getSupportFragmentManager(), "timeZonePicker");
        timeZonePicker.setTimeZoneListener(this);
    }

    @Override
    public void dateSet(String date) {
        mBinding.sourceDate.setText(date);
    }

    @Override
    public void timeSet(String time) {
        mBinding.sourceTime.setText(time);
    }

    @Override
    public void timeZoneSet(String timeZoneId) {
        //Toast.makeText(this, timeZoneId, Toast.LENGTH_SHORT).show();
        setSelectedTimeZoneInUi(timeZoneId);

        /*
        String sourceTime = String.valueOf(mBinding.sourceTime.getText());
        String sourceDate = String.valueOf(mBinding.sourceDate.getText());
        String localDateTime = sourceDate + " " + sourceTime;
        */

        String destTime = TimeZoneUtils.getCurrentDateTime(mDestinationTimeZone, mDestinationFormat);
        setDestDateTimeInUi(destTime);
    }
}
