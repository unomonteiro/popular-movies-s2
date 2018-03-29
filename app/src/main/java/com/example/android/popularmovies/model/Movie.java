package com.example.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private final int mId;
    private final String mTitle;
    private final String mOriginalTitle;
    private final String mPosterPath;
    private final String mOverview;
    private final double mVoteAverage;
    private final String mReleaseDate;
    private boolean mFavorite;

    public Movie(int id, String title, String originalTitle, String posterPath, String overview,
                 double voteAverage, String releaseDate, boolean favorite) {
        mId = id;
        mTitle = title;
        mOriginalTitle = originalTitle;
        mPosterPath = posterPath;
        mOverview = overview;
        mVoteAverage = voteAverage;
        mReleaseDate = releaseDate;
        mFavorite = favorite;
    }

    protected Movie(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mOriginalTitle = in.readString();
        mPosterPath = in.readString();
        mOverview = in.readString();
        mVoteAverage = in.readDouble();
        mReleaseDate = in.readString();
        mFavorite = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mOriginalTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mOverview);
        dest.writeDouble(mVoteAverage);
        dest.writeString(mReleaseDate);
        dest.writeByte((byte) (mFavorite ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "mId=" + mId +
                ", mTitle='" + mTitle + '\'' +
                ", mOriginalTitle='" + mOriginalTitle + '\'' +
                ", mPosterPath='" + mPosterPath + '\'' +
                ", mOverview='" + mOverview + '\'' +
                ", mVoteAverage=" + mVoteAverage +
                ", mReleaseDate='" + mReleaseDate + '\'' +
                ", mFavorite=" + mFavorite +
                '}';
    }
}
