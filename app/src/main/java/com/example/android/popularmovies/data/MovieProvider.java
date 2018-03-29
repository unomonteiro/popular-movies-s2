package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieProvider extends ContentProvider {

    /** movie directory */
    public static final int CODE_MOVIE = 100;
    /** movie directory.items */
    public static final int CODE_MOVIE_WITH_ID = 101;

    /**
     * https://www.buzzingandroid.com/2013/01/sqlite-insert-or-replace-through-contentprovider/
     * */
    public static final String SQL_INSERT_OR_REPLACE = "__sql_insert_or_replace__";

    private static final UriMatcher sUriMatcher = builUriMatcher();
    private MovieDbHelper mMovieDbHelper;

    public static UriMatcher builUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String autority = MovieContract.AUTHORITY;

        matcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES, CODE_MOVIE);
        matcher.addURI(autority, MovieContract.PATH_MOVIES + "/#", CODE_MOVIE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE: {
                cursor = db.query(MovieContract.MovieEntry.TABLE_MOVIES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_MOVIE_WITH_ID: {
                cursor = db.query(MovieContract.MovieEntry.TABLE_MOVIES,
                        projection,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public
    Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE: {
                boolean replace = false;

                if (values.containsKey(SQL_INSERT_OR_REPLACE)) {
                    replace = values.getAsBoolean( SQL_INSERT_OR_REPLACE );

                    // Clone the values object, so we don't modify the original.
                    // This is not strictly necessary, but depends on your needs
                    values = new ContentValues( values );

                    // Remove the key, so we don't pass that on to db.insert() or db.replace()
                    values.remove( SQL_INSERT_OR_REPLACE );
                }

                long rowId;
                if ( replace ) {
                    rowId = db.replace(MovieContract.MovieEntry.TABLE_MOVIES, null, values);
                } else {
                    rowId = db.insert(MovieContract.MovieEntry.TABLE_MOVIES, null, values);
                }

                if (rowId > 0 ) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, rowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int movieDeleted;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE_WITH_ID: {
                movieDeleted = db.delete(MovieContract.MovieEntry.TABLE_MOVIES,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return movieDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
