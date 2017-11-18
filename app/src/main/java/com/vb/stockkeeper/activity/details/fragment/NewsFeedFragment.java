package com.vb.stockkeeper.activity.details.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vb.stockkeeper.App;
import com.vb.stockkeeper.R;
import com.vb.stockkeeper.activity.details.newsfeed.NewsFeedAdapter;
import com.vb.stockkeeper.model.NewsfeedItem;
import com.vb.stockkeeper.model.StockSymbol;

import java.util.ArrayList;
import java.util.Arrays;

public class NewsFeedFragment extends Fragment {

    private static final String TAG = NewsFeedFragment.class.getCanonicalName();
    private NewsFeedAdapter newsFeedAdapter;

    public NewsFeedFragment() {}

    public static NewsFeedFragment newInstance(StockSymbol symbol) {
        Log.d(TAG, "newInstance");
        NewsFeedFragment fragment = new NewsFeedFragment();
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
        this.newsFeedAdapter = new NewsFeedAdapter(this.getContext(), Arrays.asList(NewsfeedItem.getMockArray()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView with: " + StockSymbol.stringify(StockSymbol.fromBundle(getArguments())));
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        ListView newsList = (ListView) view.findViewById(R.id.news_feed_list);
        newsList.setAdapter(this.newsFeedAdapter);
        return view;
    }
}
