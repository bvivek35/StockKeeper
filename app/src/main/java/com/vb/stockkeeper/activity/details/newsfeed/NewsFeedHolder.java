package com.vb.stockkeeper.activity.details.newsfeed;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vb.stockkeeper.R;
import com.vb.stockkeeper.model.NewsfeedItem;

/**
 * Created by vb on 11/18/17.
 */

public class NewsFeedHolder {
    private TextView title;
    private TextView author;
    private TextView date;

    public NewsFeedHolder() { }

    public static NewsFeedHolder fromLayout(View view, final NewsfeedItem item) {
        view.setOnClickListener(new View.OnClickListener() {
            private String url = item.getLink();
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), url, Toast.LENGTH_SHORT).show();
            }
        });
        TextView title = (TextView) view.findViewById(R.id.news_feed_title);
        title.setText(item.getTitle());
        ((TextView) view.findViewById(R.id.news_feed_author)).setText(item.getAuthor());
        ((TextView) view.findViewById(R.id.news_feed_author)).setText(item.getFormattedDate());
        return null;
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public TextView getAuthor() {
        return author;
    }

    public void setAuthor(TextView author) {
        this.author = author;
    }

    public TextView getDate() {
        return date;
    }

    public void setDate(TextView date) {
        this.date = date;
    }
}
