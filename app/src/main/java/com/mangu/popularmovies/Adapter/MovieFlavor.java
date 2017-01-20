package com.mangu.popularmovies.Adapter;

import java.net.URI;

/**
 * Created by Adrian Portillo on 20/01/2017.
 */

public class MovieFlavor {
    String url;
    URI movie_uri;

    MovieFlavor(String url) {
        this.url = url;
        this.movie_uri = URI.create(this.url);
    }
    MovieFlavor(URI uri) {
        this.movie_uri= uri;
        this.url= this.movie_uri.toString();
    }
}
