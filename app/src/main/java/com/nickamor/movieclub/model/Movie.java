package com.nickamor.movieclub.model;

/**
 * POJO for movie information and userRating. A Movie is immutable.
 */
public class Movie {
    public final String imdbID;
    public final String title;
    public final String year;
    public final String plot;
    public final String fullPlot;
    public final String poster;

    public Movie(String imdbID, String title, String year, String plot, String fullPlot, String poster) {
        this.imdbID = imdbID;
        this.title = title;
        this.year = year;
        this.plot = plot;
        this.fullPlot = fullPlot;
        this.poster = poster;
    }

    public String toDetailString() {
        String string = String.format("Movie = {\n" +
                "\timdbID = %s\n" +
                "\ttitle = %s\n" +
                "\tyear = %s\n" +
                "\tplot = %s\n" +
                "\tfullPlot = %s\n" +
                "\tposter = %s\n" +
                "}", imdbID, title, year, plot, fullPlot, poster);

        return string;
    }

    public String toString() {
        return title + " (" + year + ")";
    }

}
