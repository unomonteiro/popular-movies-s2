package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieAdapter;
import com.example.android.popularmovies.utils.JsonUtils;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.example.android.popularmovies.utils.SettingsActivity;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.DetailActivity.INTENT_EXTRA_MOVIE;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.ItemClickListener {

    private static final int MOVIE_LOADER_ID = 0;
    private static final int FAVORITES_LOADER_ID = 1;

    private MovieAdapter mMovieAdapter;
    private RecyclerView mRecyclerView;
    private View mErrorView;
    private View mLoadingView;

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
        mErrorView = findViewById(R.id.error_message_view);
        mLoadingView = findViewById(R.id.loading_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPreferences.getString(
                getString(R.string.pref_order_by_key),
                getString(R.string.pref_order_by_popular_value));

        if (orderBy.equals(getString(R.string.pref_order_by_favorites_value))) {
            getSupportLoaderManager().destroyLoader(MOVIE_LOADER_ID);
            getSupportLoaderManager().initLoader(FAVORITES_LOADER_ID, null, new FavoriteLoader());
        } else {
            getSupportLoaderManager().destroyLoader(FAVORITES_LOADER_ID);
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, new MovieLoader());
        }
    }

    private void showMovies() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
    }

    private void showError() {
        mRecyclerView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);
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

    private class MovieLoader implements LoaderManager.LoaderCallbacks<String> {
        @NonNull
        @SuppressLint("StaticFieldLeak")
        @Override
        public Loader<String> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<String>(MainActivity.this) {

                private String mData;

                @Override
                protected void onStartLoading() {
                    if (mData != null) {
                        // Use cached data
                        deliverResult(mData);
                    } else {
                        showLoading();
                        forceLoad();
                    }
                }

                @Override
                public String loadInBackground() {

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    String orderBy = sharedPreferences.getString(
                            getString(R.string.pref_order_by_key),
                            getString(R.string.pref_order_by_popular_value));

                    URL moviesRequestUrl = NetworkUtils.buildUrl(orderBy);
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
                showMovies();
                mMovieAdapter.setMovieList(results);
            } else {
                showError();
                mMovieAdapter.setMovieList(null);
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<String> loader) {

        }
    }

    class FavoriteLoader implements LoaderManager.LoaderCallbacks<Cursor> {

        private static final String TAG = "FavoriteLoader";

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            switch (id) {

                case FAVORITES_LOADER_ID:
                    return new CursorLoader(MainActivity.this,
                            MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                default:
                    throw new RuntimeException("Loader Not Implemented: " + id);
            }
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            List<Movie> results = null;
            if (data != null && data.moveToFirst()) {
                results = getMovieListFromCursor(data);
                showMovies();
                mMovieAdapter.setMovieList(results);
            } else {
                showError();
                mMovieAdapter.setMovieList(null);
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            Log.d(TAG, "clm+ onLoaderReset() ");
        }

        @NonNull
        private List<Movie> getMovieListFromCursor(Cursor data) {
            List<Movie> results = new ArrayList<>();
            int idIndex = data.getColumnIndex(MovieContract.MovieEntry._ID);
            int titleIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
            int originalTitleIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
            int posterPathIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
            int overviewIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
            int voteAverageIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
            int releaseDateIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);

            do {
                results.add(new Movie(
                        data.getInt(idIndex),
                        data.getString(titleIndex),
                        data.getString(originalTitleIndex),
                        data.getString(posterPathIndex),
                        data.getString(overviewIndex),
                        data.getDouble(voteAverageIndex),
                        data.getString(releaseDateIndex),
                        true));
            } while (data.moveToNext());
            return results;
        }
    }
}
