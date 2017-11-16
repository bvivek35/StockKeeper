package com.vb.stockkeeper.net;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by vb on 11/15/17.
 */

public class CommonJsonErrorHandler implements Response.ErrorListener {
    private static final String TAG = CommonJsonErrorHandler.class.getCanonicalName();

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e(TAG, "Error while performing request: ", error);
    }
}
