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
import android.widget.Spinner;

import com.vb.stockkeeper.App;
import com.vb.stockkeeper.R;
import com.vb.stockkeeper.model.StockSymbol;

import org.json.JSONObject;

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
        View view = inflater.inflate(R.layout.fragment_current, container, false);

        // Setup Spinner
        Spinner spinner = (Spinner) view.findViewById(R.id.indicator_spinner);
        ArrayAdapter<CharSequence> indicator_items = ArrayAdapter.createFromResource(view.getContext(), R.array.indicator_items, android.R.layout.simple_spinner_item);
        indicator_items.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(indicator_items);

        // Setup WebView
        final WebView webView = (WebView) view.findViewById(R.id.indicator_web_view);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(App.INDICATOR_FILE_URL);

        // Setup button
        final Button changeButton = (Button) view.findViewById(R.id.indicator_change_button);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(getClass().getName(), "Indicator Button Click");
                webView.loadUrl("javascript:foo()");
                Log.d(getClass().getName(), "Called javascript:foo() in JS context");
            }
        });

        // Setup Spinner onChange
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
}