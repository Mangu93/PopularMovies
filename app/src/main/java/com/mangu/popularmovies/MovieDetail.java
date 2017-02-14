package com.mangu.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mangu.popularmovies.Adapter.DetailAdapter;
import com.mangu.popularmovies.Adapter.ReviewAdapter;
import com.mangu.popularmovies.Data.FavoritesContract;
import com.mangu.popularmovies.Utilities.NetworkUtilities;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Intent.ACTION_VIEW;
import static com.mangu.popularmovies.BuildConfig.THE_MOVIE_DB_API_TOKEN;

public class MovieDetail extends AppCompatActivity implements DetailAdapter.DetailAdapterOnClickHandler, ReviewAdapter.ReviewAdapterOnClickHandler {
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
    @BindView(R.id.tv_trailers)
    TextView tv_trailers;
    JSONObject movie_json;
    boolean already_favorite = false;
    @BindView(R.id.recyclerview_trailers)
    RecyclerView mRecyclerTrailers;
    @BindView(R.id.recyclerview_reviews)
    RecyclerView mRecyclerReviews;
    int json_id;
    long _id;
    String[] list_of_trailers;
    String[] list_of_headers;
    String[] list_of_reviews;
    DetailAdapter mTrailerAdapter;
    ReviewAdapter mReviewAdapter;

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
        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            try {
                jsonInfo = new JSONObject(intent.getStringExtra(Intent.EXTRA_TEXT));
                if (isNetworkAvailable()) {
                    Picasso.with(getApplicationContext()).load(url_bigger + jsonInfo.getString(getString(R.string.poster_path))).into(image_poster_detail);
                    image_poster_detail.setAdjustViewBounds(true);
                    image_poster_detail.setContentDescription(jsonInfo.getString(getString(R.string.original_title)));
                }
                json_id = (jsonInfo.getInt("id"));
                String info = "\n";
                info = info + (jsonInfo.getString(getString(R.string.release_date))).substring(0, 4) + "\n\n\n";
                info = info + Integer.toString(jsonInfo.getInt(getString(R.string.vote_average))) + "/10";
                main_info.setText(info);
                description.setText(jsonInfo.getString(getString(R.string.overview)));
                this.movie_json = jsonInfo;
            } catch (JSONException e) {
                Log.e(this.getClass().getSimpleName(), e.getMessage());
                e.printStackTrace();
            }
        }
        LinearLayoutManager linearLayout = new LinearLayoutManager(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayoutManager linearLayout2 = new LinearLayoutManager(getApplicationContext());
        linearLayout2.setOrientation(LinearLayout.VERTICAL);
        mRecyclerTrailers.setHasFixedSize(true);
        mRecyclerTrailers.setLayoutManager(linearLayout);
        mTrailerAdapter = new DetailAdapter(this);
        mRecyclerTrailers.setAdapter(mTrailerAdapter);
        mRecyclerReviews.setHasFixedSize(true);
        mRecyclerReviews.setLayoutManager(linearLayout2);
        mReviewAdapter = new ReviewAdapter(this);
        mRecyclerReviews.setAdapter(mReviewAdapter);

        if (isNetworkAvailable()) {
            loadTrailers();
            loadReviews();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        }
        checkFavorite();

    }

    private void loadReviews() {
        new ReviewClass().execute();
    }

    private void loadTrailers() {
        new TrailerClass().execute();

    }

    private void checkFavorite() {

        String column_escaped = DatabaseUtils.sqlEscapeString(movie_json.toString());
        Cursor cursor = getContentResolver().query(FavoritesContract.FavoritesEntry.CONTENT_URI,
                null,
                FavoritesContract.FavoritesEntry.COLUMN_JSON + " = " + column_escaped,
                null,
                null
        );
        if (cursor.getCount() == 1) {
            tv_mark.setText(R.string.remove_favorite);
            already_favorite = true;
            cursor.moveToFirst();
            _id = cursor.getLong(cursor.getColumnIndex(FavoritesContract.FavoritesEntry._ID));
        } else {
            already_favorite = false;
        }
        cursor.close();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void markAsFavorite(View view) {
        if (already_favorite) {
            //Delete
            String str_id = Long.toString(_id);
            Uri uri = FavoritesContract.FavoritesEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(str_id).build();
            getContentResolver().delete(uri, null, null);
            already_favorite = false;
            tv_mark.setText(R.string.mark_favorite);
        } else {
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

    @Override
    public void finish() {
        setResult(1);
        super.finish();
    }

    @Override
    public void onClick(String url) {
        String base_url_youtube = getString(R.string.youtube_watch);
        Uri uri = Uri.parse(base_url_youtube).buildUpon()
                .appendQueryParameter(getString(R.string.youtube_watch_key_parameter),
                        url).build();
        Intent outIntent = new Intent(ACTION_VIEW, uri);
        if(outIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(outIntent);
        }
    }

    private JSONObject getReviewsFromApi() {
        Uri uri;

        JSONObject json = new JSONObject();

        String base_reviews = getString(R.string.reviews_url).replace(
                getString(R.string.movie_id_replace), Integer.toString(json_id));
        uri = Uri.parse(base_reviews).buildUpon()
                .appendQueryParameter(getString(R.string.api_key), THE_MOVIE_DB_API_TOKEN)
                .build();
        try {
            json = new JSONObject(NetworkUtilities.getResponseFromHttpUrl(new URL(uri.toString())));
        } catch (IOException | JSONException malformed) {
            Log.e(malformed.toString(), malformed.getMessage());
            malformed.printStackTrace();
        }
        return json;
    }

    private JSONObject getTrailerJsonFromApi() {
        Uri uri;

        JSONObject json = new JSONObject();

        String base_trailers = getString(R.string.trailers_url).replace(
                getString(R.string.movie_id_replace), Integer.toString(json_id));
        uri = Uri.parse(base_trailers).buildUpon()
                .appendQueryParameter(getString(R.string.api_key), THE_MOVIE_DB_API_TOKEN)
                .build();
        try {
            json = new JSONObject(NetworkUtilities.getResponseFromHttpUrl(new URL(uri.toString())));
        } catch (IOException | JSONException malformed) {
            Log.e(malformed.toString(), malformed.getMessage());
            malformed.printStackTrace();
        }
        return json;
    }

    @Override
    public void onClickReview(String url) {

    }

    public class ReviewClass extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject json_reviews = getReviewsFromApi();
            if (json_reviews != null) {
                try {
                    JSONArray jsonArray = json_reviews.getJSONArray("results");
                    list_of_reviews = new String[jsonArray.length()];
                    for (int index = 0; index < jsonArray.length(); index++) {
                        JSONObject leaf = jsonArray.getJSONObject(index);
                        list_of_reviews[index] = leaf.getString("content");
                    }
                } catch (JSONException e) {
                    Log.e(MovieDetail.class.getSimpleName(), e.getMessage());
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (list_of_reviews != null) {
                mReviewAdapter.setListOfReviews(list_of_reviews);
            }
        }
    }

    public class TrailerClass extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject json_trailers = getTrailerJsonFromApi();
            if (json_trailers != null) {
                try {
                    JSONArray jsonArray = json_trailers.getJSONArray("results");
                    list_of_headers = new String[jsonArray.length()];
                    list_of_trailers = new String[jsonArray.length()];
                    for (int index = 0; index < jsonArray.length(); index++) {
                        JSONObject leaf = jsonArray.getJSONObject(index);
                        list_of_trailers[index] = leaf.getString("key");
                        list_of_headers[index] = leaf.getString("name");
                    }
                } catch (JSONException e) {
                    Log.e(MovieDetail.class.getSimpleName(), e.getMessage());
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (list_of_trailers != null && list_of_headers != null) {
                mTrailerAdapter.setListHeaders(list_of_headers);
                mTrailerAdapter.setTrailerList(list_of_trailers);
            }
        }
    }
}
