package com.vb.stockkeeper.activity.search;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.vb.stockkeeper.App;
import com.vb.stockkeeper.model.StockSymbol;
import com.vb.stockkeeper.net.CommonJsonErrorHandler;
import com.vb.stockkeeper.net.JsonResponseConstants;
import com.vb.stockkeeper.net.VolleyFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vb on 11/14/17.
 */

public class StockSearchChangeHandler implements TextWatcher {

    private static final String TAG = StockSearchChangeHandler.class.getCanonicalName();

    private AutoCompleteTextView view;

    public StockSearchChangeHandler(AutoCompleteTextView view) {
        this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (this.view.isPerformingCompletion()) {
            Log.d(TAG, "View completing user click. Ignoring onTextChanged");
            return;
        }
        Log.d(TAG, "New Text: " + s);
        String input = s.toString().trim();
        if (input.length() <= 1) {
            Log.d(TAG, "Not enough chars in input to force autocomplete");
        } else {
            Log.d(TAG, "Need to get autocomplete list for: " + input);
            Log.i(TAG, "Starting autocomplete request");
            String wsURL = App.AUTOCOMPLETE_URL + input;
            Log.d(TAG, "Connecting to: " + wsURL);
            VolleyFactory.getInstance(view.getContext()).addToRequestQueue(prepareRequest(wsURL));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {}

    private JsonObjectRequest prepareRequest(String wsUrl) {
        return new JsonObjectRequest(wsUrl, null, new AutocompleteSetter(this.view), new CommonJsonErrorHandler());
    }
}

class AutocompleteSetter implements Response.Listener<JSONObject> {

    private static final String TAG = AutocompleteSetter.class.getCanonicalName();
    private static final int MAX_RESULTS = 5;

    AutoCompleteTextView view;
    AutocompleteSetter(AutoCompleteTextView view) {
        this.view = view;
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            String status = response.getString(JsonResponseConstants.STATUS);
            JSONArray responseArray = response.getJSONArray(JsonResponseConstants.DATA);
            if (JsonResponseConstants.STATUS_SUCCESS.equals(status)) {
                ArrayList<StockSymbol> options = new ArrayList<>();
                JSONObject tmp;
                for (int i = 0; i < MAX_RESULTS && i < responseArray.length(); ++i) {
                    tmp = responseArray.getJSONObject(i);
                    options.add(new StockSymbol(tmp.getString("symbol"), tmp.getString("option")));
                }
                Log.d(TAG, "Filled array with: " + options.toString());
                Log.i(TAG, "Parsed Response. Filling the autocomplete adapter");
                ArrayAdapter<StockSymbol> adapter = new ArrayAdapter<>(this.view.getContext(), android.R.layout.simple_dropdown_item_1line, options);
               this.view.setAdapter(adapter);
               Log.i(TAG, "Notifying dataset change");
               adapter.notifyDataSetChanged();
            } else {
                Log.e(TAG, "Error in JSON response from server: " + response.toString());
            }
            response.getJSONArray(JsonResponseConstants.DATA);
        } catch (JSONException e) {
            Log.e(TAG, "Error while parsing response: " + response.toString(), e);
        }

    }
}