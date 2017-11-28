package com.vb.stockkeeper.activity.details.newsfeed.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.vb.stockkeeper.App;
import com.vb.stockkeeper.R;
import com.vb.stockkeeper.activity.DialogUtils;
import com.vb.stockkeeper.activity.details.newsfeed.NewsFeedAdapter;
import com.vb.stockkeeper.model.NewsFeedItem;
import com.vb.stockkeeper.model.StockSymbol;
import com.vb.stockkeeper.net.CommonJsonErrorHandler;
import com.vb.stockkeeper.net.VolleyFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedFragment extends Fragment {

    private static final String TAG = NewsFeedFragment.class.getCanonicalName();
    private NewsFeedAdapter newsFeedAdapter;
    private ProgressDialog loadDialog;

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
        StockSymbol symbol = StockSymbol.fromBundle(getArguments());
        Log.d(TAG, "Recv in bundle: " + StockSymbol.stringify(symbol));
        this.newsFeedAdapter = new NewsFeedAdapter(this.getContext(), new ArrayList<NewsFeedItem>());//Arrays.asList(NewsFeedItem.getMockArray()));
        // Get Data from service
        Log.d(TAG, "Connecting to server for news feed");
        this.loadDialog = DialogUtils.dismissProgressDialog(this.loadDialog);
        this.loadDialog = DialogUtils.showProgressDialog(getActivity(), "");
        VolleyFactory.getInstance(this.getContext()).addToRequestQueue(prepareRequest(App.NEWS_FEED_URL+symbol.getSymbol(), this.newsFeedAdapter));
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

    private JsonObjectRequest prepareRequest(String wsUrl, NewsFeedAdapter outputAdapter) {
        Log.d(TAG, "Preparing request for news feed: " + wsUrl);
        return new JsonObjectRequest(wsUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(getClass().getName(), "Recv response from server");
                List<NewsFeedItem> responseItems = new ArrayList<>();
                try {
                    JSONArray arr = response.getJSONArray("data");
                    for (int i = 0; i < arr.length(); ++i) {
                        NewsFeedItem tmp = NewsFeedItem.fromJsonObject(arr.getJSONObject(i));
                        Log.d(TAG, "Parsed from object: " + NewsFeedItem.stringify(tmp));
                        responseItems.add(tmp);
                    }
                } catch (JSONException e) {
                    Log.e(getClass().getName(), "Error while parsing response: ", e);
                }
                finally {
                    loadDialog = DialogUtils.dismissProgressDialog(loadDialog);
                }
                if (responseItems.size() > 0) {
                    newsFeedAdapter.getItems().clear();
                    newsFeedAdapter.getItems().addAll(responseItems);
                    newsFeedAdapter.notifyDataSetChanged();
                }
            }
        }, new CommonJsonErrorHandler() {
            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                loadDialog = DialogUtils.dismissProgressDialog(loadDialog);
            }
        });
    }
}