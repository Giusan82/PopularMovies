package com.example.android.popularmovies.utilities;


import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class DataLoader extends AsyncTaskLoader<List<MoviesList>>{

    private String mUrl; //Query Url
    private Context mContext;
    private List<MoviesList> mList;

    /**Costructor
     *
     * @param context of the activity
     * @param url to load data from server
     */
    public DataLoader(Context context, String url) {
        super(context);
        mContext = context;
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        if(mList != null){
            deliverResult(mList);
        }else{
            forceLoad();
        }
    }

    @Override
    public List<MoviesList> loadInBackground() {
        if(mUrl == null){
            Log.e("DataLoader", "Url is null");
            return null;
        }else{
            return ApiRequest.fetchData(mContext, mUrl);
        }
    }

    @Override
    public void deliverResult(List<MoviesList> data) {
        mList = data;
        super.deliverResult(data);
    }
}
