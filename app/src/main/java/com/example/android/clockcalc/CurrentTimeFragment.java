package com.example.android.clockcalc;

import android.content.SharedPreferences;
import android.support.v4.app.LoaderManager;
import android.content.ContentValues;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.android.clockcalc.Data.ClockCalcPreferences;
import com.example.android.clockcalc.Data.TimeZoneContract;
import com.example.android.clockcalc.Utils.TimeZoneUtils;

import java.util.TimeZone;

public class CurrentTimeFragment extends Fragment implements
        TimeZonePickerFragment.DialogTimeZoneListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "CurrentTimeFragment";

    private static final int DB_LOADER = 1;

    private TimeZone mSourceTimeZone;

    private TimeZoneCursorAdapter cursorAdapter;

    private TextView sourceDateTv;
    private TextView sourceTimeZoneIdTv;
    private TextView sourceDisplayNameTv;
    private TextClock sourceTime;
    FloatingActionButton fab;

    RecyclerView recyclerView;

    SharedPreferences settings;

        public CurrentTimeFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current, container, false);



        sourceDateTv = rootView.findViewById(R.id.sourceDate);
        sourceDisplayNameTv = rootView.findViewById(R.id.sourceDisplayName);
        sourceTimeZoneIdTv = rootView.findViewById(R.id.sourceTimeZoneId);
        sourceTime = rootView.findViewById(R.id.sourceTime);
        fab = rootView.findViewById(R.id.fab);

        settings = getActivity().getSharedPreferences(ClockCalcPreferences.PREFS_CLOCK_CALC, 0);
        settings.registerOnSharedPreferenceChangeListener(this);

        recyclerView = rootView.findViewById(R.id.recyclerViewCurrent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        cursorAdapter = new TimeZoneCursorAdapter(getActivity(), TimeZoneContract.TimeZonesEntry.DIFF_CURRENT);
        recyclerView.setAdapter(cursorAdapter);

        // local time zone
        mSourceTimeZone = TimeZone.getDefault();

        setLocalTimeZoneInfoInUi();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeZonePickerDialog(view, true);
            }
        });
        getLoaderManager().initLoader(DB_LOADER, null, this);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        settings.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        settings.unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @Override
    public void timeZoneSet(String timeZoneId, boolean isCurrent) {
        insertTimeZoneInDb(timeZoneId);
    }

    /**
     * Instantiates and returns a new AsyncTaskLoader with the given ID.
     * This loader will return task data as a Cursor or null if an error occurs.
     *
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = new String[]{
                TimeZoneContract.TimeZonesEntry._ID,
                TimeZoneContract.TimeZonesEntry.COLUMN_TIME_ZONE_ID,
                TimeZoneContract.TimeZonesEntry.COLUMN_TIME_DIFF};

        String selection = TimeZoneContract.TimeZonesEntry.COLUMN_TIME_DIFF + "=?";

        String[] selectionArgs = {"" + TimeZoneContract.TimeZonesEntry.DIFF_CURRENT};


        return new CursorLoader(getContext(), TimeZoneContract.TimeZonesEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    private void insertTimeZoneInDb(String timeZoneId){
        ContentValues values = new ContentValues();
        values.put(TimeZoneContract.TimeZonesEntry.COLUMN_TIME_ZONE_ID, timeZoneId);
        values.put(TimeZoneContract.TimeZonesEntry.COLUMN_TIME_DIFF, TimeZoneContract.TimeZonesEntry.DIFF_CURRENT);

        Uri uri = getActivity().getContentResolver().insert(TimeZoneContract.TimeZonesEntry
                .CONTENT_URI, values);

        getLoaderManager().restartLoader(DB_LOADER, null, this);
    }

    /**
     * set local date and time in the TextViews
     */
    private void setLocalTimeZoneInfoInUi (){
        String id = mSourceTimeZone.getID();
        String displayName = mSourceTimeZone.getDisplayName(false, TimeZone.SHORT);
        sourceDateTv.setText(TimeZoneUtils.getCurrentDate(mSourceTimeZone));
        sourceTime.setTimeZone(id);

        if (MainActivity.prefTimeFormat == ClockCalcPreferences.PREFS_TIME_FORMAT_12_H){
            sourceTime.setFormat12Hour(TimeZoneUtils.TIME_FORMAT_12_H);
        } else {
            sourceTime.setFormat12Hour(TimeZoneUtils.TIME_FORMAT_24_H);
        }

        sourceTimeZoneIdTv.setText(id);
        sourceDisplayNameTv.setText(displayName);
    }

    public void showTimeZonePickerDialog(View v, boolean isSource) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(TimeZonePickerFragment.IS_SOURCE, isSource);

        TimeZonePickerFragment timeZonePicker = new TimeZonePickerFragment();
        timeZonePicker.setArguments(bundle);
        timeZonePicker.show(getActivity().getSupportFragmentManager(), "timeZonePicker");
        timeZonePicker.setTimeZoneListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (android.text.format.DateFormat.is24HourFormat(getContext())){
            if (MainActivity.prefTimeFormat == ClockCalcPreferences.PREFS_TIME_FORMAT_12_H){
                sourceTime.setFormat24Hour(TimeZoneUtils.TIME_FORMAT_12_H);
            } else {
                sourceTime.setFormat24Hour(TimeZoneUtils.TIME_FORMAT_24_H);
            }
        } else {
            if (MainActivity.prefTimeFormat == ClockCalcPreferences.PREFS_TIME_FORMAT_12_H){
                sourceTime.setFormat12Hour(TimeZoneUtils.TIME_FORMAT_12_H);
            } else {
                sourceTime.setFormat12Hour(TimeZoneUtils.TIME_FORMAT_12_H);
            }
        }

        if (MainActivity.prefTimeFormat == ClockCalcPreferences.PREFS_TIME_FORMAT_12_H){
            sourceTime.setFormat12Hour(TimeZoneUtils.TIME_FORMAT_12_H);
        } else {
            sourceTime.setFormat12Hour(TimeZoneUtils.TIME_FORMAT_24_H);
        }

    }
}
