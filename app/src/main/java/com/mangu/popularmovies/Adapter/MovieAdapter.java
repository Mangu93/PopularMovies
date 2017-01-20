package com.mangu.popularmovies.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.mangu.popularmovies.R;

/**
 * Created by Adrian Portillo on 20/01/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private Bitmap[] listOfImages;

    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(String title);
    }
    public MovieAdapter(MovieAdapterOnClickHandler ClickHandler) {
        mClickHandler = ClickHandler;
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
        Bitmap poster = listOfImages[position];
        holder.image_view_poster.setImageBitmap(poster);
        holder.image_view_poster.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        if(this.listOfImages == null) return 0;
        return this.listOfImages.length;
    }

    public void setImageData(Bitmap[] imageData) {
        this.listOfImages = imageData;
        notifyDataSetChanged();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView image_view_poster;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            image_view_poster = (ImageView) itemView.findViewById(R.id.image_view_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.i(this.getClass().getName(), "Clicked on poster");
            Toast.makeText(view.getContext(), "Clicked on poster "+image_view_poster.getHeight(), Toast.LENGTH_SHORT).show();
        }
    }
}
