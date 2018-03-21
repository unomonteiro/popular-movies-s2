package com.example.android.popularmovies.model;

public class Review {
    private final String mAuthor;
    private final String mContent;

    public Review(String author, String content) {
        mAuthor = author;
        mContent = content;
    }


    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }
}
