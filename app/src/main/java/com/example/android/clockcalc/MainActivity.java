package com.example.android.clockcalc;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0f);

        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.viewpager);
        SimpleFragmentPagerAdapter pagerAdapter =
                new SimpleFragmentPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);

    }
}
