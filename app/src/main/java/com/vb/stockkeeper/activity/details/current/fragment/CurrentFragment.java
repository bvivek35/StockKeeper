package com.vb.stockkeeper.activity.details.current.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.vb.stockkeeper.App;
import com.vb.stockkeeper.R;
import com.vb.stockkeeper.activity.DialogUtils;
import com.vb.stockkeeper.model.NewsFeedItem;
import com.vb.stockkeeper.model.StockSymbol;
import com.vb.stockkeeper.net.CommonJsonErrorHandler;
import com.vb.stockkeeper.net.VolleyFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private static final String JS_FUNC_GET_EXPORT_URL = "getCurrentChartAsImage";
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
    private ImageView fbImage;
    private ProgressDialog loadDialog;


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
        this.changeImage = view.findViewById(R.id.change_image);
        loadDialog = DialogUtils.dismissProgressDialog(loadDialog);
        loadDialog = DialogUtils.showProgressDialog(getActivity(), "");
        VolleyFactory.getInstance(this.getContext()).addToRequestQueue(prepareRequest(App.STOCK_TABLE_URL+symbol.getSymbol(), this.stockTbl, this.changeImage));

        // Setup Spinner
        this.spinner = (Spinner) view.findViewById(R.id.indicator_spinner);
        // Setup Spinner onChange
        this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            Button _changeButton = changeButton;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(getClass().getName(), "Spinner ItemClick: " + position);
                EnableButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // Setup FB logo action
        this.fbImage = view.findViewById(R.id.fbShare);
        this.fbImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("javascript:"+JS_FUNC_GET_EXPORT_URL+"()");
            }
        });

        // Setup WebView
        this.webView = (WebView) view.findViewById(R.id.indicator_web_view);
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Setup webview with default chart
                view.loadUrl(getJSURLFromSymbol(symbol.getSymbol(), DEFAULT_RENDERER_IDX));
                DisableButton();
            }
        });
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.setWebContentsDebuggingEnabled(true);
        this.webView.loadUrl(App.INDICATOR_FILE_URL);
        this.webView.addJavascriptInterface(DialogUtils.JSInterface(getActivity()), App.JS_ANDROID_DIALOG_INTERFACE);

        FacebookSdk.setApplicationId("299859720421493");
        FacebookSdk.sdkInitialize(getContext().getApplicationContext());
        FacebookSdk.setIsDebugEnabled(true);
        for (LoggingBehavior tmp: LoggingBehavior.values())
            FacebookSdk.addLoggingBehavior(tmp);
        this.webView.addJavascriptInterface(new Object () {
            @JavascriptInterface
            public void handleChartURL(String url) {
                Log.d(TAG, "From JSContext, got URL: " + url);
                // Export to FB...
                try {
                    VolleyFactory.getInstance(getContext()).addToRequestQueue(new ImageRequest(url, new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            SharePhoto photo = new SharePhoto.Builder().setBitmap(response).build();
                            SharePhotoContent photoContent = new SharePhotoContent.Builder().addPhoto(photo).build();
                            ShareDialog shareDialog = new ShareDialog(getActivity());
                            shareDialog.show(photoContent);

                        }
                    }, 0, 0, null, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Could not get image for chart", Toast.LENGTH_SHORT).show();
                        }
                    }));
                    /*
                    ShareLinkContent chartContent = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(url))
                            .build();


                    SharePhoto photo = new SharePhoto.Builder().setImageUrl(Uri.parse(url)).build();
                    SharePhotoContent photoContent = new SharePhotoContent.Builder().addPhoto(photo).build();

                    ShareDialog shareDialog = new ShareDialog(getActivity());
                    shareDialog.show(chartContent);
                    */
                } catch (Exception e) {
                    Log.e(getClass().getName(),"Error while posting to FB: ", e);
                }
            }
        }, App.JS_ANDROID_EXPORT_INTERFACE);

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
                DisableButton();
            }
        });

        // Setup Favorite action
        ImageView favImage = (ImageView) view.findViewById(R.id.favoriteImage);
        int tag = R.drawable.emptystar;
        if (((App) view.getContext().getApplicationContext()).isSymbolInSharedPref(symbol.getSymbol())) {
            tag = R.drawable.filledstar;
        }
        favImage.setTag(tag);
        favImage.setImageResource(tag);

        favImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(getClass().getName(), "Fav Image Clicked");
                boolean alreadyFav = ((Integer)((ImageView) v).getTag()) == R.drawable.filledstar;
                App app = (App) v.getContext().getApplicationContext();
                int newImage = R.drawable.emptystar;
                if (alreadyFav) {
                    app.removeSymbolFromSharedPref(symbol.getSymbol());
                    newImage = R.drawable.emptystar;
                } else {
                    app.addSymbolToSharedPref(symbol.getSymbol());
                    newImage = R.drawable.filledstar;
                }
                ((ImageView) v).setImageResource(newImage);
                v.setTag(newImage);
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
                    stockTbl[2].setText(tbl.getString("changeDisp"));
                    if (tbl.getString("changeDisp").startsWith("-")) {
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
                } finally {
                    loadDialog = DialogUtils.dismissProgressDialog(loadDialog);
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

    private void setUpImage(ImageView target) {
        target.setImageResource(R.drawable.up);
    }

    private void setDownImage(ImageView target) {
        target.setImageResource(R.drawable.down);
    }

    private void setupView() {

    }

    private static String getJSURLFromSymbol(String symbol, int pos) {
        return "javascript:" + JS_FUNCS[pos] + "('" + symbol + "')";
    }

    private void DisableButton() {
        Button button = this.changeButton;
        button.setClickable(false);
        //button.setEnabled(false);
        button.setAlpha(0.5f);
    }
    private void EnableButton() {
        Button button = this.changeButton;
        button.setClickable(true);
        //button.setEnabled(true);
        button.setAlpha(1.0f);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Saving Instance");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView()");
        this.webView.destroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }
}

class ImageExporter {

    private static final String TAG = ImageExporter.class.getCanonicalName();

    private Activity mActivity;

    public ImageExporter(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @JavascriptInterface
    public void handleChartURL(String url) {
        Log.d(TAG, url);
    }

}