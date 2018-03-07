package com.nickamor.movieclub.model.chain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A quad that encapsulates a single OMDb API search result.
 */
public class MovieStub {
    public final String title;
    public final String year;
    public final String imdbID;
    public final String poster;

    public MovieStub(JSONObject json) throws JSONException {
        title = json.getString("Title");
        year = json.getString("Year");
        imdbID = json.getString("imdbID");
        poster = json.getString("Poster");
    }

    @Override
    public String toString() {
        return title + " (" + year + ")";
    }
}
