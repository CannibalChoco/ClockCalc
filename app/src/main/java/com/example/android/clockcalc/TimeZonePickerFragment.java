package com.example.android.clockcalc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TimeZone;

public class TimeZonePickerFragment extends DialogFragment implements
        TimeZoneAdapter.TimeZoneAdapterOnClickHandler{

    RecyclerView recyclerView;
    SearchView searchView;
    TimeZoneAdapter adapter;
    ArrayList<String> dataSet;

    public static final String IS_SOURCE = "isSource";
    boolean isSource;

    public interface DialogTimeZoneListener{
        void timeZoneSet(String timeZoneId, boolean isSource);
    }

    DialogTimeZoneListener listener;

    public void setTimeZoneListener(DialogTimeZoneListener listener){
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.time_zone_picker_view, container);

        Bundle bundle = getArguments();
        isSource = bundle.getBoolean(IS_SOURCE);

        recyclerView = rootView.findViewById(R.id.recyclerViewTimeZonePicker);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        dataSet = new ArrayList<>(Arrays.asList(TimeZone.getAvailableIDs())) ;

        adapter = new TimeZoneAdapter(dataSet, this);
        recyclerView.setAdapter(adapter);

        this.getDialog();

        /*
          Set up search view
         */
        searchView = rootView.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getSearchResults(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getSearchResults(newText);

                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onClick(String selectedTimeZone) {
        listener.timeZoneSet(selectedTimeZone, isSource);
        dismiss();
    }
}
