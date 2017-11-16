package com.vb.stockkeeper.net;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by vb on 11/15/17.
 */

public class VolleyFactory {
    private static VolleyFactory INSTANCE;

    private RequestQueue requestQueue;
    private static Context ctx;

    public static synchronized VolleyFactory getInstance(Context ctx) {
        if (INSTANCE == null) {
            INSTANCE = new VolleyFactory(ctx);
        }
        return INSTANCE;
    }

    private VolleyFactory(Context ctx) {
        VolleyFactory.ctx = ctx;
        requestQueue = getRequestQueue();
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(VolleyFactory.ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
