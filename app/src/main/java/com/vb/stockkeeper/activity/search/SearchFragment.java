package com.vb.stockkeeper.activity.search;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.vb.stockkeeper.R;
import com.vb.stockkeeper.model.StockSymbol;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    private static final String TAG = SearchFragment.class.getCanonicalName();

    private StockSymbol selectedSymbol;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getView().findViewById(R.id.stock_input);
        Log.d(TAG, "Fragment Created");
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ((Button) view.findViewById(R.id.clear_button)).setOnClickListener(new ClearButtonHandler());
        ((Button) view.findViewById(R.id.get_quote_button)).setOnClickListener(new GetQuoteButtonHandler());
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.stock_input);
        autoCompleteTextView.addTextChangedListener(new StockSearchChangeHandler(autoCompleteTextView));
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StockSymbol selectedSymbol = (StockSymbol) parent.getAdapter().getItem(position);
                setSelectedSymbol(selectedSymbol);
                Log.d(TAG, "Selected: " + StockSymbol.stringify(selectedSymbol));
                Toast.makeText(view.getContext(), selectedSymbol.getSymbol(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    public StockSymbol getSelectedSymbol() {
        return selectedSymbol;
    }

    public void setSelectedSymbol(StockSymbol selectedSymbol) {
        this.selectedSymbol = selectedSymbol;
    }
}
