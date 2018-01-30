package com.example.android.clockcalc;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Emils on 15.12.2017.
 */

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public static final String TIME_ZONE_ID = "timeZoneId";
    private String timeZoneId;

    public TimePickerFragment(){

    }

    public interface DialogTimeListener{
        void timeSet(long time);
    }

    DialogTimeListener listener;

    public void setTimeListener(DialogTimeListener listener){
        this.listener = listener;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        timeZoneId = bundle.getString(TIME_ZONE_ID);

        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);

        long time = c.getTimeInMillis();


        listener.timeSet(time);
    }
}
