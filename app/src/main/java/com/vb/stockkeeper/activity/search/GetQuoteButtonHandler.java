package com.vb.stockkeeper.activity.search;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vb.stockkeeper.App;
import com.vb.stockkeeper.activity.MainActivity;
import com.vb.stockkeeper.R;
import com.vb.stockkeeper.activity.details.DetailsActivity;
import com.vb.stockkeeper.model.StockSymbol;

/**
 * Created by vb on 11/14/17.
 */

public class GetQuoteButtonHandler implements View.OnClickListener {
    private static final String TAG = GetQuoteButtonHandler.class.getCanonicalName();
    private static final Class Next_ACTIVITY = DetailsActivity.class;

    @Override
    public void onClick(View v) {
        TextView stockInput = (TextView) ((ViewGroup) v.getParent()).findViewById(R.id.stock_input);
        String input = stockInput.getText().toString();
        Log.d(TAG, "StockInput : " + input);
        if ("".equals(input.trim())) {
            Toast.makeText(v.getContext(), R.string.enter_valid_input, Toast.LENGTH_SHORT).show();
            stockInput.setText("");
            stockInput.requestFocus();
        } else {
            Log.i(TAG, "Starting to get quote");
            StockSymbol selectedSymbol = ((SearchFragment)((Activity) v.getContext()).getFragmentManager().findFragmentById(R.id.search_fragment)).getSelectedSymbol();
            Log.d(TAG, "Selected Symbol : " + StockSymbol.stringify(selectedSymbol));
            Intent startDetailsPage = new Intent(v.getContext(), Next_ACTIVITY);
            startDetailsPage.putExtra(App.STOCK_SYMBOL_INTENT_KEY, selectedSymbol);
            v.getContext().startActivity(startDetailsPage);
        }
    }
}
