package com.vb.stockkeeper.activity.search.handler;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vb.stockkeeper.R;

/**
 * Created by vb on 11/14/17.
 */

public class ClearButtonHandler implements View.OnClickListener {
    private static final String TAG = ClearButtonHandler.class.getCanonicalName();

    @Override
    public void onClick(View v) {
        TextView stockInput = (TextView) ((ViewGroup) v.getParent()).findViewById(R.id.stock_input);
        Log.d(TAG, "clearing StockInput");
        stockInput.setText("");
        stockInput.requestFocus();
    }
}
