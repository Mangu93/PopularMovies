package com.mangu.popularmovies.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mangu.popularmovies.R;

/**
 * Created by Adrian Portillo on 20/01/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private ImageView[] listOfImages;

    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(String title);
    }
    public MovieAdapter(MovieAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForItem, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        ImageView poster = listOfImages[position];
        holder.image_view_poster.setImageBitmap(poster.getDrawingCache());
    }

    @Override
    public int getItemCount() {
        if(this.listOfImages == null) return 0;
        return this.listOfImages.length;
    }
    public void setImageData(ImageView [] imageData) {
        this.listOfImages = imageData;
        notifyDataSetChanged();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView image_view_poster;
        private String movie_title;
        private String url;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            image_view_poster = (ImageView) itemView.findViewById(R.id.image_view_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.i(this.getClass().getName(), "Clicked on poster");
        }
    }
}
