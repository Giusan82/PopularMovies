package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.data.DataContract.DataEntry;
import com.example.android.popularmovies.utilities.DataLoader;
import com.example.android.popularmovies.utilities.DialogManager;
import com.example.android.popularmovies.utilities.NetUtils;
import com.example.android.popularmovies.utilities.MoviesData;
import com.example.android.popularmovies.utilities.SharedData;

import java.net.MalformedURLException;
import java.net.URL;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks, DialogManager.AlertDialogAction {

    private static final String IMAGE_BASE_HEADER = "http://image.tmdb.org/t/p/original/";
    private static final String IMAGE_BASE_POSTER = "http://image.tmdb.org/t/p/w342/";
    private static final String NULL_VALUE = "null";
    private static final String POPULARITY_FORMAT = "%.1f";//this display the popularity value with only one decimal
    private static final int LOADER_ID = 2; //loader id of this activity
    private static final int DATA_LOADER_ID = 3; //loader id of this activity
    private static final int DIALOG_ID = 0;

    public static int sId;
    public static String sTitle;
    private Bundle bundle;
    private String mPosterPath;
    private LoaderManager loaderManager;
    private MoviesData movie;
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
    private SharedPreferences sharedPrefs;
    private LinearLayout mFavorites;
    private TextView mTV_favorites;
    private ImageView mIV_favorites;
    private LinearLayout mMoreDetails;
    private Boolean isFavorite = false;
    public static int sDataType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //Show the back arrow
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        //Find the id for the respective views
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
        mLoading_header = findViewById(R.id.loading_header);
        mLoading_activity = findViewById(R.id.loading_activity);
        mFavorites = findViewById(R.id.lv_favorites);
        mTV_favorites = findViewById(R.id.tv_favorites);
        mIV_favorites = findViewById(R.id.iv_favorites);
        mMoreDetails = findViewById(R.id.lv_more_details);
        //Listener
        mFavorites.setOnClickListener(favorites_action);
        mMoreDetails.setOnClickListener(moreDetails_action);
        //this get the array from resources
        searchValue = getResources().getStringArray(R.array.search_type_value);

        bundle = new Bundle();
        Intent intent = getIntent();
        if (intent.hasExtra(SharedData.EXTRA_MOVIE_ID)) {
            sId = intent.getIntExtra(SharedData.EXTRA_MOVIE_ID, SharedData.DEFAULT_ID);
        }
        if (intent.hasExtra(SharedData.EXTRA_MOVIE_TITLE)) {
            sTitle = intent.getStringExtra(SharedData.EXTRA_MOVIE_TITLE);
            setTitle(sTitle);
        }
        if (intent.hasExtra(SharedData.EXTRA_MOVIE_TYPE)) {
            sDataType = intent.getIntExtra(SharedData.EXTRA_MOVIE_TYPE, SharedData.DEFAULT_TYPE);
        } else {
            sDataType = SharedData.getSearchType(this);
        }
        // Get a reference to the LoaderManager, in order to interact with loaders.
        loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(DATA_LOADER_ID, null, this);
        if (NetUtils.isConnected(this)) {
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            int icon = R.drawable.ic_portable_wifi_off;
            String title = getString(R.string.no_internet_title);
            String message = getString(R.string.no_internet);
            DialogManager dialogManager = new DialogManager(this, DIALOG_ID, this);
            dialogManager.showMessage(icon, title, message);
        }
    }

    private View.OnClickListener favorites_action = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isFavorite) {
                try {
                    String selection = DataEntry.COLUMN_MOVIE_ID + " = ?";
                    String[] selectionArgs = {String.valueOf(sId)};
                    int rowsDeleted = getContentResolver().delete(DataEntry.CONTENT_URI, selection, selectionArgs);
                    if (rowsDeleted != 0) {
                        Toast.makeText(getApplicationContext(), getString(R.string.deleting_successful), Toast.LENGTH_SHORT).show();
                        mTV_favorites.setText(getString(R.string.remove_favorites_button));
                        mIV_favorites.setImageResource(R.drawable.ic_favorite_enabled_24dp);
                        isFavorite = false;
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    long creation_time = System.currentTimeMillis();
                    ContentValues cv = new ContentValues();
                    cv.put(DataEntry.COLUMN_NAME, sTitle);
                    cv.put(DataEntry.COLUMN_CREATION_DATE, creation_time);
                    cv.put(DataEntry.COLUMN_MOVIE_ID, sId);
                    cv.put(DataEntry.COLUMN_TYPE, SharedData.getSearchType(getApplicationContext()));
                    cv.put(DataEntry.COLUMN_POSTER_PATH, mPosterPath);
                    Uri newUri = getContentResolver().insert(DataEntry.CONTENT_URI, cv);
                    if (newUri != null) {
                        Toast.makeText(getApplicationContext(), getString(R.string.saving_successful), Toast.LENGTH_SHORT).show();
                        mTV_favorites.setText(getString(R.string.add_favorites_button));
                        mIV_favorites.setImageResource(R.drawable.ic_favorite_disabled_24dp);
                        isFavorite = true;
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private View.OnClickListener moreDetails_action = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //this open the MoreDetailActivity
            Intent intent = new Intent(DetailsActivity.this, MoreDetailsActivity.class);
            startActivity(intent);
        }
    };

    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case LOADER_ID:
                return new DataLoader(this, builderUrl(sId).toString());
            case DATA_LOADER_ID:
                Uri uri = DataEntry.CONTENT_URI;
                String selection = DataEntry.COLUMN_MOVIE_ID + " = ?";
                String[] selectionArgs = {String.valueOf(sId)};
                return new CursorLoader(this,
                        uri,
                        null,
                        selection,
                        selectionArgs,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (loader.getId() == LOADER_ID) {
            movie = (MoviesData) data;
            if (NetUtils.isConnected(this)) {
                if (movie != null) {
                    String header = movie.getBackground_Path();

                    if (!header.equals(NULL_VALUE)) {
                        Glide.with(this).load(IMAGE_BASE_HEADER + header).thumbnail(0.1f).dontTransform().into(mHeader);
                    } else {
                        mHeader.setVisibility(View.GONE);
                        mLoading_header.setVisibility(View.GONE);
                    }

                    mPosterPath = movie.getPoster_Path();
                    if (!mPosterPath.equals(NULL_VALUE)) {
                        Glide.with(this).load(IMAGE_BASE_POSTER + mPosterPath).crossFade().dontTransform().into(mPoster);
                    } else {
                        mPoster.setImageResource(R.drawable.placeholder_poster);
                    }
                    mOriginalTitle.setText(Html.fromHtml(getString(R.string.original_title, movie.getOriginal_title())));
                    mPopularity.setText(String.format(POPULARITY_FORMAT, movie.getPopularity()));
                    mDescription.setVisibility(View.VISIBLE);
                    if (!movie.getDescription().isEmpty()) {
                        mDescription.setText(Html.fromHtml(getString(R.string.description, movie.getDescription())));
                    } else {
                        mDescription.setText(Html.fromHtml(getString(R.string.description, getString(R.string.unknown))));
                    }
                    mRelease_Date.setText(movie.getRelease_date());
                    mRatingValue.setText(String.valueOf(movie.getVote_Average()));
                    mRatingBar.setRating((float) movie.getVote_Average() * 5 / 10);
                    mGenres.setText(Html.fromHtml(getString(R.string.genres, movie.getGenres())));
                    mCompanies.setText(Html.fromHtml(getString(R.string.production_companies, movie.getProductionCompanies())));
                    mCountries.setText(Html.fromHtml(getString(R.string.production_countries, movie.getProductionCountries())));
                    String mediaSelection = searchValue[sDataType];
                    if (mediaSelection.equals(getString(R.string.search_movies_value))) {
                        mRuntime.setText(Html.fromHtml(getString(R.string.runtime, movie.getRuntime())));
                    } else {
                        mRuntime.setText(Html.fromHtml(getString(R.string.seasons_and_episodes, movie.getNumberOfSeasons(), movie.getNumberOfEpisodes())));
                    }
                    mLanguages.setText(Html.fromHtml(getString(R.string.spoken_languages, movie.getSpokenLanguages())));
                    mStatus.setText(Html.fromHtml(getString(R.string.status, movie.getStatus())));
                    mLoading_activity.setVisibility(View.GONE);
                    cl_content.setVisibility(View.VISIBLE);
                } else {
                    mLoading_activity.setVisibility(View.GONE);
                    mEmpty_Image.setVisibility(View.VISIBLE);
                    mEmpty_message.setVisibility(View.VISIBLE);
                }
            } else {
                mLoading_activity.setVisibility(View.GONE);
                int icon = R.drawable.ic_portable_wifi_off;
                String title = getString(R.string.no_internet_title);
                String message = getString(R.string.no_internet);
                DialogManager dialogManager = new DialogManager(this, DIALOG_ID, this);
                dialogManager.showMessage(icon, title, message);
            }
        }
        if (loader.getId() == DATA_LOADER_ID) {
            Cursor cursor = (Cursor) data;
            int count = cursor.getCount();
            if (count > 0) {
                mTV_favorites.setText(getString(R.string.remove_favorites_button));
                mIV_favorites.setImageResource(R.drawable.ic_favorite_enabled_24dp);
                isFavorite = true;
            } else {
                mTV_favorites.setText(getString(R.string.add_favorites_button));
                mIV_favorites.setImageResource(R.drawable.ic_favorite_disabled_24dp);
                isFavorite = false;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    //this builds the url
    private URL builderUrl(int id) {
        String language = sharedPrefs.getString(getString(R.string.settings_language_key), getString(R.string.settings_language_default));
        Uri.Builder builtUri;
        builtUri = Uri.parse(NetUtils.SERVER_URL).buildUpon();
        builtUri.appendPath(searchValue[sDataType])
                .appendPath(String.valueOf(id))
                .appendQueryParameter(NetUtils.LANGUAGE_PARAM, language)
                .appendQueryParameter(NetUtils.API_KEY, getString(R.string.api_key))
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    //restart the loader
    private void refresh() {
        loaderManager.restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void negativeAction(int dialog_id) {
        finish();
    }

    @Override
    public void positiveAction(int dialog_id) {
        refresh();
    }

    //Go back in the previous activity without reloading
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
