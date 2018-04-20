package com.example.android.clockcalc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.TimeZone;

public class TimeZonePickerFragment extends DialogFragment implements
        TimeZoneAdapter.TimeZoneAdapterOnClickHandler{

    RecyclerView recyclerView;
    SearchView searchView;
    TimeZoneAdapter adapter;

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

        adapter = new TimeZoneAdapter(TimeZone.getAvailableIDs(), this);
        recyclerView.setAdapter(adapter);

        this.getDialog();

        /*
          Set up search view
         */
        searchView = rootView.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
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
