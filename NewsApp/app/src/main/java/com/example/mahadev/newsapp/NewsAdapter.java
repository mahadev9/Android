package com.example.mahadev.newsapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by mahadev on 05/03/18.
 */

public class NewsAdapter extends ArrayAdapter<NewsDetails> {

    private static final String LOG_TAG = NewsAdapter.class.getSimpleName();

    public NewsAdapter(Activity context, ArrayList<NewsDetails> newsDetails) {
        super(context, 0, newsDetails);
    }

    @NonNull
    @Override
    public View getView(final int position, View view, @NonNull ViewGroup parent) {
        View listView = view;
        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_view, parent, false);
        }

        final NewsDetails currentNews = getItem(position);

        TextView webTitle = (TextView) listView.findViewById(R.id.web_title);
        webTitle.setText(currentNews.getWebTitle());

        TextView dateView = (TextView) listView.findViewById(R.id.date);
        dateView.setText(formatDate(currentNews.getPublicationDate()));

        TextView sectionNameView = (TextView) listView.findViewById(R.id.section_name);
        sectionNameView.setText(currentNews.getSectionName());

        TextView authorNameView = (TextView) listView.findViewById(R.id.author);
        authorNameView.setText(currentNews.getAuthor());

        return listView;
    }

    private static String formatDate(String rawDate) {

        SimpleDateFormat jsonFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        try {
            String timeZone = "IST";
            Date parsedJsonDate = (Date) jsonFormatter.parse(rawDate);
            SimpleDateFormat finalDateFormatter = new SimpleDateFormat("LLL dd, yyyy   h:mm a", Locale.getDefault());
            if ("".equalsIgnoreCase(timeZone.trim())) {
                timeZone = Calendar.getInstance().getTimeZone().getID();
            }
            finalDateFormatter.setTimeZone(TimeZone.getTimeZone(timeZone));

            return finalDateFormatter.format(parsedJsonDate);
        } catch (ParseException e) {
            Log.e("QueryUtils", "Error parsing JSON date: ", e);
            return "";
        }
    }
}
