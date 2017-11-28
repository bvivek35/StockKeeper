package com.vb.stockkeeper.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by vb on 11/25/17.
 */

public class FavSymbol {

    private static final String TAG = FavSymbol.class.getCanonicalName();

    private static final String PRICE_FORMAT = "%.2f";
    private static final String CHANGE_FORMAT = "%.2f (%.2f%%)";

    public static final Comparator<FavSymbol> SYMBOL_COMPARATOR = new Comparator<FavSymbol>() {
        @Override
        public int compare(FavSymbol o1, FavSymbol o2) {
            return o1.getSymbol().compareToIgnoreCase(o2.getSymbol());
        }
    };
    public static final Comparator<FavSymbol> PRICE_COMPARATOR = new Comparator<FavSymbol>() {
        @Override
        public int compare(FavSymbol o1, FavSymbol o2) {
            return ((Double)o1.getPrice()).compareTo((Double)o2.getPrice());
        }
    };
    public static final Comparator<FavSymbol> CHANGE_COMPARATOR = new Comparator<FavSymbol>() {
        @Override
        public int compare(FavSymbol o1, FavSymbol o2) {
            return ((Double)o1.getChangePercent()).compareTo((Double)o2.getChangePercent());
        }
    };



    private String symbol;
    private double price;
    private double change;
    private double changePercent;

    public FavSymbol() {
    }

    public FavSymbol(String symbol, double price, double change, double changePercent) {
        this.symbol = symbol;
        this.price = price;
        this.change = change;
        this.changePercent = changePercent;
    }

    public static FavSymbol[] getMockupArray() {
        return new FavSymbol[] {
                new FavSymbol("AAPL", 90.10, 2.00, 1.00),
                new FavSymbol("SNAP", 25.38, -5, -10),
                new FavSymbol("MSFT", 75, -2.88, -1.28)
        };
    }

    public static FavSymbol parseFromJSONObject(JSONObject obj) throws JSONException {
        FavSymbol ret = new FavSymbol();
        ret.setSymbol(obj.getString("symbol"));
        ret.setPrice(Double.parseDouble(obj.getString("price")));
        ret.setChange(Double.parseDouble(obj.getString("change")));
        ret.setChangePercent(Double.parseDouble(obj.getString("changePercent")));
        return ret;
    }

    public static List<FavSymbol> parseListFromJSON(JSONArray data) throws JSONException {
        List<FavSymbol> ret = new ArrayList<FavSymbol>();
        for (int i = 0; i < data.length(); ++i) {
            JSONObject obj = data.getJSONObject(i);
            ret.add(FavSymbol.parseFromJSONObject(obj));
        }
        return ret;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(double changePercent) {
        this.changePercent = changePercent;
    }

    public String getDisplayPrice() {
        return String.format(PRICE_FORMAT, this.getPrice());
    }

    public String getDisplayChange() {
        return String.format(CHANGE_FORMAT, this.getChange(), this.getChangePercent());
    }
}
