package com.vb.stockkeeper.activity.details.historical.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.vb.stockkeeper.App;
import com.vb.stockkeeper.R;
import com.vb.stockkeeper.activity.DialogUtils;
import com.vb.stockkeeper.model.StockSymbol;


public class HistoricalFragment extends Fragment {

    private static final String TAG = HistoricalFragment.class.getCanonicalName();

    private WebView webView;

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
        final StockSymbol symbol = StockSymbol.fromBundle(getArguments());
        Log.d(TAG, "onCreateView with: " + StockSymbol.stringify(StockSymbol.fromBundle(getArguments())));
        View view = inflater.inflate(R.layout.fragment_historical, container, false);
        this.webView = view.findViewById(R.id.historical_web_view);
        this.webView.loadUrl(App.HISTORICAL_FILE_URL);
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Setup webView with default chart
                String newUrl = getJSURLFromSymbol(symbol.getSymbol());
                Log.d(getClass().getName(), "Loading url in JS context: " + newUrl);
                view.loadUrl(newUrl);
            }
        });
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.setWebContentsDebuggingEnabled(true);
        this.webView.addJavascriptInterface(DialogUtils.JSInterface(getActivity()), App.JS_ANDROID_DIALOG_INTERFACE);

        return view;
    }

    private static String getJSURLFromSymbol(String symbol) {
        String url = App.HISTORICAL_DATA_URL + symbol;
        String res = "javascript:renderChart('" + url + "', '" + symbol + "')";
        return res;
    }

}
