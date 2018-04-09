package com.example.android.popularmovies.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.android.popularmovies.utils.NetworkUtils.getImageUrl;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> mMovieList;
    private final ItemClickListener mOnClickListener;

    public MovieAdapter(ItemClickListener listener) {
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.movie_item, viewGroup, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = mMovieList.get(position);
        String title = movie.getOriginalTitle();
        holder.titleView.setText(title);

        Picasso.with(holder.itemView.getContext())
                .load(getImageUrl(movie.getPosterPath()))
                .into(holder.posterView);
    }

    @Override
    public int getItemCount() {
        if (mMovieList == null) {
            return 0;
        } else {
            return mMovieList.size();
        }
    }

    public void setMovieList(List<Movie> movieList) {
        mMovieList = movieList;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClick(Movie movie);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        final ImageView posterView;
        final TextView titleView;
        MovieViewHolder(View itemView) {
            super(itemView);
            posterView = itemView.findViewById(R.id.movie_item_poster);
            titleView = itemView.findViewById(R.id.movie_item_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Movie movie = mMovieList.get(getAdapterPosition());
            mOnClickListener.onItemClick(movie);
        }
    }
}
