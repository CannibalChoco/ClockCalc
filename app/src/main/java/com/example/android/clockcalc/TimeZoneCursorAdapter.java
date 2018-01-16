package com.example.android.clockcalc;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextClock;

import com.example.android.clockcalc.Data.TimeZoneContract;
import com.example.android.clockcalc.Utils.TimeZoneUtils;
import com.example.android.clockcalc.databinding.ListItemDestCurrentBinding;

import java.util.TimeZone;

/**
 * Created by Emils on 16.01.2018.
 */

public class TimeZoneCursorAdapter extends CursorAdapter {

    public TimeZoneCursorAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ListItemDestCurrentBinding binding = ListItemDestCurrentBinding.inflate(LayoutInflater.from(
                context), parent, false);
        return binding.getRoot();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ListItemDestCurrentBinding binding = DataBindingUtil.getBinding(view);

        int timeZoneIdColIndex = cursor.getColumnIndex(TimeZoneContract.CurrentEntry.COLUMN_TIME_ZONE_ID);
        String id = cursor.getString(timeZoneIdColIndex);

        TimeZone tz= TimeZone.getTimeZone(id);
        String screenName = tz.getDisplayName();

        binding.destTimeZoneId.setText(id);
        binding.destDisplayName.setText(screenName);

        binding.destDate.setText(TimeZoneUtils.getCurrentDate(tz));
        binding.destClock.setTimeZone(id);
    }
}
