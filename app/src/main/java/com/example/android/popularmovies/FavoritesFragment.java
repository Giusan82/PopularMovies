package com.example.android.popularmovies;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.MoviesData;
import com.example.android.popularmovies.data.DataContract.DataEntry;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int DATA_LOADER_ID = 1;
    private RecyclerView recyclerView;
    private ArrayList<MoviesData> mItems;
    private GridAdapter adapter;
    private ProgressBar mLoading_list;
    private ImageView mIV_empty_list;
    private TextView mTV_empty_list;
    private TextView mPages_tv;
    private TextView mResults_tv;
    private ImageView mPrevius_page_iv;
    private ImageView mNext_page_iv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Find the id for the respective views
        recyclerView = rootView.findViewById(R.id.rv_list);
        mLoading_list = rootView.findViewById(R.id.loading_list);
        mIV_empty_list = rootView.findViewById(R.id.iv_empty);
        mTV_empty_list = rootView.findViewById(R.id.tv_empty);
        mTV_empty_list.setText(getString(R.string.favorites_no_found_message));
        mPages_tv = rootView.findViewById(R.id.tv_page);
        mPages_tv.setVisibility(View.GONE);
        mResults_tv = rootView.findViewById(R.id.tv_results);
        mPrevius_page_iv = rootView.findViewById(R.id.iv_navigate_before);
        mPrevius_page_iv.setVisibility(View.GONE);
        mNext_page_iv = rootView.findViewById(R.id.iv_navigate_next);
        mNext_page_iv.setVisibility(View.GONE);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(this.getResources().getInteger(R.integer.spanCount),
                StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        getActivity().getSupportLoaderManager().initLoader(DATA_LOADER_ID, null, this);
        //the ArrayList is initialized
        mItems = new ArrayList<>();
        adapter = new GridAdapter(getContext(), mItems, true);
        recyclerView.setAdapter(adapter);
        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case DATA_LOADER_ID:
                Uri uri = DataEntry.CONTENT_URI;
                String sortOrder = DataEntry.COLUMN_CREATION_DATE + " DESC";
                return new CursorLoader(getContext(),
                        uri,
                        null,
                        null,
                        null,
                        sortOrder);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == DATA_LOADER_ID) {
            if (cursor == null) {
                Log.e("FavoritesFragment", "cursor is null");
                return;
            }
            Log.e("FavoritesFragment", "onLoadFinished");
            clear();
            mLoading_list.setVisibility(View.GONE);
            String results = getString(R.string.number_results, cursor.getCount());
                if(cursor.getCount() != 0){
                    for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                        int movie_id = cursor.getInt(cursor.getColumnIndex(DataEntry.COLUMN_MOVIE_ID));
                        String title = cursor.getString(cursor.getColumnIndex(DataEntry.COLUMN_NAME));
                        String poster_path = cursor.getString(cursor.getColumnIndex(DataEntry.COLUMN_POSTER_PATH));
                        int data_type = cursor.getInt(cursor.getColumnIndex(DataEntry.COLUMN_TYPE));

                        mResults_tv.setText(results);
                        mItems.add(new MoviesData(getContext(), movie_id, title, poster_path, data_type));
                        Log.e("FavoritesFragment", "cursor is not 0");
                    }
                    adapter.notifyDataSetChanged();
                    mIV_empty_list.setVisibility(View.GONE);
                    mTV_empty_list.setVisibility(View.GONE);
                }else {
                    mResults_tv.setText(results);
                    Log.e("FavoritesFragment", "cursor is 0");
                    mIV_empty_list.setVisibility(View.VISIBLE);
                    mTV_empty_list.setVisibility(View.VISIBLE);
                }


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        clear();
    }

    private void clear() {
        //clear the arraylist
        this.mItems.clear();
        adapter.notifyDataSetChanged();
    }

    //this create an ActionsBar menu and add an searchView on ActionsBar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
        MenuItem search = menu.findItem(R.id.search_view);
        search.setVisible(false);
        MenuItem searchType = menu.findItem(R.id.sp_search_type);
        searchType.setVisible(false);
    }
}
