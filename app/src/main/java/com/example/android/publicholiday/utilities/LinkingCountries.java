package com.example.android.publicholiday.utilities;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.example.android.publicholiday.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public final class LinkingCountries {
    public static String[] countryReader(Context context, String fileName) {


        BufferedReader reader;
        ArrayList<String> listOfCountries=new ArrayList<String>();


        InputStream file = null;
        if(fileName.equals("countries_name")){
        try{
            file =context.getResources().openRawResource(R.raw.countries_name);
            reader = new BufferedReader(new InputStreamReader(file));
            String line = reader.readLine();
            listOfCountries.add(line);
            while(line != null){
                Log.d("StackOverflow", line);
                line = reader.readLine();
                listOfCountries.add(line);
            }

            file.close();

        } catch(IOException ioe){
            ioe.printStackTrace();
        }}
        else {
            try {
                file = context.getResources().openRawResource(R.raw.country_code);
                reader = new BufferedReader(new InputStreamReader(file));
                String line = reader.readLine();
                listOfCountries.add(line);
                while (line != null) {
                    Log.d("StackOverflow", line);
                    line = reader.readLine();
                    listOfCountries.add(line);
                }

                file.close();

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        listOfCountries.removeAll(Collections.singleton(null));
        String[] countries=new String[listOfCountries.size()];
        countries=listOfCountries.toArray(countries);


        return countries;
    }

    public static HashMap<String,String> getCountryCodes(String[] countries, String[] countryCodes){
        HashMap<String, String> keys=new HashMap<String,String>();
        for(int i=0;i<countries.length;i++){
            keys.put(countries[i],countryCodes[i]);
        }
         return keys;

    }

    public static String getCountrycode(HashMap keys,String country){
        String countryCode=keys.get(country).toString();
        return countryCode;

    }
}
