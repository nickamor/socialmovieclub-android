package com.nickamor.movieclub.model.chain;

import android.content.Context;

import com.nickamor.movieclub.model.Movie;
import com.nickamor.movieclub.model.Party;

import java.util.List;

/**
 * Facade to SQLiteAdapter.
 */
public class ModelSQLite extends AbstractHandler {
    private final SQLiteAdapter db;

    public ModelSQLite(Context context) {
        db = new SQLiteAdapter(context);
        db.open();
    }

    @Override
    public Movie getMovieByImdbID(String imdbID) {
        Movie movie = db.selectOneMovie(imdbID);

        if (movie != null) {
            db.updateMovieLastUsed(imdbID);
            return movie;
        }

        movie = super.getMovieByImdbID(imdbID);

        if (movie != null) {
            db.insertMovie(movie);
        }

        return movie;
    }

    @Override
    public List<Movie> getMoviesByTitle(String query) {
        List<Movie> movies = super.getMoviesByTitle(query);

        if (movies.size() <= 0) {
            movies.addAll(db.selectMovieByTitle(query));
        }

        return movies;
    }

    @Override
    public List<Movie> getRecentMovies() {
        List<Movie> movies = db.selectRecentMovies();

        if (movies.size() <= 0) {
            movies.addAll(super.getRecentMovies());
        }

        return movies;
    }

    @Override
    public void setMovie(String imdbID, Movie movie) {
        db.updateMovie(movie);

        super.setMovie(imdbID, movie);
    }

    @Override
    public Party getPartyByImdbID(String imdbID) {
        Party party = db.selectOneParty(imdbID);

        if (party == null) {
            party = new Party(imdbID);
            db.insertParty(party);
        }

        return party;
    }

    @Override
    public List<Party> getRecentParties() {
        List<Party> parties = db.selectRecentParties();

        if (parties.size() <= 0) {
            parties.addAll(super.getRecentParties());
        }

        return parties;
    }

    @Override
    public void setRating(String imdbID, float rating) {
        db.updatePartyRating(imdbID, rating);
    }

    @Override
    public void setParty(String imdbID, Party party) {
        db.updateParty(party);

        super.setParty(imdbID, party);
    }
}
