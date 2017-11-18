package com.vb.stockkeeper.activity.details.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vb.stockkeeper.App;
import com.vb.stockkeeper.R;
import com.vb.stockkeeper.model.StockSymbol;
import com.vb.stockkeeper.net.VolleyFactory;

public class CurrentFragment extends Fragment {

    private static final String TAG = CurrentFragment.class.getCanonicalName();

    public CurrentFragment() {}

    public static CurrentFragment newInstance(StockSymbol symbol) {
        Log.d(TAG, "newInstance");
        CurrentFragment fragment = new CurrentFragment();
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
        //getArguments().putInt("count", getArguments().getInt("count")+1);
        return inflater.inflate(R.layout.fragment_current, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState()");
    }
}