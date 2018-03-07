package com.nickamor.movieclub.model.chain;

import com.nickamor.movieclub.model.Movie;
import com.nickamor.movieclub.model.Party;

import java.util.List;

/**
 * Interface to implementation of the Chain of Command design pattern. A request handler is capable
 * of handling queries to a dataset.
 */
public interface RequestHandler {

    /**
     * Set the RequestHandler to fall back on.
     *
     * @param handler
     */
    void setSuccessor(RequestHandler handler);

    /**
     * Get a single Movie with the given imdbID.
     *
     * @param imdbID
     * @return
     */
    Movie getMovieByImdbID(String imdbID);

    /**
     * Get the Party for the Movie with the given imdbID.
     *
     * @param imdbID
     * @return
     */
    Party getPartyByImdbID(String imdbID);

    List<Movie> getRecentMovies();

    List<Party> getRecentParties();

    void setMovie(String imdbID, Movie movie);

    void setParty(String imdbID, Party party);

    /**
     * Set the star rating of the movie with the given imdbID.
     *
     * @param imdbID
     * @param rating
     */
    void setRating(String imdbID, float rating);

    /**
     * Get a number of Movies where the movie title contains the given search string.
     *
     * @param query
     * @return
     */
    List<Movie> getMoviesByTitle(String query);
}
