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

    private static final int VIEW_CURRENT = 0;
    private static final int VIEW_CUSTOM = 1;

    private Cursor mCursor;
    private Context mContext;

    public TimeZoneCursorAdapter(Context context){
        mContext = context;
    }

    public class TimeZoneViewHolder extends RecyclerView.ViewHolder{
        TextClock clock;
        TextView timeZoneId;
        TextView date;
        TextView name;

        public TimeZoneViewHolder(View view) {
            super(view);

            clock = view.findViewById(R.id.destClock);
            timeZoneId = view.findViewById(R.id.destTimeZoneId);
            date = view.findViewById(R.id.destDate);
            name = view.findViewById(R.id.destDisplayName);
        }
    }

    @Override
    public TimeZoneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_dest_time, parent,
                false);

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
        String screenName = tz.getDisplayName(false, TimeZone.SHORT);

        holder.itemView.setTag(rowId);

        holder.timeZoneId.setText(id);
        holder.name.setText(screenName);

        String date = TimeZoneUtils.getCurrentDate(tz);
        holder.date.setText(date);
        holder.clock.setTimeZone(id);
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
