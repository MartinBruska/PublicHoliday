package com.mbdevs.app.publicholiday;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mbdevs.app.publicholiday.adapters.HolidaysAdapter;
import com.mbdevs.app.publicholiday.adapters.MonthAdapter;
import com.mbdevs.app.publicholiday.holidaysModels.HolidayDay;
import com.mbdevs.app.publicholiday.holidaysModels.Months;
import com.mbdevs.app.publicholiday.utilities.DateUtils;
import com.mbdevs.app.publicholiday.utilities.LinkingCountries;
import com.mbdevs.app.publicholiday.utilities.NetworkUtils;
import com.mbdevs.app.publicholiday.utilities.OpenHolidaysJSONUtils;
import com.mbdevs.app.publicholiday.R;


import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HolidayActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<HolidayDay>> {

    private static final String TAG = HolidayActivity.class.getSimpleName();

    private String mCountry;
    private TextView mErrorMessageDisplay;
    private ImageView mflag;
    private TextView mCountryName;
    private Boolean splitToMonths=false;
    private HolidaysAdapter mYearAdapter;
    private MonthAdapter mMonthAdapter;
    private RecyclerView mRecyclerView;
    private Map<String,ArrayList<HolidayDay>> dataSortedByYear;
    private List<Months> dataSortedByMonth;
    private boolean existButtons=false;


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
                //setting pic for selected country detail
                mflag=(ImageView) findViewById(R.id.iv_country_flag_detail);
                String imageNameNew=mCountry.replaceAll("[^\\w]", "")
                        .replaceAll("[é,Ã§]","")
                        .toLowerCase();

                String imageName=imageNameNew;
                int picID=this.getResources().getIdentifier(imageName,"drawable","com.example.android.publicholiday");

                mflag.setImageResource(picID);
                //setting country name
                mCountryName=(TextView) findViewById(R.id.tv_country_name_detail);
                mCountryName.setText(mCountry);
                //set activity title
                //this.setTitle(mCountry);


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_holiday, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.split_months) {
            if(!splitToMonths){
            splitToMonths=true;
            mRecyclerView.setAdapter(mMonthAdapter);
            LinearLayout buttonLayout=(LinearLayout)findViewById(R.id.ll_button_row);
            buttonLayout.setVisibility(View.GONE);
            item.setTitle(getString(R.string.split_back_months));}
        else{
            splitToMonths=false;
            mRecyclerView.setAdapter(mYearAdapter);
                LinearLayout buttonLayout=(LinearLayout)findViewById(R.id.ll_button_row);
                buttonLayout.setVisibility(View.VISIBLE);
            item.setTitle(getString(R.string.split_to_months));
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
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
        mMonthAdapter =new MonthAdapter(dataSortedByMonth);



        mYearAdapter= new HolidaysAdapter();
        ArrayList<HolidayDay> adapterYearData=dataSortedByYear.get(String.valueOf(cYear));
        //sorting by date
        Collections.sort(adapterYearData, new Comparator<HolidayDay>() {
            @Override
            public int compare(HolidayDay o1, HolidayDay o2) {
                if(o1.getmStartDate()==null || o2.getmStartDate()==null)
                    return 0;
                return o1.getmStartDate().compareTo(o2.getmStartDate());
            }
        });
        mYearAdapter.setHolidayData(adapterYearData);

        mRecyclerView.setAdapter(mYearAdapter);




        //find the linear layout for buttons
        LinearLayout buttonLayout=(LinearLayout) findViewById(R.id.ll_button_row);
        RadioGroup buttonGroup=(RadioGroup) findViewById(R.id.rg_radio_buttons);
        buttonGroup.setOrientation(LinearLayout.HORIZONTAL);
        if(!existButtons) {
            //creating new buttons based on keyset and setting onclicklistener on them
            if (dataSortedByYear.keySet() != null) {
                for (final String year : dataSortedByYear.keySet()) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            dpToInt(120), LinearLayout.LayoutParams.MATCH_PARENT, 0.335f
                    );
                    params.setMargins(10, 0, 10, 20);
                    RadioButton button = new RadioButton(this);
                    button.setText(year);
                    button.setTag(year);
                    button.setLayoutParams(params);
                    button.setButtonDrawable(null);
                    button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    button.setBackground(getResources().getDrawable(R.drawable.buttons_selector));
                    //button.setPadding(50,20,50,20);
                    //button.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    button.setTextAppearance(this, android.R.style.TextAppearance_Large);
                    buttonGroup.addView(button);
                    existButtons=true;
                    //change color of button which was chosen automatically base on the system year
                    if (button.getText().equals(String.valueOf(cYear))) {
                        button.setChecked(true);
                    }
                }
            }
        }
        buttonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton=(RadioButton) group.findViewById(checkedId);
                    String year=radioButton.getText().toString();
                dataSortedByMonth= DateUtils.holidaysSortingByMonth(dataSortedByYear.get(year));

                mMonthAdapter =new MonthAdapter(dataSortedByMonth);

                ArrayList<HolidayDay> adapterYearData=dataSortedByYear.get(String.valueOf(year));
                //sorting by date
                Collections.sort(adapterYearData, new Comparator<HolidayDay>() {
                    @Override
                    public int compare(HolidayDay o1, HolidayDay o2) {
                        if(o1.getmStartDate()==null || o2.getmStartDate()==null)
                            return 0;
                        return o1.getmStartDate().compareTo(o2.getmStartDate());
                    }
                });
                mYearAdapter.setHolidayData(adapterYearData);

             }
        });


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

    /**
     * dp to px conversion
     *
     * @param dpDimension dp dimensoin which will be changed to px
     * @return dpDimension in px
     */
    private int dpToInt(float dpDimension){
        DisplayMetrics metrics =getResources().getDisplayMetrics();
        float fpixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpDimension, metrics);
        return Math.round(fpixels);
        }




}
