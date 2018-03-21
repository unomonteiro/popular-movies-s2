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
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.ReviewAdapter;
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

public class DetailActivity extends AppCompatActivity implements
        TrailerAdapter.TrailerClickListener {

    public static final String INTENT_EXTRA_MOVIE = "INTENT_EXTRA_MOVIE";
    private static final int TRAILER_LOADER_ID = 1;
    private static final int REVIEW_LOADER_ID = 2;
    private Movie mMovie;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        RecyclerView trailerReciclerView = findViewById(R.id.trailers_recycler_view);
        mTrailerAdapter = new TrailerAdapter(this);

        trailerReciclerView.setLayoutManager(new LinearLayoutManager(this));
        trailerReciclerView.setAdapter(mTrailerAdapter);

        RecyclerView reviewReciclerView = findViewById(R.id.reviews_recycler_view);
        mReviewAdapter = new ReviewAdapter();
        reviewReciclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewReciclerView.setAdapter(mReviewAdapter);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        } else if (intent.hasExtra(INTENT_EXTRA_MOVIE)) {
            mMovie = intent.getParcelableExtra(INTENT_EXTRA_MOVIE);
            populateUI(mMovie);
            getTrailers();
            getReviews();
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
        getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, null, new TrailerLoader());
    }

    private void getReviews() {
        getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, new ReviewLoader());
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    class TrailerLoader implements LoaderManager.LoaderCallbacks<List<Trailer>> {

        @SuppressLint("StaticFieldLeak")
        @NonNull
        @Override
        public Loader<List<Trailer>> onCreateLoader(int id, @Nullable Bundle args) {
            return new AsyncTaskLoader<List<Trailer>>(DetailActivity.this) {

                private List<Trailer> mData;

                @Override
                protected void onStartLoading() {
                    if (mData != null) {
                        // Use cached data
                        deliverResult(mData);
                    } else {
                        forceLoad();
                    }
                }
                @Nullable
                @Override
                public List<Trailer> loadInBackground() {
                    if (mMovie == null) {
                        return null;
                    }

                    URL trailerRequestUrl = NetworkUtils.buildTrailersUrl(
                            DetailActivity.this,
                            String.valueOf(mMovie.getId()));
                    try {
                        String jsonResponse = NetworkUtils.getJsonResponseFromUrl(trailerRequestUrl);
                        return JsonUtils.parseTrailerResultsJson(jsonResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(@Nullable List<Trailer> data) {
                    mData = data;
                    super.deliverResult(mData);
                }
            };
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<Trailer>> loader, List<Trailer> results) {
            if (results != null) {
                mTrailerAdapter.setTrailerList(results);
            } else {
                mTrailerAdapter.setTrailerList(null);
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<Trailer>> loader) {

        }
    }

    class ReviewLoader implements LoaderManager.LoaderCallbacks<List<Review>> {

        @SuppressLint("StaticFieldLeak")
        @NonNull
        @Override
        public Loader<List<Review>> onCreateLoader(int id, @Nullable Bundle args) {
            return new AsyncTaskLoader<List<Review>>(DetailActivity.this) {

                private List<Review> mData;

                @Override
                protected void onStartLoading() {
                    if (mData != null) {
                        // Use cached data
                        deliverResult(mData);
                    } else {
                        forceLoad();
                    }
                }
                @Nullable
                @Override
                public List<Review> loadInBackground() {
                    if (mMovie == null) {
                        return null;
                    }

                    URL reviewRequestUrl = NetworkUtils.buildReviewsUrl(
                            DetailActivity.this,
                            String.valueOf(mMovie.getId()));
                    try {
                        String jsonResponse = NetworkUtils.getJsonResponseFromUrl(reviewRequestUrl);
                        return JsonUtils.parseReviewResultsJson(jsonResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(@Nullable List<Review> data) {
                    mData = data;
                    super.deliverResult(mData);
                }
            };
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<Review>> loader, List<Review> results) {
            if (results != null) {
                mReviewAdapter.setReviewList(results);
            } else {
                mReviewAdapter.setReviewList(null);
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<Review>> loader) {

        }
    }

    @Override
    public void onTrailerClick(Trailer trailer) {
        startActivity(new Intent(Intent.ACTION_VIEW, getYoutubeUri(trailer.getKey())));
    }


}
