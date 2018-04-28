package com.example.android.popularmovies;


import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.DialogManager;
import com.example.android.popularmovies.utilities.NetUtils;
import com.example.android.popularmovies.utilities.ListLoader;
import com.example.android.popularmovies.utilities.MoviesData;
import com.example.android.popularmovies.utilities.SharedData;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<MoviesData>>, SharedPreferences.OnSharedPreferenceChangeListener, DialogManager.AlertDialogAction {

    private static final int LOADER_ID = 0; //loader id of this activity
    private static final int LOADER_ID_REFRESH = 10;
    private static final int DIALOG_ID = 0;
    private RecyclerView recyclerView;
    private ArrayList<MoviesData> mItems;
    private GridAdapter adapter;
    private SharedPreferences sharedPrefs;
    private String[] searchValue;

    private static LoaderManager loaderManager;
    private TextView mPages_tv;
    private TextView mResults_tv;
    private String query;
    private ImageView mPrevius_page_iv;
    private ImageView mNext_page_iv;
    private int mPage = 1;
    private int mTotal_page;
    private ProgressBar mLoading_list;
    private ImageView mIV_empty_list;
    private TextView mTV_empty_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        //Find the id for the respective views
        recyclerView = rootView.findViewById(R.id.rv_list);
        mPages_tv = rootView.findViewById(R.id.tv_page);
        mResults_tv = rootView.findViewById(R.id.tv_results);
        mPrevius_page_iv = rootView.findViewById(R.id.iv_navigate_before);
        mNext_page_iv = rootView.findViewById(R.id.iv_navigate_next);
        searchValue = getResources().getStringArray(R.array.search_type_value);
        mLoading_list = rootView.findViewById(R.id.loading_list);
        mIV_empty_list = rootView.findViewById(R.id.iv_empty);
        mTV_empty_list = rootView.findViewById(R.id.tv_empty);

        //set the action when the respective views are clicked
        mPrevius_page_iv.setOnClickListener(previousPage);
        mNext_page_iv.setOnClickListener(nextPage);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(SharedData.numberOfColumns(getActivity()),
                StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        // Get a reference to the LoaderManager, in order to interact with loaders.
        loaderManager = getLoaderManager();
        if (NetUtils.isConnected(getContext())) {
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            //if there are no internet connection, an alert dialog message is displayed
            int icon = R.drawable.ic_portable_wifi_off;
            String title = getString(R.string.no_internet_title);
            String message = getString(R.string.no_internet);
            DialogManager dialogManager = new DialogManager(getContext(), DIALOG_ID, this);
            dialogManager.showMessage(icon, title, message);
        }
        //the ArrayList is initialized
        mItems = new ArrayList<>();
        adapter = new GridAdapter(getContext(), mItems, false);
        recyclerView.setAdapter(adapter);
        //Register the fragment as an OnSharedPreferenceChangedListener to receive a callback when a SharedPreference has changed.
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);
        return rootView;
    }

    //this create an ActionsBar menu and add an searchView on ActionsBar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
        MenuItem search = menu.findItem(R.id.search_view);
        final SearchView searchField;
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
        ImageView close = searchField.findViewById(R.id.search_close_btn);//source: https://stackoverflow.com/a/24844944
        final EditText search_query = searchField.findViewById(R.id.search_src_text);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_query.setText("");
                searchField.setQuery("", false);
                refresh();
            }
        });
        //setup a spinner in the actionBar for filtering movies or tv shows
        MenuItem searchType = menu.findItem(R.id.sp_search_type);
        Spinner searchType_spinner = (Spinner) searchType.getActionView();
        ArrayAdapter searchTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.search_type, android.R.layout.simple_spinner_item);
        searchTypeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        searchType_spinner.setAdapter(searchTypeAdapter);
        searchType_spinner.setOnItemSelectedListener(searchTypeListener);
        searchType_spinner.setSelection(SharedData.getSearchType(getContext()));
    }

    private AdapterView.OnItemSelectedListener searchTypeListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String selection = (String) adapterView.getItemAtPosition(i);
            if (!TextUtils.isEmpty(selection)) {
                if (selection.equals(getString(R.string.search_movies))) {
                    SharedData.setSearchType(getContext(), i);
                }
                if (selection.equals(getString(R.string.search_tv))) {
                    SharedData.setSearchType(getContext(), i);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            SharedData.getSearchType(getContext());
        }
    };
    private View.OnClickListener previousPage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mPage > 1) {
                mPage--;
                refresh();
            }
        }
    };
    private View.OnClickListener nextPage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mTotal_page > 1) {
                mPage++;
                refresh();
                if (mPage == mTotal_page) {
                    mPage = 0;
                }
            }
        }
    };

    @Override
    public Loader<List<MoviesData>> onCreateLoader(int i, Bundle bundle) {
        mIV_empty_list.setVisibility(View.GONE);
        mTV_empty_list.setVisibility(View.GONE);
        return new ListLoader(getActivity(), builderUrl(query).toString());
    }

    @Override
    public void onLoadFinished(Loader<List<MoviesData>> loader, List<MoviesData> data) {
        if (NetUtils.isConnected(getContext())) {
            clear();
            if (loader.getId() == LOADER_ID_REFRESH) {
                recyclerView.scrollToPosition(0);
            }
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
                mLoading_list.setVisibility(View.GONE);
            } else {
                clear();
                mLoading_list.setVisibility(View.GONE);
                mIV_empty_list.setVisibility(View.VISIBLE);
                mTV_empty_list.setVisibility(View.VISIBLE);
            }
        } else {
            mLoading_list.setVisibility(View.GONE);
            int icon = R.drawable.ic_portable_wifi_off;
            String title = getString(R.string.no_internet_title);
            String message = getString(R.string.no_internet);
            DialogManager dialogManager = new DialogManager(getContext(), DIALOG_ID, this);
            dialogManager.showMessage(icon, title, message);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<MoviesData>> loader) {
        clear();
    }

    private void clear() {
        //clear the arraylist
        this.mItems.clear();
        adapter.notifyDataSetChanged();
    }

    //this builds the url
    //The movies and tv shows are sorted using the query param "sort_by" because allows to add more options in the advanced search
    private URL builderUrl(String query) {
        String orderBy = sharedPrefs.getString(getString(R.string.settings_orderBy_key), getString(R.string.settings_orderBy_default));
        String genre = sharedPrefs.getString(getString(R.string.settings_genre_ids_key), getString(R.string.settings_genre_ids_default));
        Boolean adult = sharedPrefs.getBoolean(getString(R.string.settings_adult_content_key), getResources().getBoolean(R.bool.pref_include_adult));
        Boolean video = sharedPrefs.getBoolean(getString(R.string.settings_include_video_key), getResources().getBoolean(R.bool.pref_include_video));
        String type = searchValue[SharedData.getSearchType(getContext())];
        String language = sharedPrefs.getString(getString(R.string.settings_language_key), getString(R.string.settings_language_default));
        String original_language = sharedPrefs.getString(getString(R.string.settings_original_language_key), getString(R.string.settings_original_language_default));

        Uri.Builder builtUri;
        builtUri = Uri.parse(NetUtils.SERVER_URL).buildUpon();
        if (query == null || query.isEmpty()) {
            builtUri.appendPath(NetUtils.DISCOVER_PATH)
                    .appendPath(type)
                    .appendQueryParameter(NetUtils.SORT_BY_PARAM, orderBy)
                    .appendQueryParameter(NetUtils.WITH_GENRE_PARAM, genre);
        } else {
            builtUri.appendPath(NetUtils.SEARCH_PATH)
                    .appendPath(type)
                    .appendQueryParameter(NetUtils.QUERY_PARAM, query);
        }
        builtUri.appendQueryParameter(NetUtils.PAGE_PARAM, String.valueOf(mPage))
                .appendQueryParameter(NetUtils.API_KEY, getString(R.string.api_key))
                .appendQueryParameter(NetUtils.INCLUDE_ADULT_PARAM, adult.toString())
                .appendQueryParameter(NetUtils.INCLUDE_VIDEO_PARAM, video.toString())
                .appendQueryParameter(NetUtils.LANGUAGE_PARAM, language)
                .appendQueryParameter(NetUtils.ORIGINAL_LANGUAGE_PARAM, original_language)
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
    //refresh using another id, because in this way avoid that the RecyclerView scroll up to zero position before than loading is complete when the user change the page
    private void refresh() {
        loaderManager.restartLoader(LOADER_ID_REFRESH, null, this);
    }

    private void search(String input) {
        //determine if connection is active after the search button is clicked
        if (NetUtils.isConnected(getContext())) {
            //restart the loader with the new data
            query = input;
            if (!input.isEmpty()) {
                mPage = 1;
                refresh();
            }
        } else {
            mLoading_list.setVisibility(View.GONE);
            int icon = R.drawable.ic_portable_wifi_off;
            String title = getString(R.string.no_internet_title);
            String message = getString(R.string.no_internet);
            DialogManager dialogManager = new DialogManager(getContext(), DIALOG_ID, this);
            dialogManager.showMessage(icon, title, message);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //when a sharedPreferences si changed
        mPage = 1;
        refresh();
        mLoading_list.setVisibility(View.VISIBLE);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void negativeAction(int dialog_id) {
        getActivity().finish();
    }

    @Override
    public void positiveAction(int dialog_id) {
        refresh();
    }
}
