package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.android.popularmovies.utilities.SharedData;
import com.example.android.popularmovies.utilities.TabsAdapter;

public class MoreDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_details);
        setTitle(DetailsActivity.sTitle);

//        Intent intent = getIntent();
//        if (intent.hasExtra(SharedData.EXTRA_MOVIE_ID)) {
//            mId = intent.getIntExtra(SharedData.EXTRA_MOVIE_ID, SharedData.DEFAULT_ID);
//            Log.e("DetailActivity", "Movie ID: " + mId);
//        }
//        if (intent.hasExtra(SharedData.EXTRA_MOVIE_TITLE)) {
//            mTitle = intent.getStringExtra(SharedData.EXTRA_MOVIE_TITLE);
//            setTitle(mTitle);
//        }
//
//        if (intent.hasExtra(SharedData.EXTRA_MOVIE_TYPE)) {
//            mDataType = intent.getIntExtra(SharedData.EXTRA_MOVIE_TYPE, SharedData.DEFAULT_TYPE);
//        } else {
//            mDataType = SharedData.getSearchType(this);
//        }

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager_details);
        // Create an adapter that knows which fragment should be shown on each page
        TabsAdapter tabsAdapter = new TabsAdapter(this, getSupportFragmentManager(), true);
        // Set the adapter onto the view pager
        viewPager.setAdapter(tabsAdapter);
        // Find the tab layout that shows the tabs
        TabLayout tabLayout = findViewById(R.id.sliding_tabs_details);
        tabLayout.setupWithViewPager(viewPager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tabLayout.setElevation(3);
        }
    }
}
