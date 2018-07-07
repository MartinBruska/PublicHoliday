package com.example.android.publicholiday.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.publicholiday.R;
import com.example.android.publicholiday.holidaysModels.HolidayDay;

import java.util.ArrayList;

public class HolidaysAdapter extends RecyclerView.Adapter<HolidaysAdapter.HolidaysAdapterViewHolder> {

    private ArrayList<HolidayDay> mHolidayData;

    @NonNull
    @Override
    public HolidaysAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context= viewGroup.getContext();
        int layoutIdForListItem= R.layout.holidays_list_item;
        LayoutInflater inflater=LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view=inflater.inflate(layoutIdForListItem,viewGroup,shouldAttachToParentImmediately);
        return new HolidaysAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolidaysAdapterViewHolder holder, int position) {
        String holidayName=mHolidayData.get(position).getmName();
        String holidayStart=mHolidayData.get(position).getmStartDate();
        String holidayEnd=mHolidayData.get(position).getmEndDate();

        holder.mHolidayNameTextView.setText(holidayName);
        holder.mHolidayStartDate.setText(holidayStart);
        holder.mHolidayEndDate.setText(holidayEnd);


    }

    @Override
    public int getItemCount() {
        if(null==mHolidayData) return 0;
        return mHolidayData.size();
    }

    public class HolidaysAdapterViewHolder extends RecyclerView.ViewHolder{

        public final TextView mHolidayNameTextView;
        public final TextView mHolidayStartDate;
        public final TextView mHolidayEndDate;

        public HolidaysAdapterViewHolder(View itemView) {
            super(itemView);
            mHolidayNameTextView =(TextView) itemView.findViewById(R.id.tv_holiday_name);
            mHolidayStartDate=(TextView) itemView.findViewById(R.id.tv_start_date);
            mHolidayEndDate=(TextView) itemView.findViewById(R.id.tv_end_date);

        }
    }

    public void setHolidayData(ArrayList<HolidayDay> holidayData){
        mHolidayData=holidayData;
        notifyDataSetChanged();
    }
}
