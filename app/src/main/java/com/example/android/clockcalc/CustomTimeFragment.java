package com.example.android.clockcalc;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.clockcalc.Data.TimeZoneContract;
import com.example.android.clockcalc.Utils.TimeZoneUtils;

import java.util.Calendar;
import java.util.TimeZone;

// TODO: fix time displaying
public class CustomTimeFragment extends Fragment implements
        TimeZonePickerFragment.DialogTimeZoneListener,
        TimePickerFragment.DialogTimeListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    // shared preferences
    private static final String PREFS_CLOCK_CALC = "ClockCalcPrefs";
    private static final String PREFS_TIME_IN_MILIS = "timeInMilis";
    private static final String PREFS_TIME_ZONE_ID = "timeZoneId";
    public long timeInMilis;
    private String sourceTimeZoneId;

    private static final String TAG = "CustomTimeFragment";
    private static final String TAG_TIME_PICKER = "timePicker";
    private static final String TAG_TIME_ZONE_PICKER = "timeZonePicker";

    private static final int DB_LOADER = 1;

    private TimeZone sourceTimeZone;

    Calendar sourceCalendar;

    private TimeZoneCursorAdapter cursorAdapter;

    private TextView sourceDateTv;
    private TextView sourceTimeZoneIdTv;
    private TextView sourceDisplayNameTv;
    private TextView sourceTime;
    private FloatingActionButton fab;

    RecyclerView recyclerView;

    public CustomTimeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_custom, container, false);

        sourceDateTv = rootView.findViewById(R.id.sourceDate);
        sourceDisplayNameTv = rootView.findViewById(R.id.sourceDisplayName);
        sourceTimeZoneIdTv = rootView.findViewById(R.id.sourceTimeZoneId);
        sourceTime = rootView.findViewById(R.id.sourceTime);
        fab = rootView.findViewById(R.id.fab);

        recyclerView = rootView.findViewById(R.id.recyclerViewCurrent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // restore preferences
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_CLOCK_CALC, 0);
        timeInMilis = settings.getLong(PREFS_TIME_IN_MILIS, System.currentTimeMillis());
        sourceTimeZoneId = settings.getString(PREFS_TIME_ZONE_ID, TimeZone.getDefault().getID());
        sourceTimeZone = TimeZone.getTimeZone(sourceTimeZoneId);

        // set up source time zone calendar object
        sourceCalendar = Calendar.getInstance(sourceTimeZone);
        sourceCalendar.setTimeInMillis(timeInMilis);

        cursorAdapter = new TimeZoneCursorAdapter(getActivity(), TimeZoneContract.TimeZonesEntry.DIFF_CUSTOM, timeInMilis);
        recyclerView.setAdapter(cursorAdapter);

        // simple format for the default time

        setLocalTimeZoneInfoInUi();

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int id = (int) viewHolder.itemView.getTag();

                // Build appropriate uri with String row id appended
                String stringId = Integer.toString(id);
                Uri uri = TimeZoneContract.TimeZonesEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                // Delete a single row of data using a ContentResolver
                getActivity().getContentResolver().delete(uri, null, null);

                // Restart the loader to re-query for all tasks after a deletion
                getLoaderManager().restartLoader(DB_LOADER, null, CustomTimeFragment.this);

            }
        }).attachToRecyclerView(recyclerView);

        getLoaderManager().initLoader(DB_LOADER, null, this);
        setClickListeners();

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_CLOCK_CALC, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(PREFS_TIME_IN_MILIS, timeInMilis);
        editor.putString(PREFS_TIME_ZONE_ID, sourceTimeZoneId);

        editor.commit();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                TimeZoneContract.TimeZonesEntry._ID,
                TimeZoneContract.TimeZonesEntry.COLUMN_TIME_ZONE_ID,
                TimeZoneContract.TimeZonesEntry.COLUMN_TIME_DIFF};

        String selection = TimeZoneContract.TimeZonesEntry.COLUMN_TIME_DIFF + "=?";

        String[] selectionArgs = {"" + TimeZoneContract.TimeZonesEntry.DIFF_CUSTOM};

        return new CursorLoader(getContext(), TimeZoneContract.TimeZonesEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    @Override
    public void timeZoneSet(String timeZoneId, boolean isSource) {
        if (isSource){
            sourceTimeZoneId = timeZoneId;
            sourceTimeZone = TimeZone.getTimeZone(timeZoneId);
            sourceCalendar.setTimeZone(sourceTimeZone);

            setLocalTimeZoneInfoInUi();

            cursorAdapter.notifyDataSetChanged();

        } else {
            insertTimeZoneInDb(timeZoneId);
        }
    }

    // TODO: fix time string 12:0 ==> 12:00
    @Override
    public void timeSet(long time) {
        timeInMilis = time;
        sourceCalendar.setTimeInMillis(time);
        int hour = sourceCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = sourceCalendar.get(Calendar.MINUTE);
        String timeString = "" + hour + ":" + minute;

        sourceTime.setText(timeString);

        // TODO: update time only, instead of updating all views?
        cursorAdapter.updateTime(timeInMilis);
        cursorAdapter.notifyDataSetChanged();
    }

    private void setLocalTimeZoneInfoInUi (){
        String displayName = sourceTimeZone.getDisplayName(false, TimeZone.SHORT);
        sourceDateTv.setText(TimeZoneUtils.getCurrentDate(sourceTimeZone));

        sourceTime.setText(TimeZoneUtils.getFormattedTime(timeInMilis, getContext()));

        sourceTimeZoneIdTv.setText(sourceTimeZoneId);
        sourceDisplayNameTv.setText(displayName);
    }

    private void showTimeZonePickerDialog(View v, boolean isSource) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(TimeZonePickerFragment.IS_SOURCE, isSource);

        TimeZonePickerFragment timeZonePicker = new TimeZonePickerFragment();
        timeZonePicker.setArguments(bundle);
        timeZonePicker.show(getActivity().getSupportFragmentManager(), TAG_TIME_ZONE_PICKER);
        timeZonePicker.setTimeZoneListener(this);
    }

    private void showTimePickerDialog(View v){
        Bundle bundle = new Bundle();
        bundle.putString(TimePickerFragment.TIME_ZONE_ID, sourceTimeZoneId);

        TimePickerFragment timePicker = new TimePickerFragment();
        timePicker.setArguments(bundle);
        timePicker.show(getActivity().getSupportFragmentManager(), TAG_TIME_PICKER);
        timePicker.setTimeListener(this);
    }

    private void insertTimeZoneInDb(String timeZoneId){
        ContentValues values = new ContentValues();
        values.put(TimeZoneContract.TimeZonesEntry.COLUMN_TIME_ZONE_ID, timeZoneId);
        values.put(TimeZoneContract.TimeZonesEntry.COLUMN_TIME_DIFF, TimeZoneContract.TimeZonesEntry.DIFF_CUSTOM);

        Uri uri = getActivity().getContentResolver().insert(TimeZoneContract.TimeZonesEntry
                .CONTENT_URI, values);

        getLoaderManager().restartLoader(DB_LOADER, null, this);
    }

    private void setClickListeners (){

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeZonePickerDialog(view, false);
            }
        });

        sourceTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        });

        sourceDisplayNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeZonePickerDialog(view, true);
            }
        });

        sourceTimeZoneIdTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeZonePickerDialog(view, true);
            }
        });
    }

    private void logMiliseconds(){
        long utcNowMillis = System.currentTimeMillis();
        Log.i("INFO_MILIS", "utcNowMillis: " + String.valueOf(utcNowMillis));
        Log.i("INFO_MILIS", "utcNowMillis: " + TimeZoneUtils.getFormattedTime(utcNowMillis, getContext()));

        /*
         * This TimeZone represents the device's current time zone. It provides us with a means
         * of acquiring the offset for local time from a UTC time stamp.
         */
        TimeZone currentTimeZone = TimeZone.getDefault();

        /*
         * The getOffset method returns the number of milliseconds to add to UTC time to get the
         * elapsed time since the epoch for our current time zone. We pass the current UTC time
         * into this method so it can determine changes to account for daylight savings time.
         */
        long gmtOffsetMillis = currentTimeZone.getOffset(utcNowMillis);
        Log.i("INFO_MILIS", "gmtOffsetMillis: " + String.valueOf(gmtOffsetMillis));
        Log.i("INFO_MILIS", "gmtOffsetMillis: " + TimeZoneUtils.getFormattedTime(gmtOffsetMillis, getContext()));

        /*
         * UTC time is measured in milliseconds from January 1, 1970 at midnight from the GMT
         * time zone. Depending on your time zone, the time since January 1, 1970 at midnight (GMT)
         * will be greater or smaller. This variable represents the number of milliseconds since
         * January 1, 1970 (GMT) time.
         */
        long timeSinceEpochLocalTimeMillis = utcNowMillis + gmtOffsetMillis;
        Log.i("INFO_MILIS", "timeSinceEpochLocalTimeMillis: " + String.valueOf(timeSinceEpochLocalTimeMillis));
        Log.i("INFO_MILIS", "timeSinceEpochLocalTimeMillis: " + TimeZoneUtils.getFormattedTime(timeSinceEpochLocalTimeMillis, getContext()));
    }
}
