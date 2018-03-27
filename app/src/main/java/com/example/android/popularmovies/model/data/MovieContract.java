package com.example.android.popularmovies.model.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    private static final String AUTHORITY = "com.example.android.popularmovies";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES).build();
        public static final String TABLE_NAME = "movies";
        private static final String COLUMN_TITLE = "title";
        private static final String COLUMN_ORIGINAL_TITLE = "original_title";
        private static final String COLUMN_POSTER_PATH = "poster_path";
        private static final String COLUMN_OVERVIEW = "overview";
        private static final String COLUMN_VOTE_AVERAGE = "vote_average";
        private static final String COLUMN_RELEASE_DATE = "release_date";
    }

}
