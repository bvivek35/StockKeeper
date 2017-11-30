package com.vb.stockkeeper;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.vb.stockkeeper.activity.details.DetailsActivity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vb on 11/14/17.
 */

public class App extends Application {



    private SharedPreferences pref;
    public static final String SHARED_PREF_NAME = "APP_SETTINGS";
    //private static final String APP_BASE_URL = "http://192.168.0.9:8081";
    private static final String APP_BASE_URL = "http://vb-aws-nodejs.us-east-2.elasticbeanstalk.com";
    private static final String AUTOCOMPLETE_DOC = "/api/listSymbols/";
    public static final String AUTOCOMPLETE_URL = APP_BASE_URL + AUTOCOMPLETE_DOC;
    public static final String FAV_DATA_URL = APP_BASE_URL + "/api/stockInfo/summary";
    public static final String STOCK_TABLE_URL = APP_BASE_URL + "/api/stockInfo/table/";
    public static final String HISTORICAL_DATA_URL = APP_BASE_URL + "/api/stockInfo/historical/";
    public static final String INDICATOR_DATA_URL = APP_BASE_URL + "/api/stockInfo/indicator/";
    public static final String INDICATOR_FILE_URL = "file:///android_asset/indicator_chart.html";
    public static final String HISTORICAL_FILE_URL = "file:///android_asset/historical_chart.html";
    public static final String NEWS_FEED_URL = APP_BASE_URL + "/api/stockInfo/newsFeed/";
    public static final Class DETAILS_ACTIVITY = DetailsActivity.class;
    public static final String JS_ANDROID_DIALOG_INTERFACE = "AndroidDialog";
    public static final String JS_ANDROID_EXPORT_INTERFACE = "AndroidExporter";

    @Override
    public void onCreate() {
        super.onCreate();
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        pref.getStringSet(SHARED_PREF_NAME, new HashSet<String>());
        //pref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public SharedPreferences getPref() {
        return pref;
    }

    public void addSymbolToSharedPref(String key) {
        Set<String> favSymbols = getPref().getStringSet(SHARED_PREF_NAME, new HashSet<String>());
        favSymbols.add(key);
        SharedPreferences.Editor editor = getPref().edit();
        editor.putStringSet(SHARED_PREF_NAME, favSymbols);
        editor.commit();
    }
    public void removeSymbolFromSharedPref(String key) {
        Set<String> favSymbols = getPref().getStringSet(SHARED_PREF_NAME, new HashSet<String>());
        favSymbols.remove(key);
        SharedPreferences.Editor editor = getPref().edit();
        editor.putStringSet(SHARED_PREF_NAME, favSymbols);
        editor.commit();
    }
    public boolean isSymbolInSharedPref(String key) {
        Set<String> favSymbols = getPref().getStringSet(SHARED_PREF_NAME, new HashSet<String>());
        return favSymbols.contains(key);
    }
    public Set<String> getAllSymbolsInSharedPref() {
        return getPref().getStringSet(SHARED_PREF_NAME, Collections.EMPTY_SET);
    }


}
