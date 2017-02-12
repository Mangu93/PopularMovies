package com.mangu.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mangu.popularmovies.Data.FavoritesContract;
import com.mangu.popularmovies.Data.FavoritesDbHelper;
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
    @BindView(R.id.bt_mark)
    Button tv_mark;
    JSONObject movie_json;
    boolean already_favorite = false;
    long _id;
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
                this.movie_json = jsonInfo;
            } catch (JSONException e) {
                Log.e(this.getClass().getSimpleName(), e.getMessage());
                e.printStackTrace();
            }
        }
        checkFavorite();
    }

    private void checkFavorite() {
        String query = "SELECT  *" +
                " FROM " + FavoritesContract.FavoritesEntry.TABLE_NAME +
                " WHERE " + FavoritesContract.FavoritesEntry.COLUMN_JSON +
                " = '" +movie_json.toString() + "'";
        FavoritesDbHelper mFavoritesDbHelper = new FavoritesDbHelper(getApplicationContext());
        Cursor cursor = mFavoritesDbHelper.getReadableDatabase().rawQuery(query, null);
        if(cursor.getCount() == 1) {
            tv_mark.setText(R.string.remove_favorite);
            already_favorite = true;
            cursor.moveToFirst();
            _id = cursor.getLong(cursor.getColumnIndex(FavoritesContract.FavoritesEntry._ID));
        }else {
            already_favorite = false;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void markAsFavorite(View view) {
        if(already_favorite) {
            //Delete
            String str_id = Long.toString(_id);
            Uri uri = FavoritesContract.FavoritesEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(str_id).build();
            getContentResolver().delete(uri, null, null);
            already_favorite = false;
            tv_mark.setText(R.string.mark_favorite);
        }else {
            //Insert
            ContentValues contentValues = new ContentValues();
            contentValues.put(FavoritesContract.FavoritesEntry.COLUMN_JSON, this.movie_json.toString());

            Uri uri = getContentResolver().insert(FavoritesContract.FavoritesEntry.CONTENT_URI, contentValues);

            tv_mark.setText(R.string.remove_favorite);
            already_favorite = true;
            if (uri != null) {
                Toast.makeText(getApplicationContext(), "Marked as favorite", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
