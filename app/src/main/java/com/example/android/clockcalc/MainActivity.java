package com.example.android.clockcalc;

import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.clockcalc.Data.ClockCalcPreferences;
import com.example.android.clockcalc.Data.TimeZoneContract;

public class MainActivity extends AppCompatActivity{

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    public static int prefTimeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().setElevation(0f);

        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.viewpager);
        SimpleFragmentPagerAdapter pagerAdapter =
                new SimpleFragmentPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        settings = getSharedPreferences(ClockCalcPreferences.PREFS_CLOCK_CALC, 0);
        prefTimeFormat = settings.getInt(ClockCalcPreferences.PREFS_TIME_FORMAT,
                ClockCalcPreferences.PREFS_TIME_FORMAT_24_H);

        Log.i("TEST PREFS", String.valueOf(prefTimeFormat));
    }

    @Override
    protected void onStop() {
        if (editor != null) editor.commit();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_hour_format){
            editor = settings.edit();
            editor.putInt(ClockCalcPreferences.PREFS_TIME_FORMAT,
                    ClockCalcPreferences.toggleTimeFormat(prefTimeFormat));
            editor.apply();

            prefTimeFormat = settings.getInt(ClockCalcPreferences.PREFS_TIME_FORMAT, 2);

            Log.i("TEST PREFS", String.valueOf(prefTimeFormat));

            getContentResolver().notifyChange(TimeZoneContract.TimeZonesEntry.CONTENT_URI, null);
        }

        super.onOptionsItemSelected(item);

        return true;
    }

}
