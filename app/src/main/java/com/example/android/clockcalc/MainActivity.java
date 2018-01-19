package com.example.android.clockcalc;


import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.android.clockcalc.Data.TimeZoneContract;
import com.example.android.clockcalc.Data.TimeZoneDbHelper;
import com.example.android.clockcalc.Utils.TimeZoneUtils;

import java.text.SimpleDateFormat;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity implements
                                                DatePickerFragment.DialogDateListener,
                                                TimePickerFragment.DialogTimeListener,
                                                TimeZonePickerFragment.DialogTimeZoneListener,
                                                LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int DB_LOADER = 1;

    private static final String DEFAULT_DATETIME_FORMAT = "dd/MM/yyyy HH:mm";

    private SimpleDateFormat mSourceFormat;
    private TimeZone mSourceTimeZone;

    private TimeZoneCursorAdapter cursorAdapter;

    private TextView sourceDateTv;
    private TextView sourceTimeZoneIdTv;
    private TextView sourceDisplayNameTv;
    private TextClock sourceTime;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0f);

        sourceDateTv = findViewById(R.id.sourceDate);
        sourceDisplayNameTv = findViewById(R.id.sourceDisplayName);
        sourceTimeZoneIdTv = findViewById(R.id.sourceTimeZoneId);
        sourceTime = findViewById(R.id.sourceTime);

        recyclerView = findViewById(R.id.recyclerViewCurrent);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cursorAdapter = new TimeZoneCursorAdapter(this);
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
                getContentResolver().delete(uri, null, null);

                // Restart the loader to re-query for all tasks after a deletion
                getLoaderManager().restartLoader(DB_LOADER, null, MainActivity.this);

            }
        }).attachToRecyclerView(recyclerView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeZonePickerDialog(view);
            }
        });

        getLoaderManager().initLoader(DB_LOADER, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // re-queries for all tasks
        getLoaderManager().restartLoader(DB_LOADER, null, this);
    }

    // displayed in UI
    @Override
    public void timeZoneSet(String timeZoneId) {
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
        Log.i("INFO", "onCreateLoader");
        String[] projection = {
                TimeZoneContract.CurrentEntry._ID,
                TimeZoneContract.CurrentEntry.CONTENT_LIST_TYPE};

        return new CursorLoader(this, TimeZoneContract.CurrentEntry.CONTENT_URI, projection,
                null, null, null);
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i("INFO", "onLoadFinished");
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    @Override
    public void dateSet(String date) {
        //mBinding.sourceDate.setText(date);
    }

    @Override
    public void timeSet(String time) {
        //mBinding.sourceTime.setText(time);
    }

    /**
     * set local date and time in the TextViews
     */
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
        timeZonePicker.show(getSupportFragmentManager(), "timeZonePicker");
        timeZonePicker.setTimeZoneListener(this);
    }

    private void insertTimeZoneInDb(String timeZoneId){
        ContentValues values = new ContentValues();
        values.put(TimeZoneContract.CurrentEntry.COLUMN_TIME_ZONE_ID, timeZoneId);

        Uri uri = getContentResolver().insert(TimeZoneContract.CurrentEntry.CONTENT_URI, values);

        getLoaderManager().restartLoader(DB_LOADER, null, this);
    }
}
