package com.mangu.popularmovies.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mangu.popularmovies.R;

import org.json.JSONArray;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private final MovieAdapterOnClickHandler mClickHandler;
    private Bitmap[] listOfImages;
    private int counter = 0;
    private JSONArray listOfJSON;

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
        holder.image_view_poster.setAdjustViewBounds(true);
    }

    @Override
    public int getItemCount() {
        if (this.listOfImages == null) return 0;
        return this.listOfImages.length;
    }

    public void setImageData(Bitmap[] imageData) {
        this.listOfImages = imageData;
        notifyDataSetChanged();
    }

    public void setJSONData(JSONArray jsonData) {
        this.listOfJSON = jsonData;
        notifyDataSetChanged();
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(String title);
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView image_view_poster;
        private int anInt;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            image_view_poster = (ImageView) itemView.findViewById(R.id.image_view_poster);
            itemView.setOnClickListener(this);
            anInt = counter;
            counter++;
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(Integer.toString(adapterPosition));
        }
    }
}
