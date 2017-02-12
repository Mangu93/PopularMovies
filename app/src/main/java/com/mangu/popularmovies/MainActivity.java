package com.mangu.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mangu.popularmovies.Adapter.MovieAdapter;
import com.mangu.popularmovies.Data.FavoritesContract;
import com.mangu.popularmovies.Utilities.NetworkUtilities;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mangu.popularmovies.BuildConfig.THE_MOVIE_DB_API_TOKEN;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int REQUEST_CODE = 567;
    private String order_by = "rates"; //default is rating
    private Menu menuSettings;
    @BindView(R.id.recyclerview_movies)
    RecyclerView mRecyclerView;
    private MovieAdapter movieAdapter;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.tv_error)
    TextView mTvError;
    private ImageView poster_movie;
    private boolean has_favorites = false;
    @BindString(R.string.base_url_poster_tmdb)
    String base_url_poster_tmdb;
    @BindString(R.string.url_poster_bigger)
    String url_bigger;
    private ImageView[] array_images;
    private String[] array_images_url;
    private Bitmap[] array_bitmap;
    private JSONArray array_json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        order_by = getString(R.string.rates);
        poster_movie = new ImageView(getApplicationContext());
        array_images = new ImageView[20];
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        movieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(movieAdapter);

        loadPosters();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void loadPosters() {
        showPosterDataView();
        if (isNetworkAvailable()) {
            new PosterFetcher(this.getApplicationContext(), this.poster_movie).execute();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
            showErrorInternet();
        }
    }

    private void showPosterDataView() {
        mTvError.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showError() {
        mTvError.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void showErrorInternet() {
        mTvError.setVisibility(View.VISIBLE);
        mTvError.setText(getString(R.string.no_connection));
        mRecyclerView.setVisibility(View.INVISIBLE);
    }


    private void loadFavorites() {
        showPosterDataView();
        new FavoritesFetcher(this.getApplicationContext(), this.poster_movie).execute();
    }

    public class FavoritesFetcher extends AsyncTask<Void, Integer, Void> {
        Context context;
        ImageView imageView;

        FavoritesFetcher(Context context, ImageView imageView) {
            this.context = context;
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (order_by == null) {
                finish();
            } else {
                Cursor cursorFavorites = getContentResolver().query(FavoritesContract.FavoritesEntry.CONTENT_URI,
                        null, null, null, null);

                if (cursorFavorites.moveToFirst()) {
                    array_images_url = new String[cursorFavorites.getCount()];
                    array_images = new ImageView[cursorFavorites.getCount()];
                    array_bitmap = new Bitmap[cursorFavorites.getCount()];
                    array_json = new JSONArray();
                    do {
                        String data_json = cursorFavorites.getString(
                                cursorFavorites.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_JSON));
                        try {
                            //TODO Form in json_array a json array with all json's?
                            int position = cursorFavorites.getPosition();
                            JSONObject movie = new JSONObject(data_json);
                            String poster_path = movie.getString(getString(R.string.poster_path));
                            array_images_url[position] = base_url_poster_tmdb + poster_path;
                            Bitmap bitmap = Picasso.with(getApplicationContext()).load(array_images_url[position]).get();
                            Log.i(TAG, getString(R.string.Height) + bitmap.getHeight() + getString(R.string.Width) + bitmap.getWidth());
                            array_bitmap[position] = bitmap;
                            array_json.put(movie);
                        } catch (JSONException | IOException ex) {
                            Log.e(TAG, ex.getMessage());
                            ex.printStackTrace();
                            return null;
                        }
                    } while (cursorFavorites.moveToNext());
                    has_favorites = true;
                }else {

                    has_favorites = false;
                }
                cursorFavorites.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (has_favorites) {
                showPosterDataView();
                for (int i = 0; i < array_images_url.length; i++) {
                    array_images[i] = new ImageView(getApplicationContext());
                    array_images[i].setImageBitmap(array_bitmap[i]);
                }
                movieAdapter.setImageData(array_bitmap);
                movieAdapter.setJSONData(array_json);
            } else {
                    showNoFavorites();

            }
        }
    }

    public class PosterFetcher extends AsyncTask<Void, Integer, Void> {
        Context context;
        ImageView imageView;

        PosterFetcher(Context context, ImageView imageView) {
            this.context = context;
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (order_by == null) {
                finish();
            } else {

                String popular = getResources().getString(R.string.popular_movie_url);
                String rates = getResources().getString(R.string.top_rated_url);
                JSONObject json_data = getJSONfromAPI(order_by, popular, rates);
                if (json_data.length() != 0) {
                    try {
                        JSONArray array_movies = json_data.getJSONArray(getString(R.string.results));
                        array_json = array_movies;
                        array_images_url = new String[array_movies.length()];
                        array_images = new ImageView[array_movies.length()];
                        array_bitmap = new Bitmap[array_movies.length()];
                        for (int position = 0; position < array_movies.length(); position++) {

                            JSONObject movie = array_movies.getJSONObject(position);
                            String poster_path = movie.getString(getString(R.string.poster_path));
                            array_images_url[position] = base_url_poster_tmdb + poster_path;
                            try {
                                Bitmap bitmap = Picasso.with(getApplicationContext()).load(array_images_url[position]).get();
                                Log.i(TAG, getString(R.string.Height) + bitmap.getHeight() + getString(R.string.Width) + bitmap.getWidth());
                                array_bitmap[position] = bitmap;
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(e.getClass().getSimpleName(), e.getMessage());
                    }
                }

            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (array_images != null) {
                showPosterDataView();
                for (int i = 0; i < array_images_url.length; i++) {
                    array_images[i] = new ImageView(getApplicationContext());
                    array_images[i].setImageBitmap(array_bitmap[i]);
                }
                movieAdapter.setImageData(array_bitmap);
                movieAdapter.setJSONData(array_json);
            } else {
                if(has_favorites) {
                    showError();
                }else{
                    showNoFavorites();
                }
            }
        }
    }

    private void showNoFavorites() {
        mTvError.setVisibility(View.VISIBLE);
        mTvError.setText(R.string.no_favorites);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menuSettings = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.highest_rated:
                menuSettings.findItem(R.id.highest_rated).setVisible(false);
                menuSettings.findItem(R.id.most_popular).setVisible(true);
                menuSettings.findItem(R.id.favorite_movies).setVisible(true);
                order_by = getString(R.string.rates);
                loadPosters();
                mRecyclerView.smoothScrollToPosition(0);
                return true;
            case R.id.most_popular:
                menuSettings.findItem(R.id.favorite_movies).setVisible(true);
                menuSettings.findItem(R.id.highest_rated).setVisible(true);
                menuSettings.findItem(R.id.most_popular).setVisible(false);
                order_by = getString(R.string.popular);
                loadPosters();
                mRecyclerView.smoothScrollToPosition(0);
                return true;
            case R.id.favorite_movies:
                menuSettings.findItem(R.id.highest_rated).setVisible(true);
                menuSettings.findItem(R.id.most_popular).setVisible(true);
                menuSettings.findItem(R.id.favorite_movies).setVisible(false);
                //TODO order_by favorites
                order_by = getString(R.string.favorites);
                loadFavorites();
                //loadPosters();
                mRecyclerView.smoothScrollToPosition(0);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(String position) {
        Context context = this;
        Intent destiny = new Intent(context, MovieDetail.class);
        Integer integer = Integer.valueOf(position);
        try {
            destiny.putExtra(Intent.EXTRA_TEXT, array_json.getJSONObject(integer).toString());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            array_bitmap[integer].compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            destiny.putExtra(getString(R.string.picture), byteArray);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException ex) {
            //This happens if, somehow, some image was not loaded, so, refresh
            loadPosters();
        }
        startActivityForResult(destiny, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if(order_by == getString(R.string.favorites)) {
                loadFavorites();
            }
        }
    }

    private JSONObject getJSONfromAPI(String mode, String popular, String top_rated) {
        Uri uri;

        JSONObject json = new JSONObject();
        if (mode.equalsIgnoreCase(getString(R.string.popular))) {
            uri = Uri.parse(popular).buildUpon()
                    .appendQueryParameter(getString(R.string.api_key), THE_MOVIE_DB_API_TOKEN).build();
        } else if (mode.equalsIgnoreCase(getString(R.string.rates))) {
            uri = Uri.parse(top_rated).buildUpon()
                    .appendQueryParameter(getString(R.string.api_key), THE_MOVIE_DB_API_TOKEN).build();
        } else {
            return json;
        }
        try {
            json = new JSONObject(NetworkUtilities.getResponseFromHttpUrl(new URL(uri.toString())));
        } catch (IOException | JSONException malformed) {
            Log.e(malformed.toString(), malformed.getMessage());
        }
        return json;
    }
}
