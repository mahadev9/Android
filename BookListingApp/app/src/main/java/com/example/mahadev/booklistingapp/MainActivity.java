package com.example.mahadev.booklistingapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    static final String SEARCH_RESULTS = "booksSearchResults";

    private static final String BOOK_API = "https://www.googleapis.com/books/v1/volumes?q=search+";

    EditText searchKeyword;
    ImageButton seachButton;
    BookAdapter bookAdapter;
    ListView bookListView;
    View emptyState, noInternet, progessBar, emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchKeyword = (EditText) findViewById(R.id.search_keyword);
        seachButton = (ImageButton) findViewById(R.id.seach_button);

        emptyState = (View) findViewById(R.id.empty_state);
        noInternet = (View) findViewById(R.id.no_internet);
        progessBar = (View) findViewById(R.id.loading_spinner);
        emptyView = (View) findViewById(R.id.empty_view);

        bookAdapter = new BookAdapter(this, new ArrayList<BookDetails>());

        bookListView = (ListView) findViewById(R.id.book_list);
        bookListView.setAdapter(bookAdapter);

        seachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isInternetConnectionAvailable()) {
                    BookAsyncTask task = new BookAsyncTask();
                    task.execute(getUrlForHttpRequest());
                }
                else {
                    bookAdapter.clear();
                    noInternet.setVisibility(View.VISIBLE);
                    emptyState.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, R.string.no_internet, Toast.LENGTH_LONG).show();
                }
            }
        });

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookDetails currentBook = bookAdapter.getItem(position);

                Uri bookUri = Uri.parse(currentBook.getBookPreview());

                Intent url = new Intent(Intent.ACTION_VIEW, bookUri);

                startActivity(url);
            }
        });

        if (savedInstanceState != null) {
            emptyState.setVisibility(View.GONE);
            BookDetails[] books = (BookDetails[]) savedInstanceState.getParcelableArray(SEARCH_RESULTS);
            bookAdapter.addAll(books);
        }
    }

    private boolean isInternetConnectionAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private String getUrlForHttpRequest() {
        final String baseUrl = BOOK_API;
        String formatUserInput = searchKeyword.getText().toString().trim().replaceAll("\\s+","+");
        String url = baseUrl + formatUserInput;
        return url;
    }

    private class BookAsyncTask extends AsyncTask<String, Void, List<BookDetails>> {

        @Override
        protected void onPreExecute() {
            emptyView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
            progessBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<BookDetails> doInBackground(String... strings) {

            if (strings.length < 1 || strings[0] == null) {
                return null;
            }

            return QueryUtils.fetchBookDetials(strings[0]);
        }

        @Override
        protected void onPostExecute(List<BookDetails> data) {

            emptyView.setVisibility(View.GONE);
            progessBar.setVisibility(View.GONE);
            noInternet.setVisibility(View.GONE);
            if (bookAdapter != null) {
                bookAdapter.clear();
            }
            if (data != null && !data.isEmpty()) {
                bookAdapter.addAll(data);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BookDetails[] books = new BookDetails[bookAdapter.getCount()];
        for (int i = 0; i < books.length; i++) {
            books[i] = bookAdapter.getItem(i);
        }
        outState.putParcelableArray(SEARCH_RESULTS, (Parcelable[]) books);
    }
}
