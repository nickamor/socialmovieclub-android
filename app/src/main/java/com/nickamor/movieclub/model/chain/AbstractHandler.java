package com.nickamor.movieclub.model.chain;

import com.nickamor.movieclub.model.Movie;
import com.nickamor.movieclub.model.Party;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract RequestHandler. Will defer to successor RequestHandler where appropriate.
 */
public abstract class AbstractHandler implements RequestHandler {
    private RequestHandler successor;

    @Override
    public void setSuccessor(RequestHandler successor) {
        this.successor = successor;
    }

    /**
     * Fall back on the successor.
     *
     * @param imdbID
     * @return
     */
    @Override
    public Movie getMovieByImdbID(String imdbID) {
        if (successor != null) {
            return successor.getMovieByImdbID(imdbID);
        }

        return null;
    }

    @Override
    public List<Movie> getMoviesByTitle(String query) {
        if (successor != null) {
            return successor.getMoviesByTitle(query);
        }

        return new ArrayList<>();
    }

    @Override
    public Party getPartyByImdbID(String imdbID) {
        if (successor != null) {
            return successor.getPartyByImdbID(imdbID);
        }

        return null;
    }

    @Override
    public List<Movie> getRecentMovies() {
        if (successor != null) {
            return successor.getRecentMovies();
        }

        return null;
    }

    @Override
    public List<Party> getRecentParties() {
        if (successor != null) {
            return successor.getRecentParties();
        }

        return null;
    }

    @Override
    public void setMovie(String imdbID, Movie movie) {
        if (successor != null) {
            successor.setMovie(imdbID, movie);
        }
    }

    @Override
    public void setParty(String imdbID, Party party) {
        if (successor != null) {
            successor.setParty(imdbID, party);
        }
    }

    @Override
    public void setRating(String imdbID, float rating) {
        if (successor != null) {
            successor.setRating(imdbID, rating);
        }
    }
}
