package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.utilities.MoviesList;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {
    public static final String EXTRA_MOVIE = "extra_movie";
    private static final String IMAGE_BASE_HEADER = "http://image.tmdb.org/t/p/original/";
    private static final String IMAGE_BASE_POSTER = "http://image.tmdb.org/t/p/w92/";

    private static final String SEPARATOR_LIST = ", ";
    private static final String END_LIST = ".";

    private TextView mOriginalTitle;
    private TextView mDescription;
    private TextView mRelease_Date;
    private TextView mGenres;
    private TextView mRatingValue;
    private ImageView mHeader;
    private ImageView mPoster;
    private MoviesList mMovie;
    private RatingBar mRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mOriginalTitle = findViewById(R.id.tv_original_title);
        mDescription = findViewById(R.id.tv_description);
        mRelease_Date = findViewById(R.id.tv_release_date);
        mRatingValue = findViewById(R.id.tv_rating);

        mGenres = findViewById(R.id.tv_genres);
        mHeader = findViewById(R.id.iv_header);
        mPoster = findViewById(R.id.iv_poster);

        mRatingBar = findViewById(R.id.ratingBar);

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
            mRelease_Date.setText(Html.fromHtml(getString(R.string.release_date, mMovie.getRelease_date())));
            ArrayList<String> genresListIDs = mMovie.getGenre_ids();
            mGenres.setText(Html.fromHtml(getString(R.string.genres, formatList(genresListIDs))));
            mRatingValue.setText(String.valueOf(mMovie.getVote_Average()));
            mRatingBar.setRating((float)mMovie.getVote_Average()*5/10);


            Log.e("Image Header", IMAGE_BASE_HEADER + header);
        }
    }
    private String formatList(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            builder.append(mMovie.getGenre(this, list.get(i)));
            builder.append(SEPARATOR_LIST);
        }
        String final_string = builder.toString();
        final_string = final_string.substring(0, final_string.lastIndexOf(SEPARATOR_LIST)) + END_LIST;
        return final_string;
    }
}
