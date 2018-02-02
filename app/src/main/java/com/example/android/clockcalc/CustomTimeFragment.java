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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.clockcalc.Data.ClockCalcPreferences;
import com.example.android.clockcalc.Data.TimeZoneContract;
import com.example.android.clockcalc.Utils.TimeZoneUtils;

import java.util.Calendar;
import java.util.TimeZone;

// TODO: update time format when user changes preferences
public class CustomTimeFragment extends Fragment implements
        TimeZonePickerFragment.DialogTimeZoneListener,
        TimePickerFragment.DialogTimeListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    // shared settings
    public long timeInMilis;
    private String sourceTimeZoneId;

    private static final String TAG = "CustomTimeFragment";
    private static final String TAG_TIME_PICKER = "timePicker";
    private static final String TAG_TIME_ZONE_PICKER = "timeZonePicker";

    private static final int DB_LOADER = 1;

    private TimeZone sourceTimeZone;
    private Calendar sourceCalendar;
    private String sourceTimeString;

    private TimeZoneCursorAdapter cursorAdapter;

    private TextView sourceDateTv;
    private TextView sourceTimeZoneIdTv;
    private TextView sourceDisplayNameTv;
    private TextView sourceTime;
    private FloatingActionButton fab;

    RecyclerView recyclerView;

    SharedPreferences settings;

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

        // restore settings
        settings = getActivity().getSharedPreferences(ClockCalcPreferences.PREFS_CLOCK_CALC, 0);
        timeInMilis = settings.getLong(ClockCalcPreferences.PREFS_TIME_IN_MILIS, System.currentTimeMillis());
        sourceTimeZoneId = settings.getString(ClockCalcPreferences.PREFS_TIME_ZONE_ID, TimeZone.getDefault().getID());
        sourceTimeZone = TimeZone.getTimeZone(sourceTimeZoneId);

        // set up source time zone calendar object
        sourceCalendar = Calendar.getInstance(sourceTimeZone);
        sourceCalendar.setTimeInMillis(timeInMilis);

        sourceTimeString = TimeZoneUtils.getFormattedTime(sourceTimeZone, timeInMilis);

        cursorAdapter = new TimeZoneCursorAdapter(getActivity(), TimeZoneContract.TimeZonesEntry.DIFF_CUSTOM, timeInMilis);
        recyclerView.setAdapter(cursorAdapter);

        // simple format for the default time

        setLocalTimeZoneInfoInUi();

        getLoaderManager().initLoader(DB_LOADER, null, this);
        setClickListeners();

        return rootView;
    }


    @Override
    public void onStop() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(ClockCalcPreferences.PREFS_TIME_IN_MILIS, timeInMilis);
        editor.putString(ClockCalcPreferences.PREFS_TIME_ZONE_ID, sourceTimeZoneId);

        editor.apply();
        editor.commit();

        super.onStop();
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
            updateTimeZone(timeZoneId);
            updateTimeMilis();
            setLocalTimeZoneInfoInUi();
            cursorAdapter.updateTime(timeInMilis);
            cursorAdapter.notifyDataSetChanged();

        } else {
            insertTimeZoneInDb(timeZoneId);
        }
    }

    @Override
    public void timeSet(long time) {
        timeInMilis = time;
        sourceCalendar.setTimeInMillis(time);

        sourceTimeString = TimeZoneUtils.getFormattedTime(sourceTimeZone, time);

        sourceTime.setText(sourceTimeString);

        // TODO: update time only, instead of updating all views?
        cursorAdapter.updateTime(timeInMilis);
        cursorAdapter.notifyDataSetChanged();
    }

    private void updateTimeMilis (){
        String[] hoursAndMinutes = sourceTimeString.split(":");

        int hour = Integer.valueOf(hoursAndMinutes[0]);
        int minute = Integer.valueOf(hoursAndMinutes[1]);

        Calendar c = Calendar.getInstance(sourceTimeZone);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);

        timeInMilis = c.getTimeInMillis();

        sourceCalendar.setTimeInMillis(timeInMilis);
    }

    private void updateTimeZone(String timeZoneId){
        sourceTimeZoneId = timeZoneId;
        sourceTimeZone = TimeZone.getTimeZone(timeZoneId);
        sourceCalendar.setTimeZone(sourceTimeZone);
    }

    private void setLocalTimeZoneInfoInUi (){
        String displayName = sourceTimeZone.getDisplayName(false, TimeZone.SHORT);
        sourceDateTv.setText(TimeZoneUtils.getCurrentDate(sourceTimeZone));

        sourceTime.setText(TimeZoneUtils.getFormattedTime(sourceTimeZone, timeInMilis));

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
}
