package com.example.android.popularmovies.utilities;


import android.content.Context;

import com.example.android.popularmovies.R;

import java.io.Serializable;
import java.util.ArrayList;

public class MoviesList implements Serializable {

    private int mPage; //Page
    private int mTotal_Page;
    private int mTotal_results;
    private int mID;
    private String mTitle; //this is a title of picture
    private String mOriginal_title;
    private String mOriginal_language;
    private String mDescription;
    private String mPoster_Path;
    private String mBackground_Path;
    private ArrayList<String> mGenre_ids;
    private int mVote_Count;
    private double mVote_Average;
    private double mPopularity;
    private String mRelease_date;
    private boolean mHasVideo;
    private boolean mIsAdult;

    /**
     * Constructor
     */
    public MoviesList(){}
    public MoviesList(int page,
                      int total_page,
                      int total_results,
                      int id,
                      String title,
                      String original_title,
                      String original_language,
                      String description,
                      String poster_path,
                      String background_path,
                      ArrayList<String> genre_ids,
                      int vote_count,
                      double vote_average,
                      double popularity,
                      String release_date,
                      boolean hasVideo,
                      boolean isAdult)
    {   this.mPage = page;
        this.mTotal_Page = total_page;
        this.mTotal_results = total_results;
        this.mID = id;
        this.mTitle = title;
        this.mOriginal_title = original_title;
        this.mOriginal_language = original_language;
        this.mDescription = description;
        this.mPoster_Path = poster_path;
        this.mBackground_Path = background_path;
        this.mGenre_ids = genre_ids;
        this.mVote_Count = vote_count;
        this.mVote_Average = vote_average;
        this.mPopularity = popularity;
        this.mRelease_date = release_date;
        this.mHasVideo = hasVideo;
        this.mIsAdult = isAdult;
    }

    /**
     * Getter
     **/
    public int getPage(){return mPage;}

    public int getTotal_Page(){return mTotal_Page;}

    public int getTotal_results(){return mTotal_results;}

    public int getID() {
        return mID;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getOriginal_title() {
        return mOriginal_title;
    }

    public String getOriginal_language() {
        return mOriginal_language;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getPoster_Path() {
        return mPoster_Path;
    }

    public String getBackground_Path() {
        return mBackground_Path;
    }

    public ArrayList<String> getGenre_ids() {
        return mGenre_ids;
    }

    public String getGenre(Context context, String id){
        String genre = "";
        if (id.equals(context.getString(R.string.settings_Action_value))){
            genre = context.getString(R.string.settings_Action_label);
        }
        if (id.equals(context.getString(R.string.settings_Adventure_value))){
            genre = context.getString(R.string.settings_Adventure_label);
        }
        if (id.equals(context.getString(R.string.settings_Animation_value))){
            genre = context.getString(R.string.settings_Animation_label);
        }
        if (id.equals(context.getString(R.string.settings_Comedy_value))){
            genre = context.getString(R.string.settings_Comedy_label);
        }
        if (id.equals(context.getString(R.string.settings_Crime_value))){
            genre = context.getString(R.string.settings_Crime_label);
        }
        if (id.equals(context.getString(R.string.settings_Documentary_value))){
            genre = context.getString(R.string.settings_Documentary_label);
        }
        if (id.equals(context.getString(R.string.settings_Drama_value))){
            genre = context.getString(R.string.settings_Drama_label);
        }
        if (id.equals(context.getString(R.string.settings_Family_value))){
            genre = context.getString(R.string.settings_Family_label);
        }
        if (id.equals(context.getString(R.string.settings_Fantasy_value))){
            genre = context.getString(R.string.settings_Family_label);
        }
        if (id.equals(context.getString(R.string.settings_History_value))){
            genre = context.getString(R.string.settings_History_label);
        }
        if (id.equals(context.getString(R.string.settings_Horror_value))){
            genre = context.getString(R.string.settings_Horror_label);
        }
        if (id.equals(context.getString(R.string.settings_Music_value))){
            genre = context.getString(R.string.settings_Music_label);
        }
        if (id.equals(context.getString(R.string.settings_Mystery_value))){
            genre = context.getString(R.string.settings_Mystery_label);
        }
        if (id.equals(context.getString(R.string.settings_Romance_value))){
            genre = context.getString(R.string.settings_Romance_label);
        }
        if (id.equals(context.getString(R.string.settings_Science_Fiction_value))){
            genre = context.getString(R.string.settings_Science_Fiction_label);
        }
        if (id.equals(context.getString(R.string.settings_TV_Movie_value))){
            genre = context.getString(R.string.settings_TV_Movie_label);
        }
        if (id.equals(context.getString(R.string.settings_Thriller_value))){
            genre = context.getString(R.string.settings_Thriller_label);
        }
        if (id.equals(context.getString(R.string.settings_War_value))){
            genre = context.getString(R.string.settings_War_label);
        }
        if (id.equals(context.getString(R.string.settings_Western_value))){
            genre = context.getString(R.string.settings_Western_label);
        }
        return genre;
    }

    public int getVote_Count() {
        return mVote_Count;
    }

    public double getVote_Average(){return mVote_Average;}

    public double getPopularity(){return mPopularity;}

    public String getRelease_date(){return mRelease_date;}

    public boolean getHasVideo(){return mHasVideo;}

    public boolean getIsAdult(){return mIsAdult;}
}
