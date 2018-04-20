package com.example.android.clockcalc;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class TimeZoneAdapter extends RecyclerView.Adapter<TimeZoneAdapter.TimeZoneViewHolder>{

    private ArrayList<String> originalData;
    private ArrayList<String> displayedData;

    final private TimeZoneAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface TimeZoneAdapterOnClickHandler {
        void onClick(String timeZone);
    }

    public TimeZoneAdapter(ArrayList<String> timeZones, TimeZoneAdapterOnClickHandler handler){
        originalData = timeZones;
        displayedData = new ArrayList<>(timeZones);
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

            String selectedTimeZone = displayedData.get(position);
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
        holder.timeZone.setText(displayedData.get(position));
    }

    @Override
    public int getItemCount() {
        return displayedData.size();
    }

    public void swapData (ArrayList<String> data){
        displayedData.clear();
        this.displayedData = data;
        notifyDataSetChanged();
    }

    public void displaySearchResults(String search) {
        ArrayList<String> matches = new ArrayList<>();

        for (String string : originalData){
            if (string.toLowerCase().contains(search.toLowerCase())){
                matches.add(string);
            }
        }

        swapData(matches);
    }
}
