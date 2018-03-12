package com.example.android.popularmovies.model;

public class Movie {

    private final int mId;
    private final String mTitle;
    private final String mPosterPath;

    public Movie(int id, String title, String posterPath) {
        mId = id;
        mTitle = title;
        mPosterPath = posterPath;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    @Override
    public String toString() {
        return "id=" + mId + ", title='" + mTitle + '\'';
    }
}
