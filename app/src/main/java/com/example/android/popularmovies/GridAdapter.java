package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.popularmovies.utilities.MoviesData;

import java.util.ArrayList;


public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridViewHolder> {
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342/";
    private static final String NULL_VALUE = "null";
    private Context mContext;
    private ArrayList<MoviesData> mMoviesData;

    /**
     * Constructor
     */
    public GridAdapter(Context context, ArrayList<MoviesData> items) {
        mContext = context;
        mMoviesData = items;
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int gridId = R.layout.grid;
        View view = LayoutInflater.from(mContext).inflate(gridId, parent, false);
        GridViewHolder viewHolder = new GridViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final GridViewHolder holder, int position) {
        final MoviesData current = mMoviesData.get(position);

        holder.tv_title.setText(current.getTitle());
        holder.rb_vote_average.setRating((float) current.getVote_Average() * 5 / 10);
        holder.tv_vote_average.setText(String.valueOf(current.getVote_Average()));

        if (!current.getPoster_Path().equals(NULL_VALUE)) {
            String imageUrl = IMAGE_BASE_URL + current.getPoster_Path();
            Glide.with(mContext).load(imageUrl).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    holder.tv_title.setVisibility(View.VISIBLE);
                    holder.tv_vote_average.setVisibility(View.VISIBLE);
                    holder.rb_vote_average.setVisibility(View.VISIBLE);
                    return false;
                }
            }).crossFade().dontTransform().into(holder.iv_poster);
        } else {
            holder.tv_title.setVisibility(View.VISIBLE);
            holder.tv_vote_average.setVisibility(View.VISIBLE);
            holder.rb_vote_average.setVisibility(View.VISIBLE);
        }
        //here the views are not recycled. It avoids to see the previous image on the next view during the loading.
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return mMoviesData.size();
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_poster;
        public TextView tv_title;
        public RatingBar rb_vote_average;
        public TextView tv_vote_average;
        public View list_container;

        public GridViewHolder(View itemView) {
            super(itemView);
            this.iv_poster = itemView.findViewById(R.id.iv_poster);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.rb_vote_average = itemView.findViewById(R.id.ratingBar);
            this.tv_vote_average = itemView.findViewById(R.id.tv_vote_average);
            this.list_container = itemView.findViewById(R.id.list_container);
            list_container.setOnClickListener(mViewListener);
        }

        private View.OnClickListener mViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                MoviesData current = mMoviesData.get(position);
                //this open the DetailActivity
                Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra(DetailsActivity.EXTRA_MOVIE_ID, current.getID());
                intent.putExtra(DetailsActivity.EXTRA_MOVIE_TITLE, current.getTitle());
                mContext.startActivity(intent);
            }
        };
    }

}
