package com.example.android.clockcalc;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.android.clockcalc.Data.ClockCalcPreferences;
import com.example.android.clockcalc.Data.TimeZoneContract;
import com.example.android.clockcalc.Utils.TimeZoneUtils;

import java.util.TimeZone;

/**
 * Created by Emils on 16.01.2018.
 */

public class TimeZoneCursorAdapter extends RecyclerView.Adapter<TimeZoneCursorAdapter.TimeZoneViewHolder> {

    private Cursor cursor;
    private Context context;
    private long time;

    /**
     * needed to switch between fragment specific views- TextClock and TextView for displaying time
     */
    private int viewType;

    /**
     *Constructor called from CurrentTimeFragment to display current time for time zones in db.
     *
     * @param context
     * @param viewType  which fragment is being populated
     */
    public TimeZoneCursorAdapter(Context context, int viewType){
        this.context = context;
        this.viewType = viewType;
    }

    /**
     * Constructor called from CustomTimeFragment, specifies time to be applied to timezones in db
     *
     * @param context
     * @param viewType which fragment is being populated
     * @param time UTC miliseconds to be applied to selected time zone
     */
    public TimeZoneCursorAdapter(Context context, int viewType, long time){
        this.context = context;
        this.viewType = viewType;
        this.time = time;
    }

    public class TimeZoneViewHolder extends RecyclerView.ViewHolder{
        TextClock clockTc;
        TextView clockTv;
        TextView timeZoneId;
        TextView date;
        TextView displayName;
        ImageView removeBtn;

        public TimeZoneViewHolder(View view) {
            super(view);

            timeZoneId = view.findViewById(R.id.destTimeZoneId);
            date = view.findViewById(R.id.destDate);
            displayName = view.findViewById(R.id.destDisplayName);
            removeBtn = view.findViewById(R.id.remove);

            switch (viewType) {
                case (TimeZoneContract.TimeZonesEntry.DIFF_CURRENT):
                    clockTc = view.findViewById(R.id.destClockTc);
                    break;
                case (TimeZoneContract.TimeZonesEntry.DIFF_CUSTOM):
                    clockTv = view.findViewById(R.id.destClockTv);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid view type, value of " + viewType);
            }
        }
    }

    @Override
    public TimeZoneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layout;

        switch (this.viewType){
            case (TimeZoneContract.TimeZonesEntry.DIFF_CURRENT):
                layout = R.layout.list_item_dest_current;
                break;
            case (TimeZoneContract.TimeZonesEntry.DIFF_CUSTOM):
                layout = R.layout.list_item_dest_custom;
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + this.viewType);
        }

        View view = LayoutInflater.from(context).inflate(layout, parent,false);

        return new TimeZoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TimeZoneViewHolder holder, final int position) {
        /**
         * indexes  are the same in both tables.
         */
        int timeZoneIdColIndex = cursor.getColumnIndex(TimeZoneContract.TimeZonesEntry.COLUMN_TIME_ZONE_ID);
        int _itIndex = cursor.getColumnIndex(TimeZoneContract.TimeZonesEntry._ID);

        cursor.moveToPosition(position);

        final int rowId = cursor.getInt(_itIndex);
        String id = cursor.getString(timeZoneIdColIndex);
        TimeZone tz= TimeZone.getTimeZone(id);
        String displayName = tz.getDisplayName(false, TimeZone.SHORT);

        holder.itemView.setTag(rowId);

        holder.timeZoneId.setText(id);
        holder.displayName.setText(displayName);

        String date = TimeZoneUtils.getCurrentDate(tz);
        holder.date.setText(date);

        switch (viewType) {
            case (TimeZoneContract.TimeZonesEntry.DIFF_CURRENT):
                holder.clockTc.setTimeZone(id);
                if (MainActivity.prefTimeFormat == ClockCalcPreferences.PREFS_TIME_FORMAT_12_H){
                    holder.clockTc.setFormat12Hour(TimeZoneUtils.TIME_FORMAT_12_H);
                } else {
                    holder.clockTc.setFormat12Hour(TimeZoneUtils.TIME_FORMAT_24_H);
                }
                break;
            case (TimeZoneContract.TimeZonesEntry.DIFF_CUSTOM):
                String time = TimeZoneUtils.getFormattedTime(tz, this.time);
                holder.clockTv.setText(time);
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // delete from db
                // Build appropriate uri with String row id appended
                String stringId = Integer.toString(rowId);
                Uri uri = TimeZoneContract.TimeZonesEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();
                context.getContentResolver().delete(uri, null, null);

                context.getContentResolver().notifyChange(uri, null);
            }
        });

    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (cursor == null) {
            return 0;
        }
        return cursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (cursor)
        if (cursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = cursor;
        this.cursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    public void updateTime(long time){
        this.time = time;
    }
}
