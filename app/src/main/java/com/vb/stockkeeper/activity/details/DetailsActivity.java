package com.vb.stockkeeper.activity.details;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TabHost;

import com.vb.stockkeeper.R;
import com.vb.stockkeeper.activity.details.current.fragment.CurrentFragment;
import com.vb.stockkeeper.activity.details.historical.fragment.HistoricalFragment;
import com.vb.stockkeeper.activity.details.newsfeed.fragment.NewsFeedFragment;
import com.vb.stockkeeper.model.StockSymbol;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getCanonicalName();

    private SwipelessViewPager mViewPager;
    private DetailsTabsPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        StockSymbol symbol = StockSymbol.fromIntent(getIntent());
        Log.d(TAG, "Recv: " + StockSymbol.stringify(symbol));

        if (symbol == null || "".equals(symbol.getSymbol())) {
            Log.d(TAG, "empty symbol provided !");
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(symbol.getSymbol().toUpperCase());

        pagerAdapter = new DetailsTabsPagerAdapter(getSupportFragmentManager());
        pagerAdapter.setSymbol(symbol);
        mViewPager = (SwipelessViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(5);

        TabLayout tabLayout = (TabLayout)
                findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }
}

class DetailsTabsPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = DetailsTabsPagerAdapter.class.getCanonicalName();

    StockSymbol symbol;

    public DetailsTabsPagerAdapter(FragmentManager fm) {
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

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Current";
                break;
            case 1:
                title = "Historical";
                break;
            case 2:
                title = "News";
                break;
            default:
                break;
        }
        return title;
    }
}

class SwipelessViewPager extends ViewPager {

    public SwipelessViewPager(Context context) {
        super(context);
    }

    public SwipelessViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }
}
