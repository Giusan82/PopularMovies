package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.utilities.MoviesList;

import java.util.ArrayList;


public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridViewHolder> {
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342/";
    private static final String NULL_VALUE = "null";
    private Context mContext;
    private ArrayList<MoviesList> mMoviesList;

    /**
     * Constructor
     */
    public GridAdapter(Context context, ArrayList<MoviesList> items) {
        mContext = context;
        mMoviesList = items;
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int gridId = R.layout.grid;
        View view = LayoutInflater.from(mContext).inflate(gridId, parent, false);
        GridViewHolder viewHolder = new GridViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {
        final MoviesList current = mMoviesList.get(position);
        if (!current.getPoster_Path().equals(NULL_VALUE)) {
            String imageUrl = IMAGE_BASE_URL + current.getPoster_Path();
            Glide.with(mContext).load(imageUrl).crossFade().dontTransform().into(holder.iv_poster);
        }
        holder.tv_title.setText(current.getTitle());
        holder.tv_vote_average.setText(String.valueOf(current.getVote_Average()));
        //here the views are not recycled. It avoids to see the previous image on the next view during the loading.
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_poster;
        public TextView tv_title;
        public TextView tv_vote_average;
        public View list_container;

        public GridViewHolder(View itemView) {
            super(itemView);
            this.iv_poster = itemView.findViewById(R.id.iv_poster);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.tv_vote_average = itemView.findViewById(R.id.tv_vote_average);
            this.list_container = itemView.findViewById(R.id.list_container);
            list_container.setOnClickListener(mViewListener);
        }

        private View.OnClickListener mViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                MoviesList current = mMoviesList.get(position);
                //this open the DetailActivity
                Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra(DetailsActivity.EXTRA_MOVIE_ID, current.getID());
                intent.putExtra(DetailsActivity.EXTRA_MOVIE_TITLE, current.getTitle());
                mContext.startActivity(intent);
            }
        };
    }

}
