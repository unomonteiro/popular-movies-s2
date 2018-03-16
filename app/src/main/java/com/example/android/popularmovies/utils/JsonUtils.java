package com.example.android.popularmovies.utils;

import com.example.android.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    private static final String RESULTS = "results";
    private static final String ID = "id";
    private static final String ORIGINAL_TITLE = "original_title";
    private static final String POSTER_PATH = "poster_path";
    private static final String OVERVIEW = "overview";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String RELEASE_DATE = "release_date";

    public static List<Movie> parseMovieResultsJson(String json) throws JSONException {
        JSONObject response = new JSONObject(json);

        List<Movie> movieList = new ArrayList<>();
        JSONArray resultsJsonArray = response.optJSONArray(RESULTS);
        for (int i = 0; i < resultsJsonArray.length(); i++) {
            movieList.add(parseMovie(resultsJsonArray.getString(i)));
        }
        return movieList;
    }

    private static Movie parseMovie(String json) throws JSONException {
        JSONObject movieJson = new JSONObject(json);
        int id = movieJson.getInt(ID);
        String posterPath = movieJson.getString(POSTER_PATH);
        String originalTitle = movieJson.getString(ORIGINAL_TITLE);
        String overview = movieJson.getString(OVERVIEW);
        double voteAverage = movieJson.getLong(VOTE_AVERAGE);
        String releaseDate = movieJson.getString(RELEASE_DATE);
        return new Movie(id, originalTitle, posterPath, overview, voteAverage, releaseDate);
    }

}
