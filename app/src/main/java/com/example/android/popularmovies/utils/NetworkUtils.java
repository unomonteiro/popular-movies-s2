package com.example.android.popularmovies.utils;

import android.content.Context;
import android.net.Uri;

import com.example.android.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String THE_MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String PARAM_API_KEY = "api_key";
    private static final String THE_MOVIE_DB_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";

    private static final String THE_MOVIE_DB_FILE_SIZE = "w500";
    private static String VIDEOS_SUFIX = "/videos";
    private static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch";
    private static final String YOUTUBE_VIDEO = "v";
    private static String YOUTUBE_BASE_THUMBNAIL_URL = "http://img.youtube.com/vi/";
    private static String YOUTUBE_MAIN_THUMBNAIL_SUFIX = "/0.jpg";

    public static URL buildUrl(Context context, String orderBy) {

        Uri buildUri = Uri.parse(THE_MOVIE_DB_BASE_URL + orderBy).buildUpon()
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

    public static URL buildTrailersUrl(Context context, String movieId) {

        Uri buildUri = Uri.parse(THE_MOVIE_DB_BASE_URL + movieId + VIDEOS_SUFIX).buildUpon()
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

    public static String getImageUrl(String posterUrl) {
        return THE_MOVIE_DB_IMAGE_BASE_URL + THE_MOVIE_DB_FILE_SIZE + posterUrl;
    }

    public static String getYoutubeThumbnail(String key) {
        return YOUTUBE_BASE_THUMBNAIL_URL.concat(key).concat(YOUTUBE_MAIN_THUMBNAIL_SUFIX);
    }

    public static Uri getYoutubeUri(String key) {
        Uri uri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_VIDEO, key)
                .build();
        return uri;
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
