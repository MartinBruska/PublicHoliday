package com.mbdevs.app.publicholiday.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mbdevs.app.publicholiday.R;
import com.mbdevs.app.publicholiday.holidaysModels.HolidayDay;
import com.mbdevs.app.publicholiday.utilities.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
        //getting system year
        Calendar calendar= Calendar.getInstance();
        int cYear=calendar.get(Calendar.YEAR);
        //getting data year
        int year=Integer.parseInt(DateUtils.getYear(DateUtils.stringToDate(mHolidayData.get(position).getmStartDate())));

        String holidayName=mHolidayData.get(position).getmName();




        holder.mHolidayNameTextView.setText(holidayName);
        //formatting date
        String startDateString=mHolidayData.get(position).getmStartDate();
        Date startDateDate=DateUtils.stringToDate(startDateString);
        String startDate= (String) android.text.format.DateFormat.format("MMM d",startDateDate);
        holder.mHolidayStartDate.setText(startDate);
        //getting name of day(Monday, Tuesday, Wednesday..)
        String date=mHolidayData.get(position).getmStartDate();
        Date dateOfHoliday= DateUtils.stringToDate(date);
        String weekDay= (String) android.text.format.DateFormat.format("EEEE",dateOfHoliday);
        holder.mHolidayWeekDay.setText(weekDay);

        if(cYear==year && mHolidayData.get(position).getmStartDate().equals((DateUtils.nextHoliday(mHolidayData).getmStartDate()))){

            holder.itemView.setBackgroundColor(Color.parseColor("#ffd600"));
            }
        else{
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));

        }


    }

    @Override
    public int getItemCount() {
        if(null==mHolidayData) return 0;
        return mHolidayData.size();
    }

    public class HolidaysAdapterViewHolder extends RecyclerView.ViewHolder{

        public final TextView mHolidayNameTextView;
        public final TextView mHolidayStartDate;
        public final TextView mHolidayWeekDay;


        public HolidaysAdapterViewHolder(View itemView) {
            super(itemView);
            mHolidayNameTextView =(TextView) itemView.findViewById(R.id.tv_holiday_name);
            mHolidayStartDate=(TextView) itemView.findViewById(R.id.tv_start_date);
            mHolidayWeekDay =(TextView) itemView.findViewById(R.id.tv_week_day);



        }
    }

    public void setHolidayData(ArrayList<HolidayDay> holidayData){
        mHolidayData=holidayData;
        notifyDataSetChanged();
    }


}
