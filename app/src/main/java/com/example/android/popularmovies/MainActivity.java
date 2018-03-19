package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieAdapter;
import com.example.android.popularmovies.utils.JsonUtils;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.example.android.popularmovies.utils.SettingsActivity;

import org.json.JSONException;

import java.net.URL;
import java.util.List;

import static com.example.android.popularmovies.DetailActivity.INTENT_EXTRA_MOVIE;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if (id == R.id.action_about) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setColumns(View view, Context context) {
        int columnCount = getViewColumnCount(view,R.dimen.movie_width);
        columnCount = Math.max(2, columnCount);
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    @NonNull
    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            private String mData;

            @Override
            protected void onStartLoading() {
                if (mData != null) {
                    // Use cached data
                    deliverResult(mData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                String orderBy = sharedPreferences.getString(
                        getString(R.string.pref_order_by_key),
                        getString(R.string.pref_order_by_popular_value));

                URL moviesRequestUrl = NetworkUtils.buildUrl(MainActivity.this, orderBy);
                try {
                    return NetworkUtils.getJsonResponseFromUrl(moviesRequestUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(@Nullable String data) {
                mData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
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
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    @Override
    public void onItemClick(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(INTENT_EXTRA_MOVIE, movie);
        startActivity(intent);
    }

    private int getViewColumnCount(View view, int preferredWidthResource) {
        int containerWidth = view.getWidth();
        int preferredWidth = view.getContext().getResources().
                getDimensionPixelSize(preferredWidthResource);
        return containerWidth / preferredWidth;
    }
}
