package com.example.android.publicholiday;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.publicholiday.utilities.LinkingCountries;
import com.example.android.publicholiday.utilities.NetworkUtils;
import com.example.android.publicholiday.utilities.OpenHolidaysJSONUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class HolidayActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String[]> {

    private static final String TAG = HolidayActivity.class.getSimpleName();

    private String mCountry;
    private TextView mErrorMessageDisplay;
    private HolidaysAdapter mHolidaysAdapter;
    private RecyclerView mRecyclerView;

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
        mHolidaysAdapter=new HolidaysAdapter();
        mRecyclerView.setAdapter(mHolidaysAdapter);


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
        LoaderManager.LoaderCallbacks<String[]> callback=HolidayActivity.this;

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
    public Loader<String[]> onCreateLoader(int id, Bundle loaderArgs) {
        return new AsyncTaskLoader<String[]>(this) {

            /* This String array will hold and help cache our holidays data */
            String[] mHolidaysData = null;
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
             * @return Holidays data from Google calendar as an array of Strings.
             *         null if an error occurs
             */
            @Override
            public String[] loadInBackground() {

                URL holidayRequestUrl = NetworkUtils.buildUrl(getCountryCode(mCountry));

                try {
                    String jsonHolidayResponse = NetworkUtils
                            .getResponseFromHttpUrl(holidayRequestUrl);



                    String[] simpleJsonHolidayData = OpenHolidaysJSONUtils.getHolidaysFromJson(HolidayActivity.this,jsonHolidayResponse);


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
            public void deliverResult(String[] data) {
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
    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mHolidaysAdapter.setHolidayData(data);


        if (null == data) {
            showErrorMessage();
        } else {
            showHolidayDataView();
        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader<String[]> loader) {

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



}
