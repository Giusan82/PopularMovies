package com.example.android.popularmovies.utilities;

import android.content.Context;
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

public class ApiRequest {
    public static final String LOG_TAG = ApiRequest.class.getSimpleName();
    public static int serverResponse;

    /**
     * Fetch data from server database and return an object to represent a single item.
     */
    public static List<MoviesList> fetchData(Context context, String stringUrl) {
        // Create URL object
        URL url = null;
        //TODO remove this when finished
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
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
        // Extract relevant fields from the JSON response and create a list of items
        List<MoviesList> movies = JsonParser.parsingData(context, jsonResponse);
        return movies;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String httpRequest(URL url) throws IOException {
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
}
