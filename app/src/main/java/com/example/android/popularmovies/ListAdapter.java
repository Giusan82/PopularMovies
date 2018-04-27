package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.popularmovies.utilities.MoviesData;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {
    private static final String LOG_TAG = ListAdapter.class.getSimpleName();
    private static final String IMAGE_BASE_URL = "https://img.youtube.com/vi/"; //source: https://stackoverflow.com/a/2068371
    private static final String DEFAULT_IMAGE_KEY = "default.jpg";
    private static final String YOUTUBE_URL = "http://www.youtube.com/watch?v=";
    private static final String YOUTUBE_APP_URI = "vnd.youtube:";
    private Context mContext;
    private ArrayList<MoviesData> mMoviesData;
    private boolean isReviews;

    /**
     * Constructor
     */
    public ListAdapter(Context context, ArrayList<MoviesData> items, boolean is_reviews) {
        this.mContext = context;
        this.mMoviesData = items;
        this.isReviews = is_reviews;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId;
        if (isReviews) {
            layoutId = R.layout.list_reviews;
        } else {
            layoutId = R.layout.list_videos;
        }

        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        ListViewHolder viewHolder = new ListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, int position) {
        final MoviesData current = mMoviesData.get(position);
        if (isReviews) {
            holder.tv_author.setText(current.getAuthorReview());
            holder.tv_content_review.setText(current.getContent_Review());
        } else {
            holder.tv_video_name.setText(current.getVideoName());
            holder.tv_video_type.setText(mContext.getString(R.string.video_type, current.getVideoType()));
            String imageUrl = builderUrl(current.getVideoKey()).toString();
            Glide.with(mContext).load(imageUrl).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    holder.tv_video_name.setVisibility(View.VISIBLE);
                    holder.tv_video_type.setVisibility(View.VISIBLE);
                    return false;
                }
            }).crossFade().dontTransform().into(holder.iv_video_preview);
        }
        //here the views are not recycled. It avoids to see the previous image on the next view during the loading.
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return mMoviesData.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_author;
        public TextView tv_content_review;
        public ImageView iv_video_preview;
        public TextView tv_video_name;
        public TextView tv_video_type;
        public View list_container;

        public ListViewHolder(View itemView) {
            super(itemView);
            this.tv_author = itemView.findViewById(R.id.tv_author);
            this.tv_content_review = itemView.findViewById(R.id.tv_content_review);
            this.iv_video_preview = itemView.findViewById(R.id.iv_video_preview);
            this.tv_video_name = itemView.findViewById(R.id.tv_video_name);
            this.tv_video_type = itemView.findViewById(R.id.tv_video_type);
            this.list_container = itemView.findViewById(R.id.video_container);
            if (!isReviews) {
                this.list_container.setOnClickListener(mViewListener);
            }
        }

        private View.OnClickListener mViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                MoviesData current = mMoviesData.get(position);
                //this open a youtube video
                Intent app = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_APP_URI + current.getVideoKey())); //source: https://stackoverflow.com/a/12439378
                Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_URL + current.getVideoKey()));
                try {
                    mContext.startActivity(app);
                } catch (ActivityNotFoundException e) {
                    mContext.startActivity(web);
                    Log.e(LOG_TAG, "Youtube app not found, message: " + e);
                }
            }
        };
    }

    //this builds the image url
    private URL builderUrl(String id) {
        Uri.Builder builtUri;
        builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon();
        builtUri.appendPath(id)
                .appendPath(DEFAULT_IMAGE_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
