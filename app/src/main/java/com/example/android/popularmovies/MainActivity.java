package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utils.JsonUtils;
import com.example.android.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String>, MovieAdapter.ItemClickListener {

    private MovieAdapter mMovieAdapter;

    private static final int MOVIE_LOADER_ID = 0;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.movies_recycler_view);
        mMovieAdapter = new MovieAdapter(this);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setAdapter(mMovieAdapter);

        View view = this.findViewById(android.R.id.content);
        view.post(() -> setColumns(view, view.getContext()));

        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    }

    private void setColumns(View view, Context context) {
        int columnCount = getViewColumnCount(view,R.dimen.movie_width);
        columnCount = Math.max(2, columnCount);
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
        mRecyclerView.setAdapter(mMovieAdapter);
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
            mMovieAdapter.setMovieList(results);
        } else {
            mMovieAdapter.setMovieList(null);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    public void onItemClick(Movie movie) {
        Toast.makeText(this, movie.getTitle(), Toast.LENGTH_SHORT).show();
    }

    int getViewColumnCount(View view, int preferredWidthResource) {
        int containerWidth = view.getWidth();
        int preferredWidth = view.getContext().getResources().
                getDimensionPixelSize(preferredWidthResource);
        return containerWidth / preferredWidth;
    }
}
