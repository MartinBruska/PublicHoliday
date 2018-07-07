package com.example.android.publicholiday;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;


import com.example.android.publicholiday.adapters.CountriesAdapter;
import com.example.android.publicholiday.utilities.LinkingCountries;

public class MainActivity extends AppCompatActivity implements CountriesAdapter.CountriesAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecycleView;
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private FloatingActionButton fab;
    private boolean mIsHiding=false;

    private CountriesAdapter mCountriesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        
        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecycleView=(RecyclerView) findViewById(R.id.recyclerview_countries);

        /*
         * LinearLayoutManager can support HORIZONTAL or VERTICAL orientations. The reverse layout
         * parameter is useful mostly for HORIZONTAL layouts that should reverse for right to left
         * languages.
         */

        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        mRecycleView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecycleView.setHasFixedSize(true);

        /*
         * The CountriesAdapter is responsible for linking our country data with the Views that
         * will end up displaying our country list.
         */
        mCountriesAdapter = new CountriesAdapter(this,this, LinkingCountries.countryReader(this,"countries_name"));

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecycleView.setAdapter(mCountriesAdapter);



        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                searchMenuItem.expandActionView();



            }
        });


        // The important part is attaching an OnScrollListener
        // to our RecyclerView. This object will listen
        // if RecyclerView is dragged (scroll or fling action) or idle
        mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                // While RecyclerView enters dragging state
                // (scroll, fling) we want our FAB to disappear.
                // Similarly when it enters idle state we want
                // our FAB to appear back.
                if (newState == recyclerView.SCROLL_STATE_DRAGGING) {
                    // Hiding FAB

                    fab.hide();


                } else if (newState == recyclerView.SCROLL_STATE_IDLE) {
                    // Showing FAB

                   fab.show();

                }
            }


        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();




        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mCountriesAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mCountriesAdapter.getFilter().filter(query);
                return false;
            }
        });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }



    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     *
     * @param countryName The country that was clicked
     */
    @Override
    public void onClick(String countryName) {

        Context context=this;
        Intent holidayDetailsIntent=new Intent(MainActivity.this,HolidayActivity.class);
        holidayDetailsIntent.putExtra(Intent.EXTRA_TEXT,countryName);
        startActivity(holidayDetailsIntent);

    }




}
