package com.example.android.clockcalc;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;


public class TimeZoneAdapter extends RecyclerView.Adapter<TimeZoneAdapter.TimeZoneViewHolder>{

    private ArrayList<String> mTimeZones;

    final private TimeZoneAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface TimeZoneAdapterOnClickHandler {
        void onClick(String timeZone);
    }

    public TimeZoneAdapter(String[] timeZones, TimeZoneAdapterOnClickHandler handler){
        mTimeZones = new ArrayList(Arrays.asList(timeZones));
        mClickHandler = handler;
    }


    class TimeZoneViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView timeZone;

        public TimeZoneViewHolder(View itemView) {
            super(itemView);
            timeZone = itemView.findViewById(R.id.list_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick (View v){
            int position = getAdapterPosition();

            String selectedTimeZone = mTimeZones.get(position);
            mClickHandler.onClick(selectedTimeZone);
        }
    }

    @Override
    public TimeZoneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForListItem = R.layout.list_item_time_zone_picker;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutForListItem, parent, false);
        TimeZoneViewHolder viewHolder = new TimeZoneViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TimeZoneViewHolder holder, int position) {
        holder.timeZone.setText(mTimeZones.get(position));
        holder.timeZone.setTextSize(12);
    }

    @Override
    public int getItemCount() {
        return mTimeZones.size();
    }
}
