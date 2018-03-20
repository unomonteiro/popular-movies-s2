package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Trailer;
import com.example.android.popularmovies.model.TrailerAdapter;
import com.example.android.popularmovies.utils.JsonUtils;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.net.URL;
import java.util.List;

import static com.example.android.popularmovies.utils.NetworkUtils.getImageUrl;
import static com.example.android.popularmovies.utils.NetworkUtils.getYoutubeUri;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>,TrailerAdapter.TrailerClickListener {

    public static final String INTENT_EXTRA_MOVIE = "INTENT_EXTRA_MOVIE";
    private static final int TRAILER_LOADER_ID = 1;
    private Movie mMovie;
    private RecyclerView mTrailerReciclerView;
    private TrailerAdapter mTrailerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTrailerReciclerView = findViewById(R.id.trailers_recycler_view);
        mTrailerAdapter = new TrailerAdapter(this);

        mTrailerReciclerView.setLayoutManager(new LinearLayoutManager(this));
        mTrailerReciclerView.setAdapter(mTrailerAdapter);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        } else if (intent.hasExtra(INTENT_EXTRA_MOVIE)) {
            mMovie = intent.getParcelableExtra(INTENT_EXTRA_MOVIE);
            populateUI(mMovie);
            getTrailers();
        }
    }

    private void populateUI(Movie movie) {
        String posterUrl = movie.getPosterPath();
        if (posterUrl != null && posterUrl.trim().length() > 0) {
            ImageView detailPosterView = findViewById(R.id.detail_poster);
            Picasso.with(this)
                    .load(getImageUrl(posterUrl))
                    .into(detailPosterView);
        }
        TextView detailTitleView = findViewById(R.id.detail_title);
        detailTitleView.setText(movie.getOriginalTitle());
        TextView synopsisView = findViewById(R.id.detail_synopsis);
        synopsisView.setText(movie.getOverview());
        TextView ratingView = findViewById(R.id.detail_rating);
        ratingView.setText(String.format(getString(R.string.rating_in_10), String.valueOf(movie.getVoteAverage())));
        TextView releaseDateView = findViewById(R.id.detail_release_date);
        releaseDateView.setText(movie.getReleaseDate());
    }

    private void getTrailers() {
        getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, null, this);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            private String mData;

            @Override
            protected void onStartLoading() {
                if (mData != null) {
                    // Use cached data
                    deliverResult(mData);
                } else {
                    // todo showLoading();
                    forceLoad();
                }
            }
            @Nullable
            @Override
            public String loadInBackground() {
                if (mMovie == null) {
                    return null;
                }

                URL trailerRequestUrl = NetworkUtils.buildTrailersUrl(
                        DetailActivity.this,
                        String.valueOf(mMovie.getId()));
                try {
                    return NetworkUtils.getJsonResponseFromUrl(trailerRequestUrl);
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
            List<Trailer> results = null;
            try {
                results = JsonUtils.parseTrailerResultsJson(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // todo showMovies();
            mTrailerAdapter.setTrailerList(results);
        } else {
            // todo showError();
            mTrailerAdapter.setTrailerList(null);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    @Override
    public void onTrailerClick(Trailer trailer) {
        startActivity(new Intent(Intent.ACTION_VIEW, getYoutubeUri(trailer.getKey())));
    }


}
