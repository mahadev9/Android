package com.example.mahadev.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsDetails>>, SwipeRefreshLayout.OnRefreshListener{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String NEWS_API = "https://content.guardianapis.com/search?api-key=c01c7c4d-7e80-410d-be8a-da31ba976311&show-tags=contributor";

    private NewsAdapter newsAdapter;
    private SwipeRefreshLayout swipe;
    private TextView noInternetTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe.setOnRefreshListener(this);
        swipe.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        noInternetTextView = (TextView) findViewById(R.id.no_internet_textview);

        ListView newsListView = (ListView) findViewById(R.id.list_view);
        newsAdapter = new NewsAdapter(this, new ArrayList<NewsDetails>());

        newsListView.setAdapter(newsAdapter);

        if (isInternetConnectionAvailable()) {
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(0, null, this);
            noInternetTextView.setVisibility(View.GONE);
        }
        else {
            Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            newsAdapter.clear();
            noInternetTextView.setVisibility(View.VISIBLE);
        }

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(LOG_TAG, "On Item Click");

                NewsDetails currentNews = newsAdapter.getItem(position);
                Uri newsUri = Uri.parse(currentNews.getWebUrl());
                Intent url = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(url);
            }
        });
    }

    @Override
    public Loader<List<NewsDetails>> onCreateLoader(int id, Bundle args) {
        return new NewsLoader(this, NEWS_API);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsDetails>> loader, List<NewsDetails> data) {
        newsAdapter.clear();
        swipe.setRefreshing(false);

        if (data != null && !data.isEmpty()) {
            newsAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        newsAdapter.clear();
    }

    @Override
    public void onRefresh() {
        if (isInternetConnectionAvailable()) {
            getSupportLoaderManager().restartLoader(0, null, this);
            noInternetTextView.setVisibility(View.GONE);
        }
        else {
            Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            newsAdapter.clear();
            noInternetTextView.setVisibility(View.VISIBLE);
            swipe.setRefreshing(false);
        }
    }

    private boolean isInternetConnectionAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
