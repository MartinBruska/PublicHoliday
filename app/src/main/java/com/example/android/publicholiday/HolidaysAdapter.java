package com.example.android.publicholiday;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HolidaysAdapter extends RecyclerView.Adapter<HolidaysAdapter.HolidaysAdapterViewHolder> {

    private String[] mHolidayData;

    @NonNull
    @Override
    public HolidaysAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context= viewGroup.getContext();
        int layoutIdForListItem=R.layout.holidays_list_item;
        LayoutInflater inflater=LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view=inflater.inflate(layoutIdForListItem,viewGroup,shouldAttachToParentImmediately);
        return new HolidaysAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolidaysAdapterViewHolder holder, int position) {
        String holidayDetails=mHolidayData[position];
        holder.mHolidayTextView.setText(holidayDetails);


    }

    @Override
    public int getItemCount() {
        if(null==mHolidayData) return 0;
        return mHolidayData.length;
    }

    public class HolidaysAdapterViewHolder extends RecyclerView.ViewHolder{

        public final TextView mHolidayTextView;

        public HolidaysAdapterViewHolder(View itemView) {
            super(itemView);
            mHolidayTextView=(TextView) itemView.findViewById(R.id.tv_holiday_details);
        }
    }

    public void setHolidayData(String[] holidayData){
        mHolidayData=holidayData;
        notifyDataSetChanged();
    }
}
