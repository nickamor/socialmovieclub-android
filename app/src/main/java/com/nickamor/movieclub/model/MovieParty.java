package com.nickamor.movieclub.model;

/**
 * Composite of Movie and Party.
 */
public class MovieParty {
    private Movie movie;
    //List<Party> parties;
    private Party party;

    /**
     * Constructor
     *
     * @param movie The movie .
     */
    public MovieParty(Movie movie, Party party) {
        this.movie = movie;
        this.party = party;
    }

    public MovieParty(Movie movie) {
        this(movie, new Party(movie.imdbID));
    }

    public Movie getMovie() {
        return movie;
    }

    public Party getParty() {
        return party;
    }
}
