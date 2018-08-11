package com.example.mahadev.booklistingapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mahadev on 24/02/18.
 */

public class BookAdapter extends ArrayAdapter<BookDetails> {

    private static final String LOG_TAG = BookAdapter.class.getSimpleName();

    public BookAdapter(Activity context, ArrayList<BookDetails> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View BookListView = convertView;
        if(BookListView == null) {
            BookListView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_view, parent, false);
        }
        BookDetails searchKeyword = getItem(position);

        TextView bookTitleTextView = (TextView) BookListView.findViewById(R.id.book_title);
        TextView bookAuthorTextView = (TextView) BookListView.findViewById(R.id.book_author);

        bookTitleTextView.setText(searchKeyword.getBookTitle());
        bookAuthorTextView.setText(searchKeyword.getBookAuthor());

        return BookListView;
    }
}