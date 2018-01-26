package com.example.android.clockcalc;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.android.clockcalc.Data.TimeZoneContract;
import com.example.android.clockcalc.Utils.TimeZoneUtils;

import java.util.TimeZone;

/**
 * Created by Emils on 16.01.2018.
 */

public class TimeZoneCursorAdapter extends RecyclerView.Adapter<TimeZoneCursorAdapter.TimeZoneViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    private int mView;

    public TimeZoneCursorAdapter(Context context, int viewType){
        mContext = context;
        mView = viewType;
    }

    public class TimeZoneViewHolder extends RecyclerView.ViewHolder{
        TextClock clockTc;
        TextView clockTv;
        TextView timeZoneId;
        TextView date;
        TextView displayName;

        public TimeZoneViewHolder(View view) {
            super(view);

            timeZoneId = view.findViewById(R.id.destTimeZoneId);
            date = view.findViewById(R.id.destDate);
            displayName = view.findViewById(R.id.destDisplayName);

            switch (mView) {
                case (TimeZoneContract.TimeZonesEntry.DIFF_CURRENT):
                    clockTc = view.findViewById(R.id.destClockTc);
                    break;
                case (TimeZoneContract.TimeZonesEntry.DIFF_CUSTOM):
                    clockTv = view.findViewById(R.id.destClockTv);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid view type, value of " + mView);
            }

        }
    }

    @Override
    public TimeZoneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layout;

        switch (mView){
            case (TimeZoneContract.TimeZonesEntry.DIFF_CURRENT):
                layout = R.layout.list_item_dest_current;
                break;
            case (TimeZoneContract.TimeZonesEntry.DIFF_CUSTOM):
                layout = R.layout.list_item_dest_custom;
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + mView);
        }

        View view = LayoutInflater.from(mContext).inflate(layout, parent,false);

        return new TimeZoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TimeZoneViewHolder holder, int position) {
        /**
         * indexes  are the same in both tables.
         */
        int timeZoneIdColIndex = mCursor.getColumnIndex(TimeZoneContract.TimeZonesEntry.COLUMN_TIME_ZONE_ID);
        int _itIndex = mCursor.getColumnIndex(TimeZoneContract.TimeZonesEntry._ID);

        mCursor.moveToPosition(position);

        int rowId = mCursor.getInt(_itIndex);
        String id = mCursor.getString(timeZoneIdColIndex);
        TimeZone tz= TimeZone.getTimeZone(id);
        String displayName = tz.getDisplayName(false, TimeZone.SHORT);

        holder.itemView.setTag(rowId);

        holder.timeZoneId.setText(id);
        holder.displayName.setText(displayName);

        String date = TimeZoneUtils.getCurrentDate(tz);
        holder.date.setText(date);

        switch (mView) {
            case (TimeZoneContract.TimeZonesEntry.DIFF_CURRENT):
                holder.clockTc.setTimeZone(id);
                break;
            case (TimeZoneContract.TimeZonesEntry.DIFF_CUSTOM):
                // TODO: get and display time in TextView
                holder.clockTv.setText("55:55");
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + mView);
        }
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }
}
