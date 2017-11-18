package com.vb.stockkeeper.activity.details.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vb.stockkeeper.R;
import com.vb.stockkeeper.model.StockSymbol;


public class HistoricalFragment extends Fragment {

    private static final String TAG = HistoricalFragment.class.getCanonicalName();

    public HistoricalFragment() { }

    public static HistoricalFragment newInstance(StockSymbol symbol) {
        Log.d(TAG, "newInstance");
        HistoricalFragment fragment = new HistoricalFragment();
        Bundle args = new Bundle();
        StockSymbol.addToBundle(args, symbol);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView with: " + StockSymbol.stringify(StockSymbol.fromBundle(getArguments())));
        return inflater.inflate(R.layout.fragment_historical, container, false);
    }
}
