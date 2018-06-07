package com.example.android.publicholiday;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.publicholiday.utilities.NetworkUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.CountriesAdapterViewHolder> {

    private static final String TAG = CountriesAdapter.class.getSimpleName();

   //private String[] mCountriesList={"andora", "australia"};
    private String [] mCountriesList;


    private final CountriesAdapterOnClickHandler mClickHandler;



    /**
     * The interface that receives onClick messages.
     */
    public interface CountriesAdapterOnClickHandler {
        void onClick(String countryName);
    }

    /**
     * Creates a CountriesAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public CountriesAdapter(CountriesAdapterOnClickHandler clickHandler, String[] countries) {
        mClickHandler = clickHandler;
        mCountriesList=countries;

    }


    /**
     * Cache of the children views for a forecast list item.
     */
    public class CountriesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView mCountriesTextView;



        public CountriesAdapterViewHolder(View itemView) {
            super(itemView);
            this.mCountriesTextView = itemView.findViewById(R.id.tv_country_name);
            itemView.setOnClickListener(this);
        }


        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */@Override
        public void onClick(View v) {
             int adapterPosition=getAdapterPosition();
             String countryName=mCountriesList[adapterPosition];
             mClickHandler.onClick(countryName);

        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new ForecastAdapterViewHolder that holds the View for each list item
     */
    @Override
    public CountriesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context=viewGroup.getContext();
        int layoutIdForListItem=R.layout.countries_list_item;
        LayoutInflater inflater= LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;


        View view=inflater.inflate(layoutIdForListItem,viewGroup, shouldAttachToParentImmediately);
        return new CountriesAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the data country
     * for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */@Override
    public void onBindViewHolder(CountriesAdapterViewHolder holder, int position) {
         String country=mCountriesList[position];
         holder.mCountriesTextView.setText(country);

    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in country list
     */@Override
    public int getItemCount() {
        if(null == mCountriesList) return 0;

        return mCountriesList.length;

    }

    /**
     * This method is used to set the country list on a CountriesAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new ForecastAdapter to display it.
     *
     * @param countryList The new country list to be displayed.
     *
     * ___Looks like useless for my solution */

    public void setCountryData(String[] countryList) {
        mCountriesList = countryList;
        notifyDataSetChanged();
    }


}
