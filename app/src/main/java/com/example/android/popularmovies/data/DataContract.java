package com.example.android.popularmovies.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class DataContract {

    //To prevent someone from accidentally instantiating the contract class
    private DataContract() {
    }

    //This is the name of the entire content provider expressed as package name
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";
    //Base URI used to contact the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //Path appended to base content URI referred to the favorites table
    public static final String PATH_FAVORITES = "favorites";

    public static final class DataEntry implements BaseColumns {
        //The content URI to access the item data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FAVORITES);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_DATA_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        //Name of database table
        public static final String TABLE_NAME = "favorites";
        //Unique ID number for the item (only for use in the database table). Type: INTEGER
        public static final String ID = BaseColumns._ID;
        // Name of the movie/tv show. Type: TEXT
        public static final String COLUMN_NAME = "name";
        //item's date. Type: REAL
        public static final String COLUMN_CREATION_DATE = "creation_date";
        //movie id. Type: INTEGER
        public static final String COLUMN_MOVIE_ID = "movie_id";
        //Type: INTEGER, 0 FOR MOVIE AND 1 FOR TV SHOW
        public static final String COLUMN_TYPE = "type";
        //Type: TEXT
        public static final String COLUMN_POSTER_PATH = "poster_path";
    }
}
