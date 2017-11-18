package com.vb.stockkeeper.model;

import com.vb.stockkeeper.activity.details.fragment.NewsFeedFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vb on 11/18/17.
 */

public class NewsfeedItem {

    private static final String TAG = NewsFeedFragment.class.getCanonicalName();

    private static final SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

    /**
     * Stub to test formatter
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(formatter.format(new Date()));
    }

    private String title;
    private String Author;
    private Date date;
    private String link;

    public NewsfeedItem() { }

    public NewsfeedItem(String title, String author, Date date, String link) {
        this.title = title;
        Author = author;
        this.date = date;
        this.link = link;
    }

    public static NewsfeedItem[] getMockArray() {
        String[] links = {"http://www.google.com", "http://www.bing.com/q=123", "http://www.cricinfo.com"};
        NewsfeedItem[] items = new NewsfeedItem[20];
        for (int i = 0; i < items.length; ++i) {
            items[i] = new NewsfeedItem("Title"+i, "Author"+i, new Date(), links[i%3]);
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
}
