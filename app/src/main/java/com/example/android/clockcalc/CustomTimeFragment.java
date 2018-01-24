package com.example.android.clockcalc;

import android.content.ContentValues;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.android.clockcalc.Data.TimeZoneContract;
import com.example.android.clockcalc.Utils.TimeZoneUtils;

import java.text.SimpleDateFormat;
import java.util.TimeZone;


public class CustomTimeFragment extends Fragment implements
        TimeZonePickerFragment.DialogTimeZoneListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "CustomTimeFragment";

    private static final int DB_LOADER = 2;

    private static final String DEFAULT_DATETIME_FORMAT = "dd/MM/yyyy HH:mm";

    private SimpleDateFormat mSourceFormat;
    private TimeZone mSourceTimeZone;

    private TimeZoneCursorAdapter cursorAdapter;

    private TextView sourceDateTv;
    private TextView sourceTimeZoneIdTv;
    private TextView sourceDisplayNameTv;
    private TextClock sourceTime;

    RecyclerView recyclerView;

    public CustomTimeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_view, container, false);


        /////////////////

        sourceDateTv = rootView.findViewById(R.id.sourceDate);
        sourceDisplayNameTv = rootView.findViewById(R.id.sourceDisplayName);
        sourceTimeZoneIdTv = rootView.findViewById(R.id.sourceTimeZoneId);
        sourceTime = rootView.findViewById(R.id.sourceTime);

        recyclerView = rootView.findViewById(R.id.recyclerViewCurrent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        cursorAdapter = new TimeZoneCursorAdapter(getActivity());
        recyclerView.setAdapter(cursorAdapter);

        // simple format for the default time
        mSourceFormat = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
        // local time zone
        mSourceTimeZone = TimeZone.getDefault();

        mSourceFormat.setTimeZone(mSourceTimeZone);
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
                Uri uri = TimeZoneContract.CurrentEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                // Delete a single row of data using a ContentResolver
                getActivity().getContentResolver().delete(uri, null, null);

                // Restart the loader to re-query for all tasks after a deletion
                getLoaderManager().restartLoader(DB_LOADER, null, CustomTimeFragment.this);

            }
        }).attachToRecyclerView(recyclerView);

        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeZonePickerDialog(view);
            }
        });
        getLoaderManager().initLoader(DB_LOADER, null, this);

        /////////////////

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        String[] projection = new String[]{
//                TimeZoneContract.CustomEntry._ID,
//                TimeZoneContract.CustomEntry.COLUMN_TIME_ZONE_ID};
//
//        return new CursorLoader(getContext(), TimeZoneContract.CustomEntry.CONTENT_URI,
//                projection,
//                null,
//                null,
//                null);
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        cursorAdapter.swapCursor(null);
    }

    @Override
    public void timeZoneSet(String timeZoneId) {
        insertTimeZoneInDb(timeZoneId);
    }

    private void setLocalTimeZoneInfoInUi (){
        String id = mSourceTimeZone.getID();
        String displayName = mSourceTimeZone.getDisplayName(false, TimeZone.SHORT);
        sourceDateTv.setText(TimeZoneUtils.getCurrentDate(mSourceTimeZone));
        sourceTime.setTimeZone(id);

        sourceTimeZoneIdTv.setText(id);
        sourceDisplayNameTv.setText(displayName);
    }

    public void showTimeZonePickerDialog(View v) {
        TimeZonePickerFragment timeZonePicker = new TimeZonePickerFragment();
        timeZonePicker.show(getActivity().getSupportFragmentManager(), "timeZonePicker");
        timeZonePicker.setTimeZoneListener(this);
    }

    private void insertTimeZoneInDb(String timeZoneId){
        ContentValues values = new ContentValues();
        values.put(TimeZoneContract.CurrentEntry.COLUMN_TIME_ZONE_ID, timeZoneId);

        Uri uri = getActivity().getContentResolver().insert(TimeZoneContract.CustomEntry.CONTENT_URI, values);

        getLoaderManager().restartLoader(DB_LOADER, null, this);
    }
}
