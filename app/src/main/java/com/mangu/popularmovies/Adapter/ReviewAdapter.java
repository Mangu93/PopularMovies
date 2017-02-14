package com.mangu.popularmovies.Adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mangu.popularmovies.R;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final ReviewAdapterOnClickHandler mClickHandler;

    private String[] list_of_reviews;

    public ReviewAdapter(ReviewAdapterOnClickHandler ClickHandler) {
        mClickHandler = ClickHandler;
    }

    public void setListOfReviews(String [] reviews) {
        list_of_reviews = reviews;
        notifyDataSetChanged();
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForItem = R.layout.movie_review_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForItem, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.textReview.setText(list_of_reviews[position].replace("\n", ""));
    }

    @Override
    public int getItemCount() {
        if(list_of_reviews == null) return 0;
        else return list_of_reviews.length;
    }


    public interface ReviewAdapterOnClickHandler {
        void onClickReview(String url);
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView textReview;
        public ReviewViewHolder(View itemView) {
            super(itemView);
            textReview = (TextView) itemView.findViewById(R.id.item_review);
        }

    }

}

