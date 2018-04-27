package com.example.android.popularmovies;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.DialogManager;
import com.example.android.popularmovies.utilities.ListLoader;
import com.example.android.popularmovies.utilities.MoviesData;
import com.example.android.popularmovies.utilities.NetUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReviewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<MoviesData>>, DialogManager.AlertDialogAction{
    private static final int LOADER_ID_REVIEWS = 4; //loader id of this activity
    private static final int LOADER_ID_REVIEWS_REFRESH = 11;
    private static final int DIALOG_ID = 0;
    private static LoaderManager loaderManager;
    private RecyclerView recyclerView;
    private ArrayList<MoviesData> mItems;
    private ListAdapter adapter;
    private SharedPreferences sharedPrefs;
    private String[] searchValue;
    private TextView mPages_tv;
    private TextView mResults_tv;
    private ImageView mPrevius_page_iv;
    private ImageView mNext_page_iv;
    private int mTotal_page;
    private ProgressBar mLoading_list;
    private int mPage = 1;
    private ImageView mIV_empty_list;
    private TextView mTV_empty_list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //Find the id for the respective views
        recyclerView = rootView.findViewById(R.id.rv_list);
        mPages_tv = rootView.findViewById(R.id.tv_page);
        mResults_tv = rootView.findViewById(R.id.tv_results);
        mPrevius_page_iv = rootView.findViewById(R.id.iv_navigate_before);
        mNext_page_iv = rootView.findViewById(R.id.iv_navigate_next);
        mLoading_list = rootView.findViewById(R.id.loading_list);
        mIV_empty_list = rootView.findViewById(R.id.iv_empty);
        mTV_empty_list = rootView.findViewById(R.id.tv_empty);
        mTV_empty_list.setText(getString(R.string.reviews_no_found_message));
        //this get the array from resources
        searchValue = getResources().getStringArray(R.array.search_type_value);

        //set the action when the respective views are clicked
        mPrevius_page_iv.setOnClickListener(previousPage);
        mNext_page_iv.setOnClickListener(nextPage);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        // Get a reference to the LoaderManager, in order to interact with loaders.
        loaderManager = getLoaderManager();
        if (NetUtils.isConnected(getContext())) {
            loaderManager.initLoader(LOADER_ID_REVIEWS, null, this);
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
        adapter = new ListAdapter(getContext(), mItems, true);
        recyclerView.setAdapter(adapter);
        return rootView;
    }

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

    //restart the loader
    private void refresh() {
        loaderManager.restartLoader(LOADER_ID_REVIEWS_REFRESH, null, this);
    }

    @Override
    public Loader<List<MoviesData>> onCreateLoader(int id, Bundle args) {
        return new ListLoader(getContext(), builderUrl(DetailsActivity.sId).toString());
    }

    @Override
    public void onLoadFinished(Loader<List<MoviesData>> loader, List<MoviesData> data) {
        if (NetUtils.isConnected(getContext())) {
            clear();
            if(loader.getId() == LOADER_ID_REVIEWS_REFRESH){
                recyclerView.scrollToPosition(0);
            }
            Log.e("ReviewsFragment", "onLoadFinished");
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
                mIV_empty_list.setVisibility(View.GONE);
                mTV_empty_list.setVisibility(View.GONE);
            } else {
                clear();
                mLoading_list.setVisibility(View.GONE);
                mIV_empty_list.setVisibility(View.VISIBLE);
                mTV_empty_list.setVisibility(View.VISIBLE);
                Log.e("ReviewsFragment", "Data is null");
            }
        } else {
            //mLoading_list.setVisibility(View.GONE);
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
    private URL builderUrl(int id) {
        Log.e("ReviewsFragment", "builderUrl: " + DetailsActivity.sDataType);
        String language = sharedPrefs.getString(getString(R.string.settings_language_key), getString(R.string.settings_language_default));
        Uri.Builder builtUri;
        builtUri = Uri.parse(NetUtils.SERVER_URL).buildUpon();
        builtUri.appendPath(searchValue[DetailsActivity.sDataType])
                .appendPath(String.valueOf(id))
                .appendPath(NetUtils.REVIEWS_PATH)
                .appendQueryParameter(NetUtils.LANGUAGE_PARAM, language)
                .appendQueryParameter(NetUtils.API_KEY, getString(R.string.api_key))
                .appendQueryParameter(NetUtils.PAGE_PARAM, String.valueOf(mPage))
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.e("ReviewsFragment", url.toString());
        return url;
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
