package com.example.android.popularmovies.utilities;


import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PrivateKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JsonParser {
    private static final String LOG_TAG = JsonParser.class.getSimpleName();
    private static final String KEY_PAGE = "page";
    private static final String KEY_TOTAL_RESULT = "total_results";
    private static final String KEY_TOTAL_PAGES = "total_pages";
    private static final String KEY_RESULTS = "results";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_ORIGINAL_LANGUAGE = "original_language";
    private static final String KEY_DESCRIPTION = "overview";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_BACKGROUND_PATH = "backdrop_path";
    private static final String KEY_GENRE_IDS = "genre_ids";
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_POPULARITY = "popularity";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_VIDEO = "video";
    private static final String KEY_ADULT = "adult";

    /**
     * Return a list of {@link MoviesList} objects that has been built up from parsing a JSON response.
     */
    public static List<MoviesList> parsingData(Context context, String jsonResponse) {
        ArrayList<MoviesList> list = new ArrayList<>();


        // Try to parse the JSON Response.
        try {
            //This creates the root JSONObject by calling jsonResponse
            JSONObject base = new JSONObject(jsonResponse);

            int page = base.optInt(KEY_PAGE);
            int total_results = base.optInt(KEY_TOTAL_RESULT);
            int total_pages = base.optInt(KEY_TOTAL_PAGES);

            JSONArray results = base.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < results.length(); i++) {
                int id = results.optJSONObject(i).optInt(KEY_ID);
                String title = results.optJSONObject(i).optString(KEY_TITLE);
                String original_title = results.optJSONObject(i).optString(KEY_ORIGINAL_TITLE);
                String original_language = results.optJSONObject(i).optString(KEY_ORIGINAL_LANGUAGE);
                String description = results.optJSONObject(i).optString(KEY_DESCRIPTION);
                String poster = results.optJSONObject(i).optString(KEY_POSTER_PATH);
                String background = results.optJSONObject(i).optString(KEY_BACKGROUND_PATH);
                JSONArray genre_Ids = results.optJSONObject(i).optJSONArray(KEY_GENRE_IDS);
                ArrayList<String> genreList = new ArrayList<>();
                for (int j = 0; j < genre_Ids.length(); j++){
                    genreList.add(String.valueOf(genre_Ids.optInt(j)));
                }

                int vote_count = results.optJSONObject(i).optInt(KEY_VOTE_COUNT);
                double vote_average = results.optJSONObject(i).optDouble(KEY_VOTE_AVERAGE);
                double popularity = results.optJSONObject(i).optDouble(KEY_POPULARITY);
                String release_date = results.optJSONObject(i).optString(KEY_RELEASE_DATE);
                boolean video = results.optJSONObject(i).optBoolean(KEY_VIDEO);
                boolean adult = results.optJSONObject(i).optBoolean(KEY_ADULT);
                MoviesList movies = new MoviesList(
                        page,
                        total_pages,
                        total_results,
                        id,
                        title,
                        original_title,
                        original_language,
                        description,
                        poster,
                        background,
                        genreList,
                        vote_count,
                        vote_average,
                        popularity,
                        release_date,
                        video,
                        adult);
                list.add(movies);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG + " -> JSONException", "Problem with parsing JSON");
        }
        return list;
    }

    //for getting the date conversion in milliseconds
    public static long formatDate(String date) {
        long milliseconds = 0;
        if (date.length() >= 10) {
            // Splits the string after 10 char, because the date obtained from server is like this "2017-07-15T21:30:35Z", so this method will give 2017-07-15
            CharSequence splittedDate = date.subSequence(0, 10);
            try {
                Date formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(splittedDate.toString());
                milliseconds = formatDate.getTime();
            } catch (ParseException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
        return milliseconds;
    }
}
