package com.example.android.clockcalc;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by Emils on 22.12.2017.
 */

public class TimeZoneAdapter extends RecyclerView.Adapter<TimeZoneAdapter.TimeZoneViewHolder>{

    private String[] mTimeZones;


    public TimeZoneAdapter(String[] timeZones){
        mTimeZones = timeZones;
    }

    class TimeZoneViewHolder extends RecyclerView.ViewHolder{

        TextView timeZone;

        public TimeZoneViewHolder(View itemView) {
            super(itemView);
            timeZone = itemView.findViewById(R.id.list_item);
        }
    }

    @Override
    public TimeZoneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutForListItem, parent, false);
        TimeZoneViewHolder viewHolder = new TimeZoneViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TimeZoneViewHolder holder, int position) {
        holder.timeZone.setText(mTimeZones[position]);
    }

    @Override
    public int getItemCount() {
        return mTimeZones.length;
    }
}
