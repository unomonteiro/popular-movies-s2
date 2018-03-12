package com.example.android.popularmovies.model;

public class Movie {

    private final int mId;
    private final String mTitle;

    public Movie(int id, String title) {
        mId = id;
        mTitle = title;
    }

    @Override
    public String toString() {
        return "id=" + mId + ", title='" + mTitle + '\'';
    }
}