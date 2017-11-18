package com.vb.stockkeeper.activity.details;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import com.vb.stockkeeper.App;
import com.vb.stockkeeper.activity.details.fragment.HistoricalFragment;
import com.vb.stockkeeper.R;
import com.vb.stockkeeper.activity.details.fragment.CurrentFragment;
import com.vb.stockkeeper.activity.details.fragment.NewsFeedFragment;
import com.vb.stockkeeper.model.StockSymbol;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getCanonicalName();

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

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

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.setSymbol(symbol);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }
}
