package com.example.mahadev.newsapp;

/**
 * Created by mahadev on 05/03/18.
 */

public class NewsDetails {

    private String mWebTitle;

    private String mPublicationDate;

    private String mWebUrl;

    private String mSectionName;

    private String mAuthor;

    public NewsDetails(String vWebTitle, String vPublicationDate, String vWebUrl, String vSectionName,String vAuthor) {
        mWebTitle = vWebTitle;
        mWebUrl = vWebUrl;
        mPublicationDate = vPublicationDate;
        mSectionName = vSectionName;
        mAuthor = vAuthor;
    }

    public String getWebTitle() {
        return mWebTitle;
    }

    public String getPublicationDate() {
        return mPublicationDate;
    }

    public String getWebUrl() {
        return mWebUrl;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getAuthor() {
        return mAuthor;
    }
}
