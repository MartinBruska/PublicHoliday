package com.mbdevs.app.publicholiday.utilities;

import android.content.Context;
import android.util.Log;

import com.mbdevs.app.publicholiday.holidaysModels.HolidayDay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class OpenHolidaysJSONUtils {
/**
 * This method parses JSON from a web response and returns an array of Strings
 * listing the holidays

 * @param holidaysJsonStr JSON response from server
 *
 * @return Array of Strings describing holidays
 *
 * @throws JSONException If JSON data cannot be properly parsed
 * */

//TAG for logging
private static final String TAG = OpenHolidaysJSONUtils.class.getSimpleName();

public static ArrayList<HolidayDay> getHolidaysFromJson(Context context, String holidaysJsonStr)
        throws JSONException {

    /* List of holidayDay objects to hold each holidays' day object */
    ArrayList<HolidayDay> parsedHolidaysData;
    /* Holiday information. Each holidays info is an element of the "items" array */
    final String HOL_LIST="items";
    /*Holiday name. Each holidays name is stored in the"summary" string*/
    final String HOL_NAME="summary";
    /*Dates of holiday are children of "start" and "end" objects*/
    final String HOL_START_DATE="start";
    final String HOL_END_DATE="end";
    /*String variable for a date */
    final String HOL_DATE="date";

    /*get a whole JSON object*/
    JSONObject holidaysJson= new JSONObject(holidaysJsonStr);

    /*get a array of holidays*/
    JSONArray holidayArray=holidaysJson.getJSONArray(HOL_LIST);
    /*create a array to stored a holiday data*/
        parsedHolidaysData=new ArrayList<HolidayDay>();
    /*iterate through array of holiday objects*/
        for(int i=0;i<holidayArray.length();i++){

            String holidayName;
            String startDate;
            String endDate;
            /*get a object for a particular holiday day*/
            JSONObject dayHoliday= holidayArray.getJSONObject(i);
            holidayName=dayHoliday.getString(HOL_NAME);
            Log.v(TAG,"holidayName"+holidayName);

            /*get the start date of holiday day*/
            JSONObject startDateObject=dayHoliday.getJSONObject(HOL_START_DATE);
            startDate=startDateObject.getString(HOL_DATE);
            Log.v(TAG,"startDate"+startDate);
            /*get the end date of holiday day*/
            JSONObject endDateObject=dayHoliday.getJSONObject(HOL_END_DATE);
            endDate=endDateObject.getString(HOL_DATE);
            Log.v(TAG,"endDate"+endDate);

            HolidayDay holidays=new HolidayDay(holidayName,startDate,endDate);

            parsedHolidaysData.add(holidays);


        }
            return parsedHolidaysData;
        }

}
