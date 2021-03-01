package com.mbdevs.app.publicholiday.utilities;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {
    //TAG for logging
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String HOLIDAYS_BASE_FIRST_PART_URL=
            "https://www.googleapis.com/calendar/v3/calendars/en.";

    private static final String HOLIDAYS_BASE_LAST_PART_URL=
            "%23holiday%40group.v.calendar.google.com/events?key=key";



    /**
     * Builds the URL used to talk to the google calendar server using a country. This country is based
     * on the query capabilities of the holidays provider that we are using.
     *
     * @param country The location that will be queried for.
     * @return The URL to use to query the holiday server
     *
     */

    public static URL buildUrl(String countryCode) {

   StringBuilder httpBuilt=new StringBuilder();
   httpBuilt.append(HOLIDAYS_BASE_FIRST_PART_URL);
   httpBuilt.append(countryCode);
   httpBuilt.append(HOLIDAYS_BASE_LAST_PART_URL);



        URL url = null;
        try {
            url = new URL(httpBuilt.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }


    }





