package com.example.android.popularmovies.utilities;


import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class ListLoader extends AsyncTaskLoader<List<MoviesData>> {
    private String mUrl; //Query Url
    private Context mContext;
    private List<MoviesData> mList;

    /**
     * Costructor
     *
     * @param context of the activity
     * @param url     to load data from server
     */
    public ListLoader(Context context, String url) {
        super(context);
        mContext = context;
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        if (mList != null) {
            deliverResult(mList);
        } else {
            forceLoad();
        }
    }

    @Override
    public List<MoviesData> loadInBackground() {
        if (mUrl == null) {
            Log.e("ListLoader", "Url is null");
            return null;
        } else {
            return NetUtils.fetchList(mContext, mUrl);
        }
    }

    @Override
    public void deliverResult(List<MoviesData> data) {
        mList = data;
        super.deliverResult(data);
    }
}
