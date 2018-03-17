package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.utilities.MoviesList;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
    public static final String EXTRA_MOVIE = "extra_movie";
    private static final String IMAGE_BASE_HEADER = "http://image.tmdb.org/t/p/original/";
    private static final String IMAGE_BASE_POSTER = "http://image.tmdb.org/t/p/w92/";

    private TextView mOriginalTitle;
    private TextView mDescription;
    private ImageView mHeader;
    private ImageView mPoster;
    private MoviesList mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mOriginalTitle = findViewById(R.id.tv_original_title);
        mDescription = findViewById(R.id.tv_description);
        mHeader = findViewById(R.id.iv_header);
        mPoster = findViewById(R.id.iv_poster);

        Intent intent = getIntent();
        if(intent.hasExtra(EXTRA_MOVIE)){
            //this receive a custom object, source: https://stackoverflow.com/a/7827593
            mMovie = (MoviesList)intent.getSerializableExtra(EXTRA_MOVIE);
        }
        if(mMovie != null){
            mOriginalTitle.setText(Html.fromHtml(getString(R.string.original_title, mMovie.getOriginal_title())));

            String header = mMovie.getBackground_Path();
            if (header.length() != 0){
                Glide.with(this).load(IMAGE_BASE_HEADER + header).crossFade().dontTransform().into(mHeader);
            }else{
                mHeader.setVisibility(View.GONE);
            }

            String poster = mMovie.getPoster_Path();
            if (poster.length() != 0){
                Glide.with(this).load(IMAGE_BASE_HEADER + poster).crossFade().dontTransform().into(mPoster);
            }else{
                mPoster.setImageResource(R.drawable.placeholder);
            }
            setTitle(mMovie.getTitle());
            mDescription.setText(Html.fromHtml(getString(R.string.description, mMovie.getDescription())));


            Log.e("Image Header", IMAGE_BASE_HEADER + header);
        }
    }
}
