package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;


public class NetUtils {
    public static final String SERVER_URL = "https://api.themoviedb.org/3/";
    public final static String API_KEY = "api_key"; //this is the api key of themoviedb.org
    public static final String LANGUAGE_PARAM = "language";
    public static final String DISCOVER_PATH = "discover"; //this path is used to fill the list when activity is created
    public static final String SEARCH_PATH = "search"; //this path is used for searching movie or tv show
    public static final String QUERY_PARAM = "query";
    public static final String WITH_GENRE_PARAM = "with_genres"; //this filter movies or tv shows with that genre id
    public static final String SORT_BY_PARAM = "sort_by";
    public static final String PAGE_PARAM = "page"; //this set the page displayed
    public static final String INCLUDE_ADULT_PARAM = "include_adult";
    public static final String INCLUDE_VIDEO_PARAM = "include_video";
    public static final String ORIGINAL_LANGUAGE_PARAM = "with_original_language";
    public static final String REVIEWS_PATH = "reviews";
    public static final String VIDEOS_PATH = "videos";

    private static final String LOG_TAG = NetUtils.class.getSimpleName();
    private static int serverResponse;

    /**
     * Fetch data from server database and return a list of items
     */
    public static List<MoviesData> fetchList(Context context, String stringUrl) {
        // Create URL object
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG + " MalformedURLException", e.getMessage());
        }
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = httpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG + " -> IOException", "fetchList: " + e.getMessage());
        }
        // Extract relevant fields from the JSON response and create a list of items
        List<MoviesData> movies = JsonParser.parsingList(context, jsonResponse);
        return movies;
    }

    /**
     * Fetch data from server database and return a Json String of a single item.
     */
    public static MoviesData fetchData(Context context, String stringUrl) {
        // Create URL object
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG + " MalformedURLException", e.getMessage());
        }
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = httpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG + " -> IOException", "fetchData: " + e.getMessage());
        }
        MoviesData movies = JsonParser.parsingData(context, jsonResponse);
        return movies;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    private static String httpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            serverResponse = urlConnection.getResponseCode();

            // If the request was successful (response code 200), reads the input stream
            if (serverResponse == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = streamReader(inputStream);
            } else if (serverResponse == 404) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = streamReader(inputStream);
            } else {
                Log.e(LOG_TAG + " -> httpRequest", "Server Response: " + urlConnection.getResponseCode() + " " + urlConnection.getResponseMessage());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG + " -> IOException", "httpRequest: " + e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the InputStream into a String which contains the
     * whole JSON response from the server.
     */
    private static String streamReader(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    //determine if connection is active
    public static Boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}

