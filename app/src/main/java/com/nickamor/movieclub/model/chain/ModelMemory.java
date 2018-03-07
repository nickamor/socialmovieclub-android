package com.nickamor.movieclub.model.chain;

import com.nickamor.movieclub.model.Movie;
import com.nickamor.movieclub.model.MovieModel;
import com.nickamor.movieclub.model.Party;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Memory model implementing LRU.
 */
public class ModelMemory extends AbstractHandler {
    private final Deque<Movie> mMovies = new ConcurrentLinkedDeque<>();
    private final Deque<Party> mParties = new ConcurrentLinkedDeque<>();

    private void addMovieMRU(Movie movie) {
        if (!mMovies.contains(movie)) {
            mMovies.addFirst(movie);
        }

        while (mMovies.size() >= MovieModel.MAX_MOVIES) {
            mMovies.removeLast();
        }
    }

    private void addPartyMRU(Party party) {
        if (!mMovies.contains(party)) {
            mParties.addFirst(party);
        }

        while (mParties.size() >= MovieModel.MAX_PARTIES) {
            mParties.removeLast();
        }
    }

    @Override
    public Movie getMovieByImdbID(String imdbID) {
        for (Movie movie : mMovies) {
            if (imdbID.equals(movie.imdbID)) {
                return movie;
            }
        }

        Movie movie = super.getMovieByImdbID(imdbID);

        if (movie != null) {
            setMovie(imdbID, movie);
            addMovieMRU(movie);
        }

        return movie;
    }

    @Override
    public List<Movie> getMoviesByTitle(String query) {
        // Query successor, and if they return no results do our own search.
        List<Movie> movies = super.getMoviesByTitle(query);

        return movies;
    }

    @Override
    public Party getPartyByImdbID(String imdbID) {
        for (Party party : mParties) {
            if (party.getImdbID().equals(imdbID)) {
                return party;
            }
        }

        Party party = super.getPartyByImdbID(imdbID);

        addPartyMRU(party);

        return party;
    }

    @Override
    public List<Movie> getRecentMovies() {
        return new ArrayList<>(mMovies);
    }

    @Override
    public List<Party> getRecentParties() {
        return new ArrayList<>(mParties);
    }

    @Override
    public void setMovie(String imdbID, Movie movie) {
        addMovieMRU(movie);

        super.setMovie(imdbID, movie);
    }

    @Override
    public void setParty(String imdbID, Party party) {
        addPartyMRU(party);

        super.setParty(imdbID, party);
    }

    @Override
    public void setRating(String imdbID, float rating) {
        for (Party party : mParties) {
            if (party.getImdbID().equals(imdbID)) {
                party.setUserRating(rating);
                addPartyMRU(party);
            }
        }

        super.setRating(imdbID, rating);
    }
}
