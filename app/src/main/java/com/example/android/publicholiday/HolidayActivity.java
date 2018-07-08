package com.example.android.publicholiday;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.android.publicholiday.adapters.HolidaysAdapter;
import com.example.android.publicholiday.adapters.MonthAdapter;
import com.example.android.publicholiday.holidaysModels.HolidayDay;
import com.example.android.publicholiday.holidaysModels.Months;
import com.example.android.publicholiday.utilities.DateUtils;
import com.example.android.publicholiday.utilities.LinkingCountries;
import com.example.android.publicholiday.utilities.NetworkUtils;
import com.example.android.publicholiday.utilities.OpenHolidaysJSONUtils;


import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HolidayActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<HolidayDay>> {

    private static final String TAG = HolidayActivity.class.getSimpleName();

    private String mCountry;
    private TextView mErrorMessageDisplay;



    private MonthAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Map<String,ArrayList<HolidayDay>> dataSortedByYear;
    private List<Months> dataSortedByMonth;


    private ProgressBar mLoadingIndicator;

    private static final int HOLIDAY_LOADER_ID = 0;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_holidays_detail);







        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */
        mLoadingIndicator=(ProgressBar) findViewById(R.id.pb_loading_indicator);

        Intent holidayDetialsIntent=getIntent();

        if(holidayDetialsIntent != null){
            if (holidayDetialsIntent.hasExtra(Intent.EXTRA_TEXT)) {
                mCountry=holidayDetialsIntent.getStringExtra(Intent.EXTRA_TEXT);
                //set activity title
                this.setTitle(mCountry);

                            }
        }
        mRecyclerView=(RecyclerView) findViewById(R.id.recyclerview_holidays);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);





        /*
         * This ID will uniquely identify the Loader. We can use it, for example, to get a handle
         * on our Loader at a later point in time through the support LoaderManager.
         */
        int loaderId=HOLIDAY_LOADER_ID;
        /*
         * From HolidayActivity, we have implemented the LoaderCallbacks interface with the type of
         * String array. (implements LoaderCallbacks<String[]>) The variable callback is passed
         * to the call to initLoader below. This means that whenever the loaderManager has
         * something to notify us of, it will do so through this callback.
         */
        LoaderManager.LoaderCallbacks<ArrayList<HolidayDay>> callback=HolidayActivity.this;

        /*
         * The second parameter of the initLoader method below is a Bundle. Optionally, you can
         * pass a Bundle to initLoader that you can then access from within the onCreateLoader
         * callback. In our case, we don't actually use the Bundle, but it's here in case we wanted
         * to.
         */
        Bundle bundleForLoader = null;
        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(loaderId,bundleForLoader,callback);


    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param loaderArgs Any arguments supplied by the caller.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<ArrayList<HolidayDay>> onCreateLoader(int id, Bundle loaderArgs) {
        return new AsyncTaskLoader<ArrayList<HolidayDay>>(this) {

            /* This String array will hold and help cache our holidays data */
            ArrayList<HolidayDay> mHolidaysData = null;
            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                if(mHolidaysData!=null){
                    deliverResult(mHolidaysData);}
                else{
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                    }
                }

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from GoogleCalendar in the background.
             *
             * @return Holidays data from Google calendar as an arraylist of HolidayDay objects.
             *         null if an error occurs
             */
            @Override
            public ArrayList<HolidayDay> loadInBackground() {

                URL holidayRequestUrl = NetworkUtils.buildUrl(getCountryCode(mCountry));

                try {
                    String jsonHolidayResponse = NetworkUtils
                            .getResponseFromHttpUrl(holidayRequestUrl);



                    ArrayList<HolidayDay> simpleJsonHolidayData = OpenHolidaysJSONUtils.getHolidaysFromJson(HolidayActivity.this,jsonHolidayResponse);


                    return simpleJsonHolidayData;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(ArrayList<HolidayDay> data) {
                mHolidaysData = data;
                super.deliverResult(data);
            }
        };
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @SuppressLint("ResourceType")
    @Override
    public void onLoadFinished(Loader<ArrayList<HolidayDay>> loader, ArrayList<HolidayDay> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        //getting system year
        Calendar calendar= Calendar.getInstance();
        int cYear=calendar.get(Calendar.YEAR);
        //sort the data by year
        dataSortedByYear = DateUtils.holidaysSortingByYear(data);
        //sort the data by month
        dataSortedByMonth= DateUtils.holidaysSortingByMonth(dataSortedByYear.get(String.valueOf(cYear)));
        mAdapter=new MonthAdapter(dataSortedByMonth);

        mRecyclerView.setAdapter(mAdapter);



        //find the linear layout for buttons
        LinearLayout buttonLayout=(LinearLayout) findViewById(R.id.ll_button_row);
        RadioGroup buttonGroup=(RadioGroup) findViewById(R.id.rg_radio_buttons);
        buttonGroup.setOrientation(LinearLayout.HORIZONTAL);

        //creating new buttons based on keyset and seting onclicklistener on them
        if(dataSortedByYear.keySet()!=null) {
            for (final String year : dataSortedByYear.keySet()) {
                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
                        dpToInt(120), LinearLayout.LayoutParams.MATCH_PARENT,0.335f
                );
                params.setMargins(10,0,10,20);
                RadioButton button = new RadioButton(this);
                button.setText(year);
                button.setTag(year);
                button.setLayoutParams(params);
                button.setButtonDrawable(null);
                button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                button.setBackground(getResources().getDrawable(R.drawable.buttons_selector));
                //button.setPadding(50,20,50,20);
                //button.setGravity(View.TEXT_ALIGNMENT_CENTER);
                button.setTextAppearance(this,android.R.style.TextAppearance_Large);
                buttonGroup.addView(button);
                //change color of button which was chosen automatically base on the system year
                if(button.getText().equals(String.valueOf(cYear))){
                   button.setChecked(true);
                }
            }
        }

        buttonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton=(RadioButton) group.findViewById(checkedId);
                    String year=radioButton.getText().toString();
                dataSortedByMonth= DateUtils.holidaysSortingByMonth(dataSortedByYear.get(year));
                mAdapter=new MonthAdapter(dataSortedByMonth);
                mRecyclerView.setAdapter(mAdapter);
            }
        });




        //TODO options do not split to months
        //TODO set closest holiday
        //TODO thinks about showing day of week in particular year

        if (null == data) {
            showErrorMessage();
        } else {
            showHolidayDataView();
        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<HolidayDay>> loader) {

    }
    /**
     * This method will make the error message visible and hide the holidays
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the View for the holidays data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showHolidayDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);

    }


    private String getCountryCode(String country){
        String[] countries=LinkingCountries.countryReader(this,"countries_name");
        String[] codes=LinkingCountries.countryReader(this,"country_code");
        HashMap<String,String> keys=LinkingCountries.getCountryCodes(countries,codes);
        String code=LinkingCountries.getCountrycode(keys,country);
        Log.v(TAG,"code"+code);
        return code;
    }

    private int dpToInt(float dpDimension){
        DisplayMetrics metrics =getResources().getDisplayMetrics();
        float fpixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpDimension, metrics);
        return Math.round(fpixels);
    }



}
