package com.vb.stockkeeper.model;

import java.io.Serializable;

/**
 * Created by vb on 11/15/17.
 */

public class StockSymbol implements Serializable {
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
