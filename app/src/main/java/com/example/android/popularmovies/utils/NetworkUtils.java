package com.example.android.popularmovies.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    public static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String THE_MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String PARAM_API_KEY = "api_key";

    public static URL buildUrl(Context context) {

        Uri buildUri = Uri.parse(THE_MOVIE_DB_BASE_URL + "popular").buildUpon()
                .appendQueryParameter(PARAM_API_KEY, context.getString(R.string.tmdb_api_key))
                .build();

        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getJsonResponseFromUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
