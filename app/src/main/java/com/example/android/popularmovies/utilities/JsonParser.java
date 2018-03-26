package com.example.android.popularmovies.utilities;


import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonParser {
    private static final String LOG_TAG = JsonParser.class.getSimpleName();
    private static final String KEY_PAGE = "page";
    private static final String KEY_TOTAL_RESULT = "total_results";
    private static final String KEY_TOTAL_PAGES = "total_pages";
    private static final String KEY_RESULTS = "results";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_TV_NAME = "name";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_ORIGINAL_TV_NAME = "original_name";
    private static final String KEY_ORIGINAL_LANGUAGE = "original_language";
    private static final String KEY_DESCRIPTION = "overview";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_BACKGROUND_PATH = "backdrop_path";
    private static final String KEY_GENRES = "genres";
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_POPULARITY = "popularity";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_FIRST_AIR_DATE = "first_air_date";
    private static final String KEY_VIDEO = "video";
    private static final String KEY_ADULT = "adult";
    private static final String KEY_GENRE_NAME = "name";
    private static final String KEY_PRODUCTION_COMPANIES = "production_companies";
    private static final String KEY_PRODUCTION_COMPANIES_NAME = "name";
    private static final String KEY_PRODUCTION_COUNTRIES = "production_countries";
    private static final String KEY_PRODUCTION_COUNTRIES_NAME = "name";
    private static final String KEY_ORIGIN_COUNTRY = "origin_country";
    private static final String KEY_RUNTIME = "runtime";
    private static final String KEY_SPOKEN_LANGUAGES = "spoken_languages";
    private static final String KEY_SPOKEN_LANGUAGES_NAME = "name";
    private static final String KEY_LANGUAGES = "languages";
    private static final String KEY_STATUS = "status";
    private static final String KEY_SEASONS = "number_of_seasons";
    private static final String KEY_EPISODES = "number_of_episodes";

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
            if (base.has(KEY_RESULTS)) {
                JSONArray results = base.optJSONArray(KEY_RESULTS);
                for (int i = 0; i < results.length(); i++) {
                    if (results.optJSONObject(i) != null) {
                        int id = results.optJSONObject(i).optInt(KEY_ID);
                        String title;
                        if (results.optJSONObject(i).has(KEY_TITLE)) {
                            title = results.optJSONObject(i).optString(KEY_TITLE);
                        } else {
                            title = results.optJSONObject(i).optString(KEY_TV_NAME);
                        }
                        String poster = results.optJSONObject(i).optString(KEY_POSTER_PATH);
                        double vote_average = results.optJSONObject(i).optDouble(KEY_VOTE_AVERAGE);
                        final MoviesList movies = new MoviesList(
                                context,
                                page,
                                total_pages,
                                total_results,
                                id,
                                title,
                                poster,
                                vote_average);
                        list.add(movies);
                    }
                }
            } else {
                int id = base.optInt(KEY_ID);
                String title;
                if (base.has(KEY_TITLE)) {
                    title = base.optString(KEY_TITLE);
                } else {
                    title = base.optString(KEY_TV_NAME);
                }
                String original_title;
                if (base.has(KEY_ORIGINAL_TITLE)) {
                    original_title = base.optString(KEY_ORIGINAL_TITLE);
                } else {
                    original_title = base.optString(KEY_ORIGINAL_TV_NAME);
                }
                String original_language = base.optString(KEY_ORIGINAL_LANGUAGE);
                String description = base.optString(KEY_DESCRIPTION);
                String poster = base.optString(KEY_POSTER_PATH);
                String background = base.optString(KEY_BACKGROUND_PATH);
                int vote_count = base.optInt(KEY_VOTE_COUNT);
                double vote_average = base.optDouble(KEY_VOTE_AVERAGE);
                double popularity = base.optDouble(KEY_POPULARITY);
                String release_date = "";
                if (base.has(KEY_RELEASE_DATE)) {
                    release_date = base.optString(KEY_RELEASE_DATE);
                } else if (base.has(KEY_FIRST_AIR_DATE)) {
                    release_date = base.optString(KEY_FIRST_AIR_DATE);
                }
                boolean video = base.optBoolean(KEY_VIDEO);
                boolean adult = base.optBoolean(KEY_ADULT);
                ArrayList<String> genreList = new ArrayList<>();
                if (base.has(KEY_GENRES)) {
                    JSONArray genres = base.optJSONArray(KEY_GENRES);

                    for (int j = 0; j < genres.length(); j++) {
                        genreList.add(genres.optJSONObject(j).optString(KEY_GENRE_NAME));
                    }
                }
                ArrayList<String> companiesList = new ArrayList<>();
                if (base.has(KEY_PRODUCTION_COMPANIES)) {
                    JSONArray companies = base.optJSONArray(KEY_PRODUCTION_COMPANIES);

                    for (int j = 0; j < companies.length(); j++) {
                        companiesList.add(companies.optJSONObject(j).optString(KEY_PRODUCTION_COMPANIES_NAME));
                    }
                }
                ArrayList<String> coutriesList = new ArrayList<>();
                if (base.has(KEY_PRODUCTION_COUNTRIES)) {
                    JSONArray countries = base.optJSONArray(KEY_PRODUCTION_COUNTRIES);
                    for (int j = 0; j < countries.length(); j++) {
                        coutriesList.add(countries.optJSONObject(j).optString(KEY_PRODUCTION_COUNTRIES_NAME));
                    }
                }
                if (base.has(KEY_ORIGIN_COUNTRY)) {
                    JSONArray countries = base.optJSONArray(KEY_ORIGIN_COUNTRY);
                    for (int j = 0; j < countries.length(); j++) {
                        coutriesList.add(countries.optString(j));
                    }
                }
                int runtime = base.optInt(KEY_RUNTIME);
                ArrayList<String> languagesList = new ArrayList<>();
                if (base.has(KEY_SPOKEN_LANGUAGES)) {
                    JSONArray languages = base.optJSONArray(KEY_SPOKEN_LANGUAGES);
                    for (int j = 0; j < languages.length(); j++) {
                        languagesList.add(languages.optJSONObject(j).optString(KEY_SPOKEN_LANGUAGES_NAME));
                    }
                }
                if (base.has(KEY_LANGUAGES)) {
                    JSONArray languages = base.optJSONArray(KEY_LANGUAGES);
                    for (int j = 0; j < languages.length(); j++) {
                        languagesList.add(languages.optString(j));
                    }
                }
                String status = base.optString(KEY_STATUS);
                int seasons = base.optInt(KEY_SEASONS);
                int episodes = base.optInt(KEY_EPISODES);
                final MoviesList movies = new MoviesList(
                        context,
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
                        adult,
                        companiesList,
                        coutriesList,
                        runtime,
                        languagesList,
                        status,
                        seasons,
                        episodes);
                list.add(movies);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG + " -> JSONException", "Problem with parsing JSON, message: " + e);
        }
        return list;
    }
}
