package com.vb.stockkeeper.model;

import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;

/**
 * Created by vb on 11/15/17.
 */

public class StockSymbol implements Serializable {

    private static final String EXTRA_KEY = "stock_symbol";

    private static final String TAG = StockSymbol.class.getCanonicalName();

    private String symbol;
    private String option;

    public StockSymbol() {
        this.symbol = "";
        this.option = "";
    }

    public StockSymbol(String symbol, String option) {
        this.symbol = symbol;
        this.option = option;
    }

    public StockSymbol(String symbol) {
        this(symbol, "");
    }

    @Override
    public String toString() {
        return this.option;
    }

    public static StockSymbol fromBundle(Bundle bundle) {
        if (bundle == null)
            return null;
        return (StockSymbol) bundle.getSerializable(EXTRA_KEY);
    }
    public static StockSymbol fromIntent(Intent intent) {
        if (intent == null)
            return null;
        return (StockSymbol) intent.getSerializableExtra(EXTRA_KEY);
    }

    public static void addToBundle(Bundle bundle, StockSymbol symbol) {
        if (bundle != null) {
            bundle.putSerializable(EXTRA_KEY, symbol);
        }
    }

    public static void addToIntent(Intent intent, StockSymbol symbol) {
        if (intent != null) {
            intent.putExtra(EXTRA_KEY, symbol);
        }
    }

    public static String stringify(StockSymbol ss) {
        return ss == null ? "null" :
                new StringBuilder()
                .append("{")
                .append("symbol:").append(ss.symbol)
                .append((", "))
                .append("option:").append(ss.option)
                .append("}")
                .toString();
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

}
