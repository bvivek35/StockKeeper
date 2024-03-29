package com.vb.stockkeeper.activity.search.handler;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vb.stockkeeper.App;
import com.vb.stockkeeper.R;
import com.vb.stockkeeper.activity.details.DetailsActivity;
import com.vb.stockkeeper.activity.search.fragment.SearchFragment;
import com.vb.stockkeeper.model.StockSymbol;

/**
 * Created by vb on 11/14/17.
 */

public class GetQuoteButtonHandler implements View.OnClickListener {
    private static final String TAG = GetQuoteButtonHandler.class.getCanonicalName();
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
            if (selectedSymbol == null)
                selectedSymbol = new StockSymbol(input.trim());
            Log.d(TAG, "Selected Symbol : " + StockSymbol.stringify(selectedSymbol));
            Intent startDetailsPage = new Intent(v.getContext(), App.DETAILS_ACTIVITY);
            StockSymbol.addToIntent(startDetailsPage, selectedSymbol);
            v.getContext().startActivity(startDetailsPage);
        }
    }
}
