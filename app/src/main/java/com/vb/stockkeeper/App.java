package com.vb.stockkeeper;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by vb on 11/14/17.
 */

public class App extends Application {


    private SharedPreferences pref;
    private static final String SHARED_PREF_NAME = "APP_SETTINGS";
    private static final String APP_BASE_URL = "http://192.168.0.9:3000";
    private static final String AUTOCOMPLETE_DOC = "/api/listSymbols/";
    public static final String AUTOCOMPLETE_URL = APP_BASE_URL + AUTOCOMPLETE_DOC;
    public static final String STOCK_TABLE_URL = APP_BASE_URL + "/api/stockInfo/table/";
    public static final String INDICATOR_FILE_URL = "file:///android_asset/indicator_chart.html";
    public static final String HISTORICAL_FILE_URL = "file:///android_asset/historical_chart.html";
    public static final String NEWS_FEED_URL = APP_BASE_URL + "/api/stockInfo/newsFeed/";
    public static final String STOCK_SYMBOL_INTENT_KEY = "stock_symbol";

    @Override
    public void onCreate() {
        super.onCreate();
        pref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public SharedPreferences getPref() {
        return pref;
    }
}
