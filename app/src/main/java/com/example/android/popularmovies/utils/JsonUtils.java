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
    private static final String TITLE = "title";

    public static List<Movie> parseMovieResultsJson(String json) throws JSONException {
        JSONObject response = new JSONObject(json);

        List<Movie> movieList = new ArrayList<>();
        JSONArray resultsJsonArrray = response.optJSONArray(RESULTS);
        for (int i = 0; i < resultsJsonArrray.length(); i++) {
            movieList.add(parseMovie(resultsJsonArrray.getString(i)));
        }
        return movieList;
    }

    private static Movie parseMovie(String json) throws JSONException {
        JSONObject movieJson = new JSONObject(json);
        int id = movieJson.getInt(ID);
        String title = movieJson.getString(TITLE);
        return new Movie(id, title);
    }

}
