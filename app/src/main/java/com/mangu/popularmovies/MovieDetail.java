package com.mangu.popularmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
    @BindString(R.string.url_poster_bigger) String url_bigger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        JSONObject jsonInfo = new JSONObject();
        Intent intent = getIntent();
        if(intent.hasExtra(Intent.EXTRA_TEXT)) {
            try {
                jsonInfo = new JSONObject(intent.getStringExtra(Intent.EXTRA_TEXT));
                Picasso.with(getApplicationContext()).load(url_bigger+jsonInfo.getString("poster_path")).into(image_poster_detail);
                image_poster_detail.setAdjustViewBounds(true);
                image_poster_detail.setContentDescription(jsonInfo.getString("original_title"));
                String info = "\n";
                info = info + (jsonInfo.getString("release_date")).substring(0,4) + "\n\n\n";
                info = info + Integer.toString(jsonInfo.getInt("vote_average")) + "/10";
                main_info.setText(info);
                description.setText(jsonInfo.getString("overview"));
            } catch (JSONException e) {
                Log.e(this.getClass().getSimpleName(), e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
