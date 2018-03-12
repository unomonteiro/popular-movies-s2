package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utils.JsonUtils;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private TextView mResultView;

    private static final int MOVIE_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView picassoIv = findViewById(R.id.picasso_iv);
        Picasso.with(MainActivity.this).load("http://i.imgur.com/DvpvklR.png").into(picassoIv);
        mResultView = findViewById(R.id.results_tv);

        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            String mMovieResults;

            @Override
            protected void onStartLoading() {

                if (mMovieResults != null) {
                    deliverResult(mMovieResults);
                } else {
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {

                URL moviesRequestUrl = NetworkUtils.buildUrl(MainActivity.this);
                try {
                    String jsonResponse = NetworkUtils.getJsonResponseFromUrl(moviesRequestUrl);
                    return jsonResponse;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (data != null) {
            List<Movie> results = null;
            try {
                results = JsonUtils.parseMovieResultsJson(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String resultString = TextUtils.join(",\n", results);
            mResultView.setText(resultString);
        } else {
            mResultView.setText("error");
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
