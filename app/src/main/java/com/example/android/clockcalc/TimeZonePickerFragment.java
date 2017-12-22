package com.example.android.clockcalc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.TimeZone;

/**
 * Created by Emils on 22.12.2017.
 */

public class TimeZonePickerFragment extends DialogFragment {

    RecyclerView recyclerView;
    TimeZoneAdapter adapter;
    //TimeZoneAdapter.OnClickListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.time_zone_picker_view, container);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        adapter = new TimeZoneAdapter(TimeZone.getAvailableIDs());
        recyclerView.setAdapter(adapter);

        this.getDialog();

        return rootView;
    }
}
