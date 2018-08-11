package com.example.mahadev.booklistingapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mahadev on 24/02/18.
 */

public class BookDetails implements Parcelable{

    private String mBookTitle;

    private String mBookAuthor;

    private String mBookPreview;

    public BookDetails(String vBookTitle, String vBookAuthor, String vBookPreview) {
        mBookTitle = vBookTitle;
        mBookAuthor = vBookAuthor;
        mBookPreview = vBookPreview;
    }

    protected BookDetails(Parcel in) {
        mBookTitle = in.readString();
        mBookAuthor = in.readString();
        mBookPreview = in.readString();
    }

    public static final Creator<BookDetails> CREATOR = new Creator<BookDetails>() {
        @Override
        public BookDetails createFromParcel(Parcel in) {
            return new BookDetails(in);
        }

        @Override
        public BookDetails[] newArray(int size) {
            return new BookDetails[size];
        }
    };

    public String getBookTitle() {
        return mBookTitle;
    }

    public String getBookAuthor() {
        return mBookAuthor;
    }

    public String getBookPreview() {
        return mBookPreview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mBookTitle);
        dest.writeString(mBookAuthor);
        dest.writeString(mBookPreview);
    }
}
