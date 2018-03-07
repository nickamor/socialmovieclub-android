package com.nickamor.movieclub.controller;

import android.os.AsyncTask;

import com.nickamor.movieclub.model.MovieModel;

/**
 * Background task to set new rating.
 */
public class SetRatingTask extends AsyncTask<String, Void, Void> {
    private final float rating;

    public SetRatingTask(float rating) {
        this.rating = rating;
    }

    @Override
    protected Void doInBackground(String... params) {
        MovieModel.getInstance().setRating(params[0], rating);
        return null;
    }
}
