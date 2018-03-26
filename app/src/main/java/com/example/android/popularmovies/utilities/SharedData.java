package com.example.android.popularmovies.utilities;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

public final class SharedData {
    private static final String SEARCH_TYPE_KEY = "search_type";

    public static void setSearchType(Context context, int position) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(SEARCH_TYPE_KEY, position);
        editor.apply();
    }

    public static int getSearchType(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        int position = sharedPrefs.getInt(SEARCH_TYPE_KEY, 0);
        return position;
    }
}
