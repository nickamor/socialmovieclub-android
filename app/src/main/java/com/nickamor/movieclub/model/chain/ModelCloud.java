package com.nickamor.movieclub.model.chain;

import com.nickamor.movieclub.model.Movie;
import com.nickamor.movieclub.model.MovieModel;

import java.util.List;

/**
 * Facade to OMDBAdapter.
 */
public class ModelCloud extends AbstractHandler {
    @Override
    public Movie getMovieByImdbID(String imdbID) {
        Movie movie = OMDBAdapter.byImdbID(imdbID);

        if (movie != null) {
            return movie;
        }

        return super.getMovieByImdbID(imdbID);
    }

    @Override
    public List<Movie> getMoviesByTitle(String query) {
        List<Movie> movies = super.getMoviesByTitle(query);

        if (movies.size() <= 0) {
            List<MovieStub> results = OMDBAdapter.searchByTitle(query);

            for (MovieStub movieStub : results) {
                movies.add(MovieModel.getInstance().getMovieByImdbID(movieStub.imdbID));
            }

            return movies;
        }

        return movies;
    }
}
