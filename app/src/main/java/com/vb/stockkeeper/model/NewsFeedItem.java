package com.vb.stockkeeper.model;

import com.vb.stockkeeper.activity.details.newsfeed.fragment.NewsFeedFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vb on 11/18/17.
 */

public class NewsFeedItem {

    private static final String TAG = NewsFeedFragment.class.getCanonicalName();

    private static final SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
    private static final String JSON_TITLE_KEY = "title";
    private static final String JSON_AUTHOR_KEY = "author";
    private static final String JSON_DATE_KEY = "pubDate";
    private static final String JSON_LINK_KEY = "link";


    public static void main(String[] args) {
        System.out.println(formatter.format(new Date()));
    }

    private String title;
    private String Author;
    private Date date;
    private String link;

    public NewsFeedItem() { }

    public NewsFeedItem(String title, String author, Date date, String link) {
        this.title = title;
        Author = author;
        this.date = date;
        this.link = link;
    }

    public static NewsFeedItem fromJsonObject(JSONObject obj) throws JSONException {
        return new NewsFeedItem(
                obj.getString(JSON_TITLE_KEY),
                obj.getString(JSON_AUTHOR_KEY),
                new Date(obj.getString(JSON_DATE_KEY)),
                obj.getString(JSON_LINK_KEY)
                );
    }

    public static NewsFeedItem[] getMockArray() {
        String[] links = {"http://www.google.com", "http://www.bing.com/q=123", "http://www.cricinfo.com"};
        NewsFeedItem[] items = new NewsFeedItem[20];
        for (int i = 0; i < items.length; ++i) {
            items[i] = new NewsFeedItem("Title"+i, "Author"+i, new Date(), links[i%3]);
        }
        return items;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFormattedDate() {
        return formatter.format(this.getDate());
    }

    public static String stringify(NewsFeedItem item) {
        return item == null ? "" : new StringBuilder()
                .append("{")
                .append("title:").append(item.getTitle())
                .append(",author:").append(item.getAuthor())
                .append(",date:").append(item.getFormattedDate())
                .append(",link:").append(item.getLink())
                .append("}")
                .toString();
    }
}
