package com.example.android.clockcalc;


import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.clockcalc.Data.TimeZoneContract;
import com.example.android.clockcalc.Data.TimeZoneDbHelper;
import com.example.android.clockcalc.Utils.TimeZoneUtils;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * MainActivity
 */
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

    private SimpleDateFormat mSourceFormat;
    private TimeZone mSourceTimeZone;

    private View.OnClickListener sourceTimeZoneClickListener;
    private View.OnClickListener destTimeZoneClickListener;
    private View.OnClickListener timeClickListener;
    private View.OnClickListener dateClickListener;

    private TimeZoneDbHelper dbHelper;
    private TimeZoneCursorAdapter cursorAdapter;

    private TextView sourceDateTv;
    private TextView sourceTimeZoneIdTv;
    private TextView sourceDisplayNameTv;
    private TextClock sourceTime;

    private Cursor cursor;
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

        dbHelper = new TimeZoneDbHelper(this);

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

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
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
        //displayDbInfo();
    }


    private void getDbInfo (){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + TimeZoneContract.CurrentEntry.TABLE_NAME, null);
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
     */
    private void setLocalTimeZoneInfoInUi (){
        String id = mSourceTimeZone.getID();
        String displayName = mSourceTimeZone.getDisplayName(false, TimeZone.SHORT);
        sourceDateTv.setText(TimeZoneUtils.getCurrentDate(mSourceTimeZone));
        sourceTime.setTimeZone(id);

        sourceTimeZoneIdTv.setText(id);
        sourceDisplayNameTv.setText(displayName);
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

    private void insertTimeZoneInDb(String timeZoneId){
        ContentValues values = new ContentValues();
        values.put(TimeZoneContract.CurrentEntry.COLUMN_TIME_ZONE_ID, timeZoneId);

        Uri uri = getContentResolver().insert(TimeZoneContract.CurrentEntry.CONTENT_URI, values);

        if(uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }

        getLoaderManager().restartLoader(DB_LOADER, null, this);
    }
}
