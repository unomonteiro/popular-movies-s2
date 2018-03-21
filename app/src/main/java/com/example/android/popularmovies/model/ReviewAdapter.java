package com.example.android.popularmovies.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;

import java.util.List;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> mReviewList;

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, viewGroup, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = mReviewList.get(position);

        holder.authorView.setText(review.getAuthor());
        holder.contentView.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if (mReviewList == null) {
            return 0;
        } else {
            return mReviewList.size();
        }
    }

    public void setReviewList(List<Review> reviewList) {
        mReviewList = reviewList;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        final TextView authorView;
        final TextView contentView;
        ReviewViewHolder(View itemView) {
            super(itemView);
            authorView = itemView.findViewById(R.id.review_author);
            contentView = itemView.findViewById(R.id.review_content);
        }
    }
}
