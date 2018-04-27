package com.example.android.popularmovies;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class VideosFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<MoviesData>>, DialogManager.AlertDialogAction {
    private static final int LOADER_ID_VIDEOS = 5; //loader id of this activity
    private static final int DIALOG_ID = 0; //id of that dialog
    private SharedPreferences sharedPrefs;
    private String[] searchValue;
    private RecyclerView recyclerView;
    private ArrayList<MoviesData> mItems;
    private ListAdapter adapter;
    private static LoaderManager loaderManager;
    private ImageView mIV_empty_list;
    private TextView mTV_empty_list;
    private ProgressBar mLoading_list;
    private TextView mResults_tv;
    private ImageView mPrevius_page_iv;
    private ImageView mNext_page_iv;
    private TextView mPages_tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        searchValue = getResources().getStringArray(R.array.search_type_value);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //Find the id for the respective views
        recyclerView = rootView.findViewById(R.id.rv_list);
        mResults_tv = rootView.findViewById(R.id.tv_results);
        mPages_tv = rootView.findViewById(R.id.tv_page);
        mPages_tv.setVisibility(View.GONE);
        mPrevius_page_iv = rootView.findViewById(R.id.iv_navigate_before);
        mPrevius_page_iv.setVisibility(View.GONE);
        mNext_page_iv = rootView.findViewById(R.id.iv_navigate_next);
        mNext_page_iv.setVisibility(View.GONE);
        mLoading_list = rootView.findViewById(R.id.loading_list);
        mIV_empty_list = rootView.findViewById(R.id.iv_empty);
        mTV_empty_list = rootView.findViewById(R.id.tv_empty);
        mTV_empty_list.setText(getString(R.string.videos_no_found_message));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        loaderManager = getLoaderManager();
        if (NetUtils.isConnected(getContext())) {
            loaderManager.initLoader(LOADER_ID_VIDEOS, null, this);
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
        adapter = new ListAdapter(getContext(), mItems, false);
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    //this builds the url
    private URL builderUrl(int id) {
        String language = sharedPrefs.getString(getString(R.string.settings_language_key), getString(R.string.settings_language_default));
        Uri.Builder builtUri;
        builtUri = Uri.parse(NetUtils.SERVER_URL).buildUpon();
        builtUri.appendPath(searchValue[DetailsActivity.sDataType])
                .appendPath(String.valueOf(id))
                .appendPath(NetUtils.VIDEOS_PATH)
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

    @Override
    public Loader<List<MoviesData>> onCreateLoader(int i, Bundle bundle) {
        return new ListLoader(getContext(), builderUrl(DetailsActivity.sId).toString());
    }

    @Override
    public void onLoadFinished(Loader<List<MoviesData>> loader, List<MoviesData> data) {
        if (NetUtils.isConnected(getContext())) {
            clear();
            if (data != null && !data.isEmpty()) {
                //if not, add all items into the ArrayList
                mItems.addAll(data);
                String results = getString(R.string.number_results, mItems.size());
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

    //restart the loader
    private void refresh() {
        loaderManager.restartLoader(LOADER_ID_VIDEOS, null, this);
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
