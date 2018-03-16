package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import static com.example.android.popularmovies.utils.Constants.INTENT_EXTRA_MOVIE;
import static com.example.android.popularmovies.utils.NetworkUtils.getImageUrl;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        if (intent.hasExtra(INTENT_EXTRA_MOVIE)) {
            Movie movie = intent.getParcelableExtra(INTENT_EXTRA_MOVIE);
            ImageView detailPosterView = findViewById(R.id.detail_poster);
            Picasso.with(this)
                    .load(getImageUrl(movie.getPosterPath()))
                    .into(detailPosterView);
            TextView detailTitleView = findViewById(R.id.detail_title);
            detailTitleView.setText(movie.getOriginalTitle());
            TextView synopsisView = findViewById(R.id.detail_synopsis);
            synopsisView.setText(movie.getOverview());
            TextView ratingView = findViewById(R.id.detail_rating);
            ratingView.setText(String.valueOf(movie.getVoteAverage()));
            TextView releaseDateView = findViewById(R.id.detail_release_date);
            releaseDateView.setText(movie.getReleaseDate());
        }

    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }
}
