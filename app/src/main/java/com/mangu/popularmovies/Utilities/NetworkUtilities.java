package com.mangu.popularmovies.Utilities;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.mangu.popularmovies.MainActivity;
import com.mangu.popularmovies.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;



import static com.mangu.popularmovies.BuildConfig.THE_MOVIE_DB_API_TOKEN;

/**
 * Created by Adrian Portillo on 20/01/2017.
 */

public class NetworkUtilities {
    public static JSONObject getJSONfromAPI (String mode, String popular, String top_rated) {
        Uri uri;
        JSONObject json = new JSONObject();
        if(mode.equalsIgnoreCase("popular")) {
            uri = Uri.parse(popular).buildUpon()
                    .appendQueryParameter("api_key", THE_MOVIE_DB_API_TOKEN).build();
        }else if(mode.equalsIgnoreCase("rates")) {
            uri = Uri.parse(top_rated).buildUpon()
                    .appendQueryParameter("api_key", THE_MOVIE_DB_API_TOKEN).build();
        }else {
            return json;
        }
        try {
            json =  new JSONObject(getResponseFromHttpUrl(new URL(uri.toString())));
        }catch (IOException | JSONException malformed) {
            Log.e(malformed.toString(), malformed.getMessage());
        }
        return json;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }


}
