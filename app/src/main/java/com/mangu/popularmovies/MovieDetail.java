package com.mangu.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetail extends AppCompatActivity {
    @BindView(R.id.image_poster_detail)
    ImageView image_poster_detail;
    @BindView(R.id.tv_main_info)
    TextView main_info;
    @BindView(R.id.tv_description)
    TextView description;
    @BindString(R.string.url_poster_bigger)
    String url_bigger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        JSONObject jsonInfo;
        Intent intent = getIntent();
        if(intent.hasExtra(Intent.EXTRA_TEXT)) {
            try {
                jsonInfo = new JSONObject(intent.getStringExtra(Intent.EXTRA_TEXT));
                if(isNetworkAvailable()) {
                    Picasso.with(getApplicationContext()).load(url_bigger + jsonInfo.getString(getString(R.string.poster_path))).into(image_poster_detail);
                    image_poster_detail.setAdjustViewBounds(true);
                    image_poster_detail.setContentDescription(jsonInfo.getString(getString(R.string.original_title)));
                }
                String info = "\n";
                info = info + (jsonInfo.getString(getString(R.string.release_date))).substring(0,4) + "\n\n\n";
                info = info + Integer.toString(jsonInfo.getInt(getString(R.string.vote_average))) + "/10";
                main_info.setText(info);
                description.setText(jsonInfo.getString(getString(R.string.overview)));
            } catch (JSONException e) {
                Log.e(this.getClass().getSimpleName(), e.getMessage());
                e.printStackTrace();
            }
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
