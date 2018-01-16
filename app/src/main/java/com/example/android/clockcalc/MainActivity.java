package com.example.android.clockcalc;


import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.Toast;

import com.example.android.clockcalc.Data.TimeZoneContract;
import com.example.android.clockcalc.Data.TimeZoneDbHelper;
import com.example.android.clockcalc.Utils.TimeZoneUtils;
import com.example.android.clockcalc.databinding.ActivityMainBinding;
import com.example.android.clockcalc.databinding.DefaultTimezoneCurrentBinding;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * MainActivity
 */
// TODO: add fab
// TODO: on fab click, open timezonePickerFragment and put the selected timeZone in db

public class MainActivity extends AppCompatActivity implements
                                                DatePickerFragment.DialogDateListener,
                                                TimePickerFragment.DialogTimeListener,
                                                TimeZonePickerFragment.DialogTimeZoneListener,
                                                LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int DB_LOADER = 1;

    private static final String DEFAULT_DATETIME_FORMAT = "dd/MM/yyyy HH:mm";

    // placeholder destination time zone
    private static final String DEST_TIME_ZONE = "Africa/El_Aaiun";

    // keys for SavedInstanceState
    private static final String DEST_TIMEZONE_ID_KEY = "destTimeZoneString";

    private SimpleDateFormat mSourceFormat;
    private TimeZone mSourceTimeZone;

    private DefaultTimezoneCurrentBinding mDefaultTimeZoneBinding;
    private ActivityMainBinding mActivityMainBinding;

    private View.OnClickListener sourceTimeZoneClickListener;
    private View.OnClickListener destTimeZoneClickListener;
    private View.OnClickListener timeClickListener;
    private View.OnClickListener dateClickListener;

    private TimeZoneDbHelper dbHelper;
    private TimeZoneCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDefaultTimeZoneBinding = DataBindingUtil.setContentView(this,
                R.layout.default_timezone_current);
        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        dbHelper = new TimeZoneDbHelper(this);
        cursorAdapter = new TimeZoneCursorAdapter(this, null);
        ListView listView = mActivityMainBinding.listViewCurrent;
        listView.setAdapter(cursorAdapter);

        // simple format for the default time
        mSourceFormat = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);

        // local time zone
        mSourceTimeZone = TimeZone.getDefault();

        mSourceFormat.setTimeZone(mSourceTimeZone);
        setInitialTimeZonesInUi();

        FloatingActionButton fab = mActivityMainBinding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertTimeZoneInDb(DEST_TIME_ZONE);
                insertTimeZoneInDb("Europe/Riga");
            }
        });

        setLocalDateTimeInUi(TimeZoneUtils.getCurrentDateTime(mSourceTimeZone));

        getLoaderManager().initLoader(DB_LOADER, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // re-queries for all tasks
        getLoaderManager().restartLoader(DB_LOADER, null, this);
    }

    // TODO: refactor after db is set up and working
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        /*
        outState.putString(DEST_TIMEZONE_ID_KEY, String.valueOf(mDestinationTimeZone.getID()));
        Log.v("TEST outState", String.valueOf(mDestinationTimeZone.getID()));

        */
        super.onSaveInstanceState(outState);
    }

    // TODO: refactor after db is set up and working
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        /*
        String id = savedInstanceState.getString(DEST_TIMEZONE_ID_KEY);
        TimeZone tz = TimeZone.getTimeZone(id);
        mDestinationTimeZone = tz;
        //mDestClock.setTimeZone(id);
        setSelectedTimeZoneInUi(id);
        */
    }

    private void setAllClickListeners (){

        /*
        // TODO: place hasChangedSourceTimeZone in more appropriate place;
        /* set it to true after the time zone actually has been selected,
        instead of when the dialog is launched */
        /*
        sourceTimeZoneClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hasChangedSourceTimeZone = true;
                showTimeZonePickerDialog(view);
            }
        };
        */

        // TODO: place hasChangedDestTimeZone in more appropriate place;
        /* set it to true after the time zone actually has been selected,
        instead of when the dialog is launched */
        /*
        destTimeZoneClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hasChangedDestTimeZone = true;
                showTimeZonePickerDialog(view);
            }
        };

        /*
        timeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        };

        dateClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        };
        */

        // Time zones
        //mBinding.sourceTimeZoneField.setOnClickListener(sourceTimeZoneClickListener);
        //mBinding.destTimeZoneField.setOnClickListener(destTimeZoneClickListener);

        // time
        //mBinding.sourceTime.setOnClickListener(timeClickListener);
        //mBinding.destTime.setOnClickListener(timeClickListener);

        // date
        //mBinding.sourceDate.setOnClickListener(dateClickListener);
        //mBinding.destDate.setOnClickListener(dateClickListener);
    }

    /**
     * set local date and time in the TextViews
     * @param localDateTime
     */
    private void setLocalDateTimeInUi (String localDateTime){
        String[] localDateTimeArr = localDateTime.split(" ");

        mDefaultTimeZoneBinding.sourceDate.setText(localDateTimeArr[0]);
        mDefaultTimeZoneBinding.sourceTime.setText(localDateTimeArr[1]);
    }

    /**
     * set time zone ID's in TextViews
     */
    private void setInitialTimeZonesInUi (){
        String sourceId = mSourceTimeZone.getID();
        String sourceDisplayName = mSourceTimeZone.getDisplayName(false, TimeZone.SHORT);
        mDefaultTimeZoneBinding.sourceTimeZoneId.setText(sourceId);
        mDefaultTimeZoneBinding.sourceDisplayName.setText(sourceDisplayName);
    }

    // TODO: can't change default(initially)
    // TODO: refactor to load all selected dest timezones from db
    /**
     * load all the selected time zones from db
     */
    private void setSelectedTimeZoneInUi(String id){
        TimeZone timeZone = TimeZone.getTimeZone(id);
        String displayName = timeZone.getDisplayName(false, TimeZone.SHORT);
/*
        if (hasChangedSourceTimeZone){
            mSourceTimeZone = timeZone;
            mSourceFormat.setTimeZone(mSourceTimeZone);

            //mBinding.sourceTimeZoneId.setText(id);
            //mBinding.sourceDisplayName.setText(displayName);

            hasChangedSourceTimeZone = false;
        } else {
            mDestinationTimeZone = timeZone;
            mDestinationFormat.setTimeZone(mDestinationTimeZone);

            //mBinding.destTimeZoneId.setText(id);
            //mBinding.destDisplayName.setText(displayName);

            hasChangedDestTimeZone = false;
        }
        */
    }

    public void showTimePickerDialog(View v) {
        TimePickerFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "timePicker");
        timePicker.setTimeListener(this);
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
        datePicker.setDateListener(this);
    }

    public void showTimeZonePickerDialog(View v) {
        TimeZonePickerFragment timeZonePicker = new TimeZonePickerFragment();
        timeZonePicker.show(getSupportFragmentManager(), "timeZonePicker");
        timeZonePicker.setTimeZoneListener(this);
    }

    @Override
    public void dateSet(String date) {
        //mBinding.sourceDate.setText(date);
    }

    @Override
    public void timeSet(String time) {
        //mBinding.sourceTime.setText(time);
    }

    // TODO: when user selects timezone, store its id in db, then load all timezones from db to be
    // displayed in UI
    @Override
    public void timeZoneSet(String timeZoneId) {
        //insertTimeZoneInDb(timeZoneId);
    }

    private void insertTimeZoneInDb(String timeZoneId){
        ContentValues values = new ContentValues();
        values.put(TimeZoneContract.CurrentEntry.COLUMN_TIME_ZONE_ID, timeZoneId);

        Uri uri = getContentResolver().insert(TimeZoneContract.CurrentEntry.CONTENT_URI, values);

        if(uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }

        getLoaderManager().restartLoader(DB_LOADER, null, this);
    }

    /**
     * Instantiates and returns a new AsyncTaskLoader with the given ID.
     * This loader will return task data as a Cursor or null if an error occurs.
     *
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(TimeZoneContract.CurrentEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
