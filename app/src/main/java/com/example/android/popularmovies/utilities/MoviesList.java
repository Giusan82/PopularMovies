package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.util.Log;

import com.example.android.popularmovies.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MoviesList{
    public static final String LOG_TAG = MoviesList.class.getName();
    private static final String SEPARATOR_LIST = ", ";
    private static final String END_LIST = ".";
    private Context mContext;
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
    private ArrayList<String> mGenres;
    private int mVote_Count;
    private double mVote_Average;
    private double mPopularity;
    private String mRelease_date;
    private boolean mHasVideo;
    private boolean mIsAdult;
    private ArrayList<String> mProduction_Companies;
    private ArrayList<String> mProduction_Countries;
    private int mRuntime;
    private ArrayList<String> mSpoken_Languages;
    private String mStatus;
    private int mNumberOfSeasons;
    private int mNumberOfEpisodes;

    /**
     * Constructor
     */
    public MoviesList() {
    }

    public MoviesList(Context context,
                      int page,
                      int total_page,
                      int total_results,
                      int id,
                      String title,
                      String poster_path,
                      double vote_average) {
        this.mContext = context;
        this.mPage = page;
        this.mTotal_Page = total_page;
        this.mTotal_results = total_results;
        this.mID = id;
        this.mTitle = title;
        this.mPoster_Path = poster_path;
        this.mVote_Average = vote_average;
    }

    public MoviesList(Context context,
                      int page,
                      int total_page,
                      int total_results,
                      int id,
                      String title,
                      String original_title,
                      String original_language,
                      String description,
                      String poster_path,
                      String background_path,
                      ArrayList<String> genres,
                      int vote_count,
                      double vote_average,
                      double popularity,
                      String release_date,
                      boolean hasVideo,
                      boolean isAdult,
                      ArrayList<String> production_companies,
                      ArrayList<String> production_countries,
                      int runtime,
                      ArrayList<String> spoken_languages,
                      String status,
                      int seasons,
                      int episodes) {
        this.mContext = context;
        this.mPage = page;
        this.mTotal_Page = total_page;
        this.mTotal_results = total_results;
        this.mID = id;
        this.mTitle = title;
        this.mOriginal_title = original_title;
        this.mOriginal_language = original_language;
        this.mDescription = description;
        this.mPoster_Path = poster_path;
        this.mBackground_Path = background_path;
        this.mGenres = genres;
        this.mVote_Count = vote_count;
        this.mVote_Average = vote_average;
        this.mPopularity = popularity;
        this.mRelease_date = release_date;
        this.mHasVideo = hasVideo;
        this.mIsAdult = isAdult;
        this.mProduction_Companies = production_companies;
        this.mProduction_Countries = production_countries;
        this.mRuntime = runtime;
        this.mSpoken_Languages = spoken_languages;
        this.mStatus = status;
        this.mNumberOfSeasons = seasons;
        this.mNumberOfEpisodes = episodes;
    }

    /**
     * Getter
     **/
    public int getPage() {
        return mPage;
    }

    public int getTotal_Page() {
        return mTotal_Page;
    }

    public int getTotal_results() {
        return mTotal_results;
    }

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

    public String getGenres() {
        return formatList(mGenres);
    }

    public String getProductionCompanies() {
        return formatList(mProduction_Companies);
    }

    public String getProductionCountries() {
        return formatList(mProduction_Countries);
    }

    public int getVote_Count() {
        return mVote_Count;
    }

    public double getVote_Average() {
        return mVote_Average;
    }

    public double getPopularity() {
        return mPopularity;
    }

    public String getRelease_date() {
        return formatDate(mRelease_date);
    }

    public boolean getHasVideo() {
        return mHasVideo;
    }

    public boolean getIsAdult() {
        return mIsAdult;
    }

    public int getRuntime() {
        return mRuntime;
    }

    public String getSpokenLanguages() {
        return formatList(mSpoken_Languages);
    }

    public String getStatus() {
        return mStatus;
    }

    public int getNumberOfSeasons() {
        return mNumberOfSeasons;
    }

    public int getNumberOfEpisodes() {
        return mNumberOfEpisodes;
    }

    //for getting the date conversion
    public String formatDate(String date) {
        String newFormatData = "";
        if (date.length() >= 10) {
            // Splits the string after 10 char, because the date obtained from server is like this "2017-07-15T21:30:35Z", so this method will give 2017-07-15
            CharSequence splittedDate = date.subSequence(0, 10);
            try {
                Date formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(splittedDate.toString());
                newFormatData = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(formatDate);
            } catch (ParseException e) {
                Log.e(LOG_TAG + " -> formatDate", e.getMessage());
            }
        } else {
            newFormatData = date;
        }
        return newFormatData;
    }

    private String formatList(List<String> list) {
        if (list.size() != 0) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                builder.append(list.get(i));
                builder.append(SEPARATOR_LIST);
            }
            String final_string = builder.toString();
            final_string = final_string.substring(0, final_string.lastIndexOf(SEPARATOR_LIST)) + END_LIST;
            return final_string;
        } else {
            return mContext.getString(R.string.unknown);
        }
    }
}
