package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.utilities.DataLoader;
import com.example.android.popularmovies.utilities.MoviesList;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<MoviesList>>,
        SharedPreferences.OnSharedPreferenceChangeListener{
    private static final int LOADER_ID = 0;
    private static final String SERVER_URL = "https://api.themoviedb.org/3/";
    private static final String MOVIE_PATH = "movie";
    private static final String DISCOVER_PATH = "discover";
    private static final String POPULAR_PATH = "popular";
    private static final String SEARCH_PATH = "search";
    private final static String QUERY_PARAM = "query";
    private final static String WITH_GENRE_PARAM = "with_genres";
    private final static String SORT_BY_PARAM = "sort_by";
    private final static String PAGE_PARAM = "page"; //this set the page displayed
    private final static String API_KEY = "api_key"; //this is the api key of unsplash.com
    private RecyclerView recyclerView;
    private ArrayList<MoviesList> mItems;
    private GridAdapter adapter;
    private SharedPreferences sharedPrefs;

    private LoaderManager loaderManager;
    private TextView mPages_tv;
    private TextView mResults_tv;
    private MoviesList movies;
    private String query;
    private ImageView mPrevius_page_iv;
    private ImageView mNext_page_iv;
    private int mPage = 1;
    private int mTotal_page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        //Find the id for the respective views
        recyclerView = findViewById(R.id.rv_list);
        mPages_tv = findViewById(R.id.tv_page);
        mResults_tv = findViewById(R.id.tv_results);
        mPrevius_page_iv = findViewById(R.id.iv_navigate_before);
        mNext_page_iv = findViewById(R.id.iv_navigate_next);

        mPrevius_page_iv.setOnClickListener(previousPage);
        mNext_page_iv.setOnClickListener(nextPage);

        //Here is determined as the collection of items is displayed
        GridLayoutManager layoutManager = new GridLayoutManager(this,
                this.getResources().getInteger(R.integer.spanCount));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_ID, null, this);

        //the ArrayList is initialized
        mItems = new ArrayList<>();
        adapter = new GridAdapter(this, mItems);
        recyclerView.setAdapter(adapter);


        //Register MainActivity as an OnSharedPreferenceChangedListener to receive a callback when a SharedPreference has changed.
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    private View.OnClickListener previousPage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mPage >1){
                mPage--;
                refresh();
            }
        }
    };
    private View.OnClickListener nextPage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mTotal_page > 1){
                mPage++;
                refresh();
                if(mPage == mTotal_page){
                    mPage = 0;
                }
            }
        }
    };

    @Override
    public Loader<List<MoviesList>> onCreateLoader(int i, Bundle bundle) {
        return new DataLoader(this, builderUrl(query).toString());
    }

    @Override
    public void onLoadFinished(Loader<List<MoviesList>> loader, List<MoviesList> data) {
        if (isConnected()) {
            clear();
            if (data != null && !data.isEmpty()) {
                //if not, add all items into the ArrayList
                mItems.addAll(data);
                mTotal_page = mItems.get(0).getTotal_Page();
                String pages = getString(R.string.number_pages, mItems.get(0).getPage(), mTotal_page);
                String results = getString(R.string.number_results, mItems.get(0).getTotal_results());
                mPages_tv.setText(pages);
                mResults_tv.setText(results);

                //and notify to adapter to update the data
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<MoviesList>> loader) {
        clear();
    }
    private void clear() {
        this.mItems.clear();
        recyclerView.scrollToPosition(0);
        adapter.notifyDataSetChanged();
    }

    //this create an ActionsBar menu and add an searchView on ActionsBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem search = menu.findItem(R.id.search_view);
        SearchView searchField;
        //this add the searchView to actionBar
        searchField = (SearchView) search.getActionView();
        //set the hint text on searchView
        searchField.setQueryHint(getString(R.string.searchHint));
        //this expand the searchView without click on it
        searchField.setIconified(false);
        searchField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                /**this active the instant search*/
                search(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String input) {
                search(input);
                return true;
            }
        });

        MenuItem searchType= menu.findItem(R.id.sp_search_type);
        Spinner searchType_spinner = (Spinner) searchType.getActionView();
        ArrayAdapter searchTypeAdapter = ArrayAdapter.createFromResource(this, R.array.search_type, android.R.layout.simple_spinner_item);
        searchTypeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        searchType_spinner.setAdapter(searchTypeAdapter);
        searchType_spinner.setOnItemSelectedListener(searchTypeListener);
        return true;
    }

    private AdapterView.OnItemSelectedListener searchTypeListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String selection = (String) adapterView.getItemAtPosition(i);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //this builds the url
    private URL builderUrl(String query) {
        String orderBy = sharedPrefs.getString(getString(R.string.settings_orderBy_key), getString(R.string.settings_orderBy_default));
        String genre = sharedPrefs.getString(getString(R.string.settings_genre_ids_key), getString(R.string.settings_genre_ids_default));
        Uri.Builder builtUri;
        builtUri = Uri.parse(SERVER_URL).buildUpon();
        if (query == null || query.isEmpty()) {
            builtUri.appendPath(DISCOVER_PATH)
                    .appendPath(MOVIE_PATH)
                    .appendQueryParameter(SORT_BY_PARAM, orderBy)
                    .appendQueryParameter(WITH_GENRE_PARAM, genre);
        }else{
            Log.e("Query", query);
            builtUri.appendPath(SEARCH_PATH)
                    .appendPath(MOVIE_PATH)
                    .appendQueryParameter(QUERY_PARAM, query);
        }
        builtUri.appendQueryParameter(PAGE_PARAM, String.valueOf(mPage))
                .appendQueryParameter(API_KEY, getString(R.string.api_key))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.e("Url", url.toString());
        return url;
    }

    private void search(String input) {
        //determine if connection is active after the search button is clicked
        if (isConnected()) {
            //restart the loader with the new data
                mPage =1;
                query = input;
                refresh();
                Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No connection", Toast.LENGTH_SHORT).show();
        }
    }

    //restart the loader
    private void refresh() {
        loaderManager.restartLoader(LOADER_ID, null, this);
    }

    //determine if connection is active
    private Boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.settings_genre_ids_key))){

        }
        mPage =1;
        refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
