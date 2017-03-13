package com.nickamor.movieclub.model.chain;

import android.util.Log;

import com.nickamor.movieclub.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

/**
 * Performs RESTful API queries on the OMDb API service.
 */
public class OMDBAdapter {

    private static final String baseURL = "http://www.omdbapi.com/?";
    private static final String defaultByIdFormat = "i=%s&type=movie&r=json";
    private static final String defaultSearchTitle = "s=%s&type=movie&r=json";
    private static final String fullPlotModifier = "&plot=long";

    /**
     * Download the HTTP object with the given URL.
     *
     * @param requestUrl
     * @return
     */
    private static String executeRequest(String requestUrl) {
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // urlConnection.setRequestMethod("GET"); // GET is the default
            urlConnection.connect();

            int response = urlConnection.getResponseCode();

            String result = "";

            if (response == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                result = readStream(in);
            }

            urlConnection.disconnect();

            return result;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL argument.");
        } catch (IOException e) {
            throw new RuntimeException("Could not open connection to remote host.");
        }
    }

    private static String readStream(InputStream in) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String readLine = "";

        while ((readLine = bufferedReader.readLine()) != null) {
            stringBuilder.append(readLine);
        }

        return stringBuilder.toString();
    }

    private static Movie buildMovie(JSONObject response, JSONObject fullPlotResponse) throws JSONException {
        String imdbID, title, year, shortPlot, fullPlot, poster;

        imdbID = response.getString("imdbID");
        title = response.getString("Title");
        year = response.getString("Year");
        shortPlot = response.getString("Plot");
        fullPlot = fullPlotResponse.getString("Plot");
        poster = response.getString("Poster");

        return new Movie(imdbID, title, year, shortPlot, fullPlot, poster);
    }

    /**
     * Get the full details of a movie with the given imdbID.
     *
     * @param imdbID The imdbID to queryByID on.
     * @return The Movie from the API response, or null if no such movie exists.
     */
    public static Movie byImdbID(String imdbID) {
        String idQuery = baseURL + String.format(defaultByIdFormat, imdbID);
        String idQueryFullPlot = idQuery + fullPlotModifier;

        try {
            JSONObject response = new JSONObject(executeRequest(idQuery));

            if (response.getString("Response").equals("True")) {
                JSONObject responseFullPlot = new JSONObject(executeRequest(idQueryFullPlot));
                if (responseFullPlot.getString("Response").equals("True")) {
                    return buildMovie(response, responseFullPlot);
                }
            }
        } catch (JSONException e) {
            Log.d("OMDB FAIL", "byImdbID() called with: " + "imdbID = [" + imdbID + "]", e);
        }

        return null;
    }

    /**
     * Search for movies by title.
     *
     * @param titleUnescaped Movie title to search for.
     * @return A Collection of SearchResult triples.
     */
    public static List<MovieStub> searchByTitle(String titleUnescaped) {
        List<MovieStub> results = new LinkedList<>();

        try {
            String title = URLEncoder.encode(titleUnescaped, "utf-8"), response;

            try {
                response = executeRequest(String.format(baseURL + defaultSearchTitle, title));
            } catch (RuntimeException e) {
                Log.e("OMDB FAIL", "Could not execute request.", e);
                return results;
            }

            JSONObject jsonResponse = new JSONObject(response);

            JSONArray jsonResults = jsonResponse.getJSONArray("Search");

            for (int i = 0; i < jsonResults.length(); i++) {
                JSONObject jsonResult = jsonResults.getJSONObject(i);

                results.add(new MovieStub(jsonResult));
            }
        } catch (JSONException e) {
            Log.e("OMDB FAIL", "searchByTitle() called with: " + "title = [" + titleUnescaped + "]", e);
        } catch (UnsupportedEncodingException e) {
            Log.e("OMDB FAIL", "Can't encode to utf-8?", e);
        }

        return results;
    }
}
