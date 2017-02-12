package com.mangu.popularmovies.Data;

import android.net.Uri;
import android.provider.BaseColumns;


public class FavoritesContract {

    public static final String AUTHORITY = "com.mangu.popularmovies";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String PATH_FAVORITES = "favorites";

    public static final class FavoritesEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_JSON = "json_movie";
    }
}
