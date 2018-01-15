package com.example.android.clockcalc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.TimeZone;

/**
 * Created by Emils on 22.12.2017.
 */

public class TimeZonePickerFragment extends DialogFragment implements TimeZoneAdapter.TimeZoneAdapterOnClickHandler{

    RecyclerView recyclerView;
    TimeZoneAdapter adapter;


    public interface DialogTimeZoneListener{
        void timeZoneSet(String timeZoneId);
    }

    DialogTimeZoneListener listener;

    public void setTimeZoneListener(DialogTimeZoneListener listener){
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.time_zone_picker_view, container);

        recyclerView = rootView.findViewById(R.id.recyclerViewTimeZonePicker);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        adapter = new TimeZoneAdapter(TimeZone.getAvailableIDs(), this);
        recyclerView.setAdapter(adapter);

        this.getDialog();

        return rootView;
    }

    @Override
    public void onClick(String selectedTimeZone) {
        listener.timeZoneSet(selectedTimeZone);
        dismiss();
    }
}
