package com.example.android.clockcalc;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.DatePickerDialog;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public DatePickerFragment(){
    }


    public interface DialogDateListener{
        void dateSet(String date);
    }

    DialogDateListener listener;

    public void setDateListener(DialogDateListener listener){
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String date = String.valueOf(day) + "/" +
                        String.valueOf(month + 1) + "/" +
                        String.valueOf(year);

        listener.dateSet(date);

        Log.i("INFO", "onDateSet: " + date);
    }
}
