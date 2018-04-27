package com.example.android.popularmovies.utilities;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

public class DataLoader extends AsyncTaskLoader<MoviesData>{
    private String mUrl; //Query Url
    private Context mContext;
    private MoviesData mData;
    public DataLoader(Context context, String url) {
        super(context);
        mContext = context;
        mUrl = url;
    }
    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        } else {
            forceLoad();
        }
    }
    @Override
    public MoviesData loadInBackground() {
        if (mUrl == null) {
            Log.e("DataLoader", "Url is null");
            return null;
        } else {
            return NetUtils.fetchData(mContext, mUrl);
        }
    }
    @Override
    public void deliverResult(MoviesData data) {
        mData = data;
        super.deliverResult(data);
    }
}
