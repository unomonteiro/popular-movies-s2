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

import static com.example.android.popularmovies.utils.NetworkUtils.getYoutubeThumbnail;


public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private List<Trailer> mTrailerList;
    private final TrailerClickListener mOnClickListener;

    public TrailerAdapter(TrailerClickListener listener) {
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_item, viewGroup, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        Trailer trailer = mTrailerList.get(position);

        Picasso.with(holder.thumbView.getContext())
                .load(getYoutubeThumbnail(trailer.getKey()))
                .into(holder.thumbView);
        holder.nameView.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {
        if (mTrailerList == null) {
            return 0;
        } else {
            return mTrailerList.size();
        }
    }

    public void setTrailerList(List<Trailer> trailerList) {
        mTrailerList = trailerList;
        notifyDataSetChanged();
    }

    public interface TrailerClickListener {
        void onTrailerClick(Trailer trailer);
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        final ImageView thumbView;
        final TextView nameView;
        TrailerViewHolder(View itemView) {
            super(itemView);
            thumbView = itemView.findViewById(R.id.trailer_item_thumb);
            nameView = itemView.findViewById(R.id.trailer_item_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Trailer trailer = mTrailerList.get(getAdapterPosition());
            mOnClickListener.onTrailerClick(trailer);
        }
    }
}
