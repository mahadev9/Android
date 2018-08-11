package com.example.mahadev.newsapp;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.List;

/**
 * Created by mahadev on 05/03/18.
 */

public class NewsLoader extends AsyncTaskLoader<List<NewsDetails>>{

    private static final String LOG_TAG = NewsLoader.class.getSimpleName();

    private String mAPI;

    public NewsLoader(Context context, String api) {
        super(context);
        mAPI = api;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "TEST: onStartingLoading");
        forceLoad();
    }

    @Override
    public List<NewsDetails> loadInBackground() {
        Log.i(LOG_TAG, "TEST: loadInBackground");
        if (mAPI == null) {
            return null;
        }

        return QueryUtils.fetchNewsDetails(mAPI);
    }
}