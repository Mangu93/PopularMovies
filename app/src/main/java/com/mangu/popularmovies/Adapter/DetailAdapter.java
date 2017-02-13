package com.mangu.popularmovies.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mangu.popularmovies.R;


public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.DetailViewHolder> {

    private final DetailAdapterOnClickHandler mClickHandler;
    //This has the List of url's: Url 1, then Url 2, etc
    private String[] list_of_trailers;
    //This has the list of header for each video: Trailer 1, then Trailer 2, etc
    private String[] list_of_headers;

    public DetailAdapter(DetailAdapterOnClickHandler ClickHandler) {
        mClickHandler = ClickHandler;
    }
    public void setTrailerList(String[] trailerList) {
        this.list_of_trailers = trailerList;
        notifyDataSetChanged();
    }
    public void setListHeaders(String[] headers) {
        this.list_of_headers = headers;
        notifyDataSetChanged();
    }
    @Override
    public DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForItem = R.layout.movie_detail_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForItem, parent, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailViewHolder holder, int position) {
        holder.text_from_api.setText(list_of_headers[position]);
        holder.url_trailer = list_of_trailers[position];
    }

    @Override
    public int getItemCount() {
        if(this.list_of_headers == null) return 0;
        else return list_of_headers.length;
    }

    public interface DetailAdapterOnClickHandler {
        void onClick(String url);
    }

    class DetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text_from_api;
        private String url_trailer;
        DetailViewHolder(View itemView) {
            super(itemView);
            text_from_api = (TextView) itemView.findViewById(R.id.tv_movies_trailer);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickHandler.onClick(url_trailer);
        }
    }
}
