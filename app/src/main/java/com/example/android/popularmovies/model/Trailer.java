package com.example.android.popularmovies.model;

public class Trailer {
    private String mKey;
    private String mName;

    public Trailer(String key, String name) {

        mKey = key;
        mName = name;
    }

    public String getKey() {
        return mKey;
    }

    public String getName() {
        return mName;
    }
}
