package com.vb.stockkeeper.activity.search.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.vb.stockkeeper.App;
import com.vb.stockkeeper.R;
import com.vb.stockkeeper.activity.DialogUtils;
import com.vb.stockkeeper.model.FavSymbol;
import com.vb.stockkeeper.model.StockSymbol;
import com.vb.stockkeeper.net.CommonJsonErrorHandler;
import com.vb.stockkeeper.net.VolleyFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FavSymbolsFragment extends Fragment {

    private static final String TAG = FavSymbolsFragment.class.getCanonicalName();
    private static final int POS_DESCENDING = 2;
    private static final long REFRESH_RATE = 6000;

    private SwitchCompat autoRefreshToggle;
    private ImageView refreshButton;
    private Spinner sortColSpinner;
    private Spinner sortOrderSpinner;
    private ListView favList;
    private FavListAdapter favListAdapter;
    private Handler refreshHandler;
    private Runnable refreshRunnable;
    private ProgressDialog refreshDialog;

    private static final Map<Integer, Comparator<FavSymbol>> POS_COMPARATOR_MAP = new HashMap<Integer, Comparator<FavSymbol>>();
    static {
        POS_COMPARATOR_MAP.put(1, FavSymbol.SYMBOL_COMPARATOR);
        POS_COMPARATOR_MAP.put(2, FavSymbol.PRICE_COMPARATOR);
        POS_COMPARATOR_MAP.put(3, FavSymbol.CHANGE_COMPARATOR);
    }


    public FavSymbolsFragment() { }

    public static FavSymbolsFragment newInstance() {
        FavSymbolsFragment fragment = new FavSymbolsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        //favListAdapter.getFavSymbols().get(info.position);
        menu.setHeaderTitle("Choose");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Delete") {
            Log.d(TAG, "Deleting: " + item.getItemId());
            int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
            FavSymbol removed = favListAdapter.getFavSymbols().remove(position);
            ((App) getContext().getApplicationContext()).removeSymbolFromSharedPref(removed.getSymbol());
            favListAdapter.notifyDataSetChanged();
        } else {
            return false;
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "Inflating fav symbols layout");
        final View view = inflater.inflate(R.layout.fragment_fav_symbols, container, false);

        registerForContextMenu(view.findViewById(R.id.fav_list));

        // Setup Listing
        this.favList = view.findViewById(R.id.fav_list);
        this.favListAdapter = new FavListAdapter(getContext(), new ArrayList<FavSymbol>());
        this.favList.setAdapter(this.favListAdapter);
        Set<String> favSymbols = ((App) view.getContext().getApplicationContext()).getAllSymbolsInSharedPref();
        if (favSymbols.size() > 0) {
            refreshDialog = DialogUtils.dismissProgressDialog(refreshDialog);
            refreshDialog = DialogUtils.showProgressDialog(getActivity(), "");
            VolleyFactory.getInstance(view.getContext()).addToRequestQueue(prepareRequest(App.FAV_DATA_URL, favSymbols, this.favListAdapter));
        }

        // Setup refresh button
        this.refreshButton = view.findViewById(R.id.refresh_button);
        this.refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Set<String> favSymbols = ((App) v.getContext().getApplicationContext()).getAllSymbolsInSharedPref();
                if (favSymbols.size() > 0) {
                    refreshDialog = DialogUtils.dismissProgressDialog(refreshDialog);
                    refreshDialog = DialogUtils.showProgressDialog(getActivity(), "");
                    VolleyFactory.getInstance(v.getContext()).addToRequestQueue(prepareRequest(App.FAV_DATA_URL, favSymbols, favListAdapter));
                }
            }
        });

        // Setup Spinner sortCol
        this.sortColSpinner = view.findViewById(R.id.sort_col);
        this.sortColSpinner.setAdapter(new FavOptionsAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.sort_cols)));
        this.sortColSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isFirst = false;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isFirst) {
                    isFirst = true;
                    ((FavOptionsAdapter)sortColSpinner.getAdapter()).setShowDefaultOption(false);
                }
                if (position == 0) {
                    return;
                }
                List<FavSymbol> entries = favListAdapter.getFavSymbols();
                int colPos = position;
                int orderPos = sortOrderSpinner.getSelectedItemPosition();
                if (colPos != 0 && orderPos != 0) {
                    Collections.sort(entries, getComparator(colPos, orderPos));
                    favListAdapter.setFavSymbols(entries);
                    favListAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Setup Spinner sortOrder
        this.sortOrderSpinner = view.findViewById(R.id.sort_order);
        this.sortOrderSpinner.setAdapter(new FavOptionsAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.sort_order)));
        this.sortOrderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isFirst = false;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isFirst) {
                    isFirst = true;
                    ((FavOptionsAdapter)sortOrderSpinner.getAdapter()).setShowDefaultOption(false);
                }
                if (position == 0) {
                    return;
                }
                List<FavSymbol> entries = favListAdapter.getFavSymbols();
                int colPos = sortColSpinner.getSelectedItemPosition();
                int orderPos = position;
                if (colPos != 0 && orderPos != 0) {
                    Collections.sort(entries, getComparator(sortColSpinner.getSelectedItemPosition(), position));
                    favListAdapter.setFavSymbols(entries);
                    favListAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        this.refreshHandler = new Handler();
        this.refreshRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d(getClass().getName(), "Refreshing !");
                Set<String> favSymbols = ((App) view.getContext().getApplicationContext()).getAllSymbolsInSharedPref();
                if (favSymbols.size() > 0) {
                    refreshDialog = DialogUtils.dismissProgressDialog(refreshDialog);
                    refreshDialog = DialogUtils.showProgressDialog(getActivity(), "");
                    VolleyFactory.getInstance(view.getContext()).addToRequestQueue(prepareRequest(App.FAV_DATA_URL, favSymbols, favListAdapter));
                }
                refreshHandler.postDelayed(this, REFRESH_RATE);
            }
        };
        this.autoRefreshToggle = (SwitchCompat) view.findViewById(R.id.auto_refresh_toggle);
        this.autoRefreshToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d(getClass().getName(), "AutoRefresh checked");
                    refreshHandler.postDelayed(refreshRunnable, REFRESH_RATE);
                } else {
                    Log.d(getClass().getName(), "AutoRefresh unchecked");
                    refreshHandler.removeCallbacks(refreshRunnable);
                }
            }
        });
        return view;
    }

    private JsonObjectRequest prepareRequest(String wsUrl, Set<String> favSymbols, final FavListAdapter outAdapter) {
        String args = "";
        for (String sym: favSymbols) {
            args += sym + "|";
        }
        args = args.substring(0, args.length()-1);
        String fullURL = App.FAV_DATA_URL + "?symbols=" + args;

        return new JsonObjectRequest(fullURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    List<FavSymbol> tmp = outAdapter.getFavSymbols();
                    tmp.clear();
                    JSONArray arr = response.getJSONArray("data");
                    JSONObject obj;
                    for (int i = 0; i < arr.length(); ++i) {
                        obj = arr.getJSONObject(i);
                        if ("success".equalsIgnoreCase(obj.getString("status")))
                            tmp.add(FavSymbol.parseFromJSONObject(obj));
                    }
                    outAdapter.setFavSymbols(tmp);
                    outAdapter.notifyDataSetChanged();
                    refreshDialog = DialogUtils.dismissProgressDialog(refreshDialog);
                } catch (JSONException e) {
                    Log.e(getClass().getName(), "Error parsing server response: ", e);
                }
            }
        },  new CommonJsonErrorHandler() {
            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                refreshDialog = DialogUtils.dismissProgressDialog(refreshDialog);
            }
        });
    }

    private static Comparator<FavSymbol> getComparator(int colPosition, int orderPosition) {
        if (colPosition == 0 || orderPosition == 0)
            return null;
        Comparator<FavSymbol> tmp = POS_COMPARATOR_MAP.get(colPosition);
        if (orderPosition == POS_DESCENDING)
            return Collections.reverseOrder(tmp);
        return tmp;
    }
}

class FavListAdapter extends ArrayAdapter<FavSymbol> {

    private List<FavSymbol> favSymbols;
    private static LayoutInflater inflater;

    public FavListAdapter(@NonNull Context context, @NonNull List<FavSymbol> objects) {
        super(context, 0, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.favSymbols = objects;
    }

    public List<FavSymbol> getFavSymbols() {
        return favSymbols;
    }

    public void setFavSymbols(List<FavSymbol> favSymbols) {
        this.favSymbols = favSymbols;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return buildViewFromList(this.favSymbols.get(position));
    }

    private static final View buildViewFromList(FavSymbol item) {
        View ret = inflater.inflate(R.layout.fav_symbol_item, null);
        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String symbol = ((TextView) v.findViewById(R.id.fav_item_symbol)).getText().toString().trim();
                StockSymbol tmp = new StockSymbol(symbol);
                Intent startDetailsPage = new Intent(v.getContext(), App.DETAILS_ACTIVITY);
                StockSymbol.addToIntent(startDetailsPage, tmp);
                v.getContext().startActivity(startDetailsPage);
            }
        });
        ret.setLongClickable(true);
        ((TextView) ret.findViewById(R.id.fav_item_symbol)).setText(item.getSymbol());
        ((TextView) ret.findViewById(R.id.fav_item_price)).setText(item.getDisplayPrice());
        TextView changeView = (TextView) ret.findViewById(R.id.fav_item_change);
        int color = R.color.green;
        if (item.getDisplayChange().startsWith("-")) {
            color = R.color.red;
        }
        changeView.setTextColor(color);
        changeView.setText(item.getDisplayChange());

        return ret;
    }

    @Override
    public int getCount() {return this.favSymbols.size();}
}

class FavOptionsAdapter extends ArrayAdapter<String> {

    private boolean showDefaultOption;

    public FavOptionsAdapter(@NonNull Context context, int resource, @NonNull String[] objects) {
        super(context, resource, objects);
        this.showDefaultOption = true;
    }

    public boolean isShowDefaultOption() {
        return showDefaultOption;
    }

    public void setShowDefaultOption(boolean showDefaultOption) {
        this.showDefaultOption = showDefaultOption;
    }

    @Override
    public boolean isEnabled(int position) {
        //return super.isEnabled(position);
        if (position == 0 && !this.showDefaultOption)
            return false;
        return true;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View tmp = super.getDropDownView(position, convertView, parent);
        TextView mTextView = (TextView) tmp;
        if (!isEnabled(position))
            mTextView.setTextColor(Color.GRAY);
        return tmp;
    }
}


