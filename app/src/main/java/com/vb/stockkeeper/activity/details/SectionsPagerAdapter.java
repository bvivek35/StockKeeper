package com.vb.stockkeeper.activity.details;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.vb.stockkeeper.activity.details.current.fragment.CurrentFragment;
import com.vb.stockkeeper.activity.details.historical.fragment.HistoricalFragment;
import com.vb.stockkeeper.activity.details.newsfeed.fragment.NewsFeedFragment;
import com.vb.stockkeeper.model.StockSymbol;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final String TAG = SectionsPagerAdapter.class.getCanonicalName();
    private StockSymbol symbol;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public StockSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(StockSymbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem::" + position);
        Fragment frag = null;
        switch (position) {
            case 0:
                frag = CurrentFragment.newInstance(this.getSymbol());
                break;
            case 1:
                frag = HistoricalFragment.newInstance(this.getSymbol());
                break;
            case 2:
                frag = NewsFeedFragment.newInstance(this.getSymbol());
                break;
            default:
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 3;
    }
}