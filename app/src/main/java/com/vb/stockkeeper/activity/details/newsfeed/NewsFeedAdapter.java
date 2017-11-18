package com.vb.stockkeeper.activity.details.newsfeed;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.vb.stockkeeper.R;
import com.vb.stockkeeper.model.NewsfeedItem;

import java.util.List;

/**
 * Created by vb on 11/18/17.
 */

public class NewsFeedAdapter extends BaseAdapter {

    private static final String TAG = NewsFeedAdapter.class.getCanonicalName();

    private Context mContext;
    private List<NewsfeedItem> items;
    private static LayoutInflater inflater;

    public NewsFeedAdapter(Context mContext, List<NewsfeedItem> items) {
        this.mContext = mContext;
        this.items = items;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return fillViewFromItem(this.items.get(position));
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    private static View fillViewFromItem(final NewsfeedItem item) {
        final View view = inflater.inflate(R.layout.news_feed_item, null);
        final String linkUrl = item.getLink();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(getClass().getCanonicalName(), "Opening link in browser: " + linkUrl);
                Toast.makeText(v.getContext(), linkUrl, Toast.LENGTH_SHORT).show();
                Intent viewPageIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl));
                v.getContext().startActivity(viewPageIntent);
            }
        });
        TextView title = (TextView) view.findViewById(R.id.news_feed_title);
        title.setText(item.getTitle());
        ((TextView) view.findViewById(R.id.news_feed_author)).setText(item.getAuthor());
        ((TextView) view.findViewById(R.id.news_feed_date)).setText(item.getFormattedDate());
        return view;
    }
}
