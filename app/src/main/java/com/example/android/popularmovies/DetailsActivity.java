package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.utilities.DataLoader;
import com.example.android.popularmovies.utilities.JsonParser;
import com.example.android.popularmovies.utilities.MoviesList;
import com.example.android.popularmovies.utilities.SharedData;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<MoviesList>> {
    private static final String LOG_TAG = DetailsActivity.class.getSimpleName();
    public static final String EXTRA_MOVIE = "extra_movie";
    public static final String EXTRA_MOVIE_ID = "extra_movie_id";
    public static final String EXTRA_MOVIE_TITLE = "extra_movie_title";
    public static final String EXTRA_MEDIA_TYPE = "extra_media_type";
    public static final int DEFAULT_ID = 0;
    private static final String IMAGE_BASE_HEADER = "http://image.tmdb.org/t/p/original/";
    private static final String IMAGE_BASE_POSTER = "http://image.tmdb.org/t/p/w342/";
    private static final String NULL_VALUE = "null";


    private static final String POPULARITY_FORMAT = "%.1f";

    private static final int LOADER_ID = 1;
    private static final String SERVER_URL = "https://api.themoviedb.org/3/";
    private final static String API_KEY = "api_key"; //this is the api key of themoviedb.org
    private static final String MOVIE_PATH = "movie";

    private int mId;
    private String mTitle;
    private LoaderManager loaderManager;
    private ArrayList<MoviesList> mItems;
    private String[] searchValue;
    private ProgressBar mLoading_header;
    private ProgressBar mLoading_activity;

    private ConstraintLayout cl_content;
    private TextView mOriginalTitle;
    private TextView mDescription;
    private TextView mRelease_Date;
    private TextView mGenres;
    private TextView mRatingValue;
    private TextView mPopularity;
    private TextView mCompanies;
    private TextView mCountries;
    private TextView mRuntime;
    private TextView mLanguages;
    private TextView mStatus;
    private ImageView mHeader;
    private ImageView mPoster;
    private RatingBar mRatingBar;
    private ImageView mEmpty_Image;
    private TextView mEmpty_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        cl_content = findViewById(R.id.cl_content);
        mOriginalTitle = findViewById(R.id.tv_original_title);
        mDescription = findViewById(R.id.tv_description);
        mRelease_Date = findViewById(R.id.tv_release_date);
        mRatingValue = findViewById(R.id.tv_rating);
        mPopularity = findViewById(R.id.tv_popularity);
        mCompanies = findViewById(R.id.tv_production_companies);
        mCountries = findViewById(R.id.tv_production_coutries);
        mRuntime = findViewById(R.id.tv_runtime);
        mLanguages = findViewById(R.id.tv_spoken_languages);
        mStatus = findViewById(R.id.tv_status);

        mGenres = findViewById(R.id.tv_genres);
        mHeader = findViewById(R.id.iv_header);
        mPoster = findViewById(R.id.iv_poster);

        mRatingBar = findViewById(R.id.ratingBar);

        mEmpty_Image = findViewById(R.id.iv_empty);
        mEmpty_message = findViewById(R.id.tv_empty);

        searchValue = getResources().getStringArray(R.array.search_type_value);

        mLoading_header = findViewById(R.id.loading_header);
        mLoading_activity = findViewById(R.id.loading_activity);

        Intent intent = getIntent();
        if(intent.hasExtra(EXTRA_MOVIE_ID)){
            //this receive a custom object, source: https://stackoverflow.com/a/7827593
            //mMovie = (MoviesList)intent.getSerializableExtra(EXTRA_MOVIE);
            mId = intent.getIntExtra(EXTRA_MOVIE_ID, DEFAULT_ID);
            Log.e("Detail", "Intent ID: " + mId);
        }
        if(intent.hasExtra(EXTRA_MOVIE_TITLE)){
            mTitle = intent.getStringExtra(EXTRA_MOVIE_TITLE);
            setTitle(mTitle);
        }
        // Get a reference to the LoaderManager, in order to interact with loaders.
        loaderManager = getLoaderManager();
        if (isConnected()) {
            loaderManager.initLoader(LOADER_ID, null, this);
        }else{
            String title = getString(R.string.no_internet_title);
            String message = getString(R.string.no_internet);
            alertDialogMessage(title, message);
        }
        //the ArrayList is initialized
        mItems = new ArrayList<>();
    }

    @Override
    public Loader<List<MoviesList>> onCreateLoader(int i, Bundle bundle) {
        return new DataLoader(this, builderUrl(mId).toString());
    }

    @Override
    public void onLoadFinished(Loader<List<MoviesList>> loader, List<MoviesList> data) {
        if (isConnected()) {
            clear();
            if (data != null && !data.isEmpty()) {
                //if not, add all items into the ArrayList
                mItems.addAll(data);
                String header = mItems.get(0).getBackground_Path();
                if (!header.equals(NULL_VALUE)){
                    Glide.with(this).load(IMAGE_BASE_HEADER + header).crossFade().dontTransform().into(mHeader);
                }else{
                    mHeader.setVisibility(View.GONE);
                    mLoading_header.setVisibility(View.GONE);
                }
                String poster = mItems.get(0).getPoster_Path();
                if (!poster.equals(NULL_VALUE)){
                    Glide.with(this).load(IMAGE_BASE_POSTER + poster).crossFade().dontTransform().into(mPoster);
                }else{
                    mPoster.setImageResource(R.drawable.placeholder_poster);
                }
                mOriginalTitle.setText(Html.fromHtml(getString(R.string.original_title, mItems.get(0).getOriginal_title())));
                mPopularity.setText(String.format(POPULARITY_FORMAT, mItems.get(0).getPopularity()));
                mDescription.setVisibility(View.VISIBLE);
                if(!mItems.get(0).getDescription().isEmpty()){
                    mDescription.setText(Html.fromHtml(getString(R.string.description, mItems.get(0).getDescription())));
                }else{
                    mDescription.setText(Html.fromHtml(getString(R.string.description, getString(R.string.unknown))));
                }

                mRelease_Date.setText(mItems.get(0).getRelease_date());
                mRatingValue.setText(String.valueOf(mItems.get(0).getVote_Average()));
                mRatingBar.setRating((float)mItems.get(0).getVote_Average()*5/10);
                mGenres.setText(Html.fromHtml(getString(R.string.genres, mItems.get(0).getGenres())));
                mCompanies.setText(Html.fromHtml(getString(R.string.production_companies, mItems.get(0).getProductionCompanies())));
                mCountries.setText(Html.fromHtml(getString(R.string.production_countries, mItems.get(0).getProductionCountries())));
                String mediaSelection = searchValue[SharedData.getSearchType(this)];
                if(mediaSelection.equals(getString(R.string.search_movies_value))){
                    mRuntime.setText(Html.fromHtml(getString(R.string.runtime, mItems.get(0).getRuntime())));
                }else{
                    mRuntime.setText(Html.fromHtml(getString(R.string.seasons_and_episodes, mItems.get(0).getNumberOfSeasons(), mItems.get(0).getNumberOfEpisodes())));
                }

                mLanguages.setText(Html.fromHtml(getString(R.string.spoken_languages, mItems.get(0).getSpokenLanguages())));
                mStatus.setText(Html.fromHtml(getString(R.string.status, mItems.get(0).getStatus())));
                mLoading_activity.setVisibility(View.GONE);
                cl_content.setVisibility(View.VISIBLE);
                Log.e(LOG_TAG, "Status Message: " + mItems.get(0).getStatus_Message());
            }else{
                mLoading_activity.setVisibility(View.GONE);
                mEmpty_Image.setVisibility(View.VISIBLE);
                mEmpty_message.setVisibility(View.VISIBLE);
            }
        }else{
            mLoading_activity.setVisibility(View.GONE);
            String title = getString(R.string.no_internet_title);
            String message = getString(R.string.no_internet);
            alertDialogMessage(title, message);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<MoviesList>> loader) {
        clear();
    }

    private void clear() {
        mItems.clear();
    }

    //this builds the url
    private URL builderUrl(int id) {

        String type = searchValue[SharedData.getSearchType(this)];
        Uri.Builder builtUri;
        builtUri = Uri.parse(SERVER_URL).buildUpon();
        builtUri.appendPath(type)
                .appendPath(String.valueOf(id))
                .appendQueryParameter(API_KEY, getString(R.string.api_key))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.e("DetailUrl", url.toString());
        return url;
    }

    //restart the loader
    private void refresh() {
        loaderManager.restartLoader(LOADER_ID, null, this);
    }

    //build an alert dialog message for no internet connection
    public void alertDialogMessage(String title, String message) {
        int style = R.style.alertDialog;
        int icon = R.drawable.ic_portable_wifi_off;

        AlertDialog.Builder builder = new AlertDialog.Builder(this, style);
        builder.setTitle(title);
        builder.setIcon(icon);
        builder.setMessage(message);
        builder.setNegativeButton(getString(R.string.close_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setPositiveButton(getString(R.string.refresh_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                refresh();
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //determine if connection is active
    private Boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
