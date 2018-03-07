package com.nickamor.movieclub.model;

import android.content.Context;

import com.nickamor.movieclub.model.chain.ModelCloud;
import com.nickamor.movieclub.model.chain.ModelMemory;
import com.nickamor.movieclub.model.chain.ModelSQLite;
import com.nickamor.movieclub.model.chain.RequestHandler;

import java.util.List;

/**
 * Singleton container/facade to internal application data.
 */
public class MovieModel {
    public static final int MAX_MOVIES = 10;
    public static final int MAX_PARTIES = 5;

    private static MovieModel ourInstance = null;
    private final RequestHandler handler;

    private MovieModel(Context context) {
        RequestHandler cloud = new ModelCloud(), database = new ModelSQLite(context), memory = new ModelMemory();

        database.setSuccessor(cloud);
        memory.setSuccessor(database);

        handler = memory;
    }

    public static MovieModel getInstance() {
        if (ourInstance == null) {
            throw new IllegalStateException("The model has not been initialised.");
        }

        return ourInstance;
    }

    public static MovieModel getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new MovieModel(context);
        }

        return ourInstance;
    }

    public Movie getMovieByImdbID(String imdbID) {
        return handler.getMovieByImdbID(imdbID);
    }

    public Party getPartyByImdbID(String imdbID) {
        return handler.getPartyByImdbID(imdbID);
    }

    public List<Movie> getMoviesByTitle(String needle) {
        return handler.getMoviesByTitle(needle);
    }

    public List<Movie> getRecentMovies() {
        return handler.getRecentMovies();
    }

    public List<Party> getRecentParties() {
        return handler.getRecentParties();
    }

    public void setMovie(String imdbID, Movie movie) {
        handler.setMovie(imdbID, movie);
    }

    public void setParty(String imdbID, Party party) {
        handler.setParty(imdbID, party);
    }

    public void setRating(String imdbID, float rating) {
        handler.setRating(imdbID, rating);
    }
}
