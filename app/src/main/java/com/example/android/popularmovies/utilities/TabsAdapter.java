package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.popularmovies.FavoritesFragment;
import com.example.android.popularmovies.MainFragment;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.ReviewsFragment;
import com.example.android.popularmovies.VideosFragment;

public class TabsAdapter extends FragmentPagerAdapter{
    private Context mContext;
    private boolean isDetails;

    public TabsAdapter(Context context, FragmentManager fm, boolean is_details) {
        super(fm);
        mContext = context;
        isDetails = is_details;
    }

    @Override
    public Fragment getItem(int position) {
        if(isDetails){
            if(position == 0){
                return new VideosFragment();
            }else{
                return new ReviewsFragment();
            }
        }else {
            if(position == 0){
                return new MainFragment();
            }else {
                return new FavoritesFragment();
            }
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(isDetails){
            if(position == 0){
                return mContext.getString(R.string.videos_tab);
            }else {
                return mContext.getString(R.string.reviews_tab);
            }
        }else {
            if(position == 0){
                return mContext.getString(R.string.main_tab);
            }else {
                return mContext.getString(R.string.favorites_tab);
            }
        }
    }
}
