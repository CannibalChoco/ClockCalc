package com.example.android.clockcalc;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Emils on 15.12.2017.
 */

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public TimePickerFragment(){

    }

    public interface DialogTimeListener{
        void timeSet(String time);
    }

    DialogTimeListener listener;

    public void setTimeListener(DialogTimeListener listener){
        this.listener = listener;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
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
        String time = String.valueOf(hour) + ":" +
                String.valueOf(minute);

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        try {
            Date date = sdf.parse(time);
            String formattedTime = sdf.format(date);
            listener.timeSet(formattedTime);
            Log.i("INFO", "onTimeSet: " + formattedTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
