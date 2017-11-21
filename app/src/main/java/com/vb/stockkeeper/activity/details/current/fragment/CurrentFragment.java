package com.vb.stockkeeper.activity.details.current.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.vb.stockkeeper.App;
import com.vb.stockkeeper.R;
import com.vb.stockkeeper.model.NewsFeedItem;
import com.vb.stockkeeper.model.StockSymbol;
import com.vb.stockkeeper.net.CommonJsonErrorHandler;
import com.vb.stockkeeper.net.VolleyFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CurrentFragment extends Fragment {

    private static final String TAG = CurrentFragment.class.getCanonicalName();
    private static final String[] JS_FUNCS = {
            "renderPriceVolumeChart", // 0
            "renderSMAChart", // 1
            "renderEMAChart", // 2
            "renderSTOCHChart", // 3
            "renderRSIChart", // 4
            "renderADXChart", // 5
            "renderCCIChart", // 6
            "renderBBANDSChart", // 7
            "renderMACDChart", // 8
    };
    private static final int[] STOCK_TABLE_IDS = {
            R.id.stock_symbol,
            R.id.stock_price,
            R.id.stock_change,
            R.id.stock_timestamp,
            R.id.stock_open,
            R.id.stock_close,
            R.id.stock_range,
            R.id.stock_volume
    };
    private static final int DEFAULT_RENDERER_IDX = 0;

    private WebView webView;
    private Spinner spinner;
    private Button changeButton;
    private TextView[] stockTbl;
    private ImageView changeImage;


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
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        super.setRetainInstance(true);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final StockSymbol symbol = StockSymbol.fromBundle(getArguments());
        Log.d(TAG, "onCreateView with: " + StockSymbol.stringify(symbol));
        View view = inflater.inflate(R.layout.fragment_current, container, false);

        if (this.webView == null || this.spinner == null || this.changeButton == null) {
            //setupView();
        }

        this.stockTbl = new TextView[STOCK_TABLE_IDS.length];
        for (int i = 0; i < STOCK_TABLE_IDS.length; ++i) {
            int id = STOCK_TABLE_IDS[i];
            this.stockTbl[i] = view.findViewById(id);
        }
        VolleyFactory.getInstance(this.getContext()).addToRequestQueue(prepareRequest(App.STOCK_TABLE_URL+symbol.getSymbol(), this.stockTbl, this.changeImage));

        // Setup Spinner
        this.spinner = (Spinner) view.findViewById(R.id.indicator_spinner);
        ArrayAdapter<CharSequence> indicator_items = ArrayAdapter.createFromResource(view.getContext(), R.array.indicator_items, android.R.layout.simple_spinner_item);
        indicator_items.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(indicator_items);

        // Setup WebView
        this.webView = (WebView) view.findViewById(R.id.indicator_web_view);
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Setup webview with default chart
                view.loadUrl(getJSURLFromSymbol(symbol.getSymbol(), DEFAULT_RENDERER_IDX));
            }
        });
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.setWebContentsDebuggingEnabled(true);
        this.webView.loadUrl(App.INDICATOR_FILE_URL);

        // Setup button
        this.changeButton = (Button) view.findViewById(R.id.indicator_change_button);
        this.changeButton.setOnClickListener(new View.OnClickListener() {
            Spinner _spinner = spinner;
            WebView _webView = webView;
            @Override
            public void onClick(View v) {
                int pos = _spinner.getSelectedItemPosition();
                Log.d(getClass().getName(), "Indicator Button Click. Spinner at pos: "+pos);
                if (pos >= 0 && pos < JS_FUNCS.length) {
                    Log.d(getClass().getName(), "Valid pos clicked");
                    String jsInvokeUrl = getJSURLFromSymbol(symbol.getSymbol(), pos);
                    _webView.loadUrl(jsInvokeUrl);
                    Log.d(getClass().getName(), "Called " + jsInvokeUrl + " in JS context");
                }
            }
        });

        // Setup Spinner onChange
        this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            Button _changeButton = changeButton;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(getClass().getName(), "Spinner ItemClick: " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    private JsonObjectRequest prepareRequest(String wsUrl, final TextView[] stockTbl, final ImageView changeImage) {
        Log.d(TAG, "Preparing request for news feed: " + wsUrl);
        return new JsonObjectRequest(wsUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(getClass().getName(), "Recv response from server");
                try {
                    JSONObject tbl = response.getJSONObject("data").getJSONObject("infoTable");
                    stockTbl[0].setText(tbl.getString("symbol"));
                    stockTbl[1].setText(tbl.getString("price"));
                    stockTbl[2].setText(tbl.getString("change"));
                    if (tbl.getString("change").startsWith("-")) {
                        setDownImage(changeImage);
                    } else {
                        setUpImage(changeImage);
                    }
                    stockTbl[3].setText(tbl.getString("timestamp"));
                    stockTbl[4].setText(tbl.getString("open"));
                    stockTbl[5].setText(tbl.getString("prevClose"));
                    stockTbl[6].setText(tbl.getString("range"));
                    stockTbl[7].setText(tbl.getString("volume"));


                } catch (JSONException e) {
                    Log.e(getClass().getName(), "Error while parsing response: ", e);
                }
            }
        }, new CommonJsonErrorHandler());
    }

    private void setUpImage(ImageView target) {

    }

    private void setDownImage(ImageView target) {

    }

    private void setupView() {

    }

    private static String getJSURLFromSymbol(String symbol, int pos) {
        return "javascript:" + JS_FUNCS[pos] + "('" + symbol + "')";
    }

    private static void DisableButton(Button button) {
        button.setClickable(false);
        //button.setEnabled(false);
        button.setAlpha(0.5f);
    }
    private static void EnableButton(Button button) {
        button.setClickable(true);
        //button.setEnabled(true);
        button.setAlpha(1.0f);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Saving Instance");
    }
}