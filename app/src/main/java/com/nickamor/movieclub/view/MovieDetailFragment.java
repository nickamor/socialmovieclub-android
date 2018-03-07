package com.nickamor.movieclub.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nickamor.movieclub.R;
import com.nickamor.movieclub.controller.SetRatingTask;
import com.nickamor.movieclub.model.Movie;
import com.nickamor.movieclub.model.MovieModel;
import com.nickamor.movieclub.model.MovieParty;
import com.nickamor.movieclub.model.Party;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Fragment for movie details.
 */
public class MovieDetailFragment extends Fragment {

    private ViewHolder mHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_movie, container, false);

        mHolder = new ViewHolder(view);

        String imdbID = getArguments().getString(getString(R.string.EXTRA_KEY_IMDB_ID));

        new GetMoviePartyTask().execute(imdbID);

        return view;
    }

    public void bindViewHolder(ViewHolder holder, final MovieParty movieParty) {
        holder.mTitle.setText(movieParty.getMovie().title);
        holder.mYear.setText(movieParty.getMovie().year);
        holder.mFullPlot.setText(movieParty.getMovie().fullPlot);
        holder.mUserRating.setRating(movieParty.getParty().getUserRating());

        holder.mUserRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
                new SetRatingTask(rating).execute(movieParty.getMovie().imdbID);
            }
        });

        try {
            PosterImageTask task = new PosterImageTask(movieParty.getMovie().poster);
            task.execute();
        } catch (IllegalArgumentException e) {
            // Ignore...
        }
    }

    /**
     * ViewHolder for movie detail Views.
     */
    public class ViewHolder {
        public final ImageView mPoster;
        public final TextView mTitle;
        public final TextView mYear;
        public final TextView mFullPlot;
        public final RatingBar mUserRating;

        public ViewHolder(View v) {
            mPoster = (ImageView) v.findViewById(R.id.movie_poster);
            mTitle = (TextView) v.findViewById(R.id.movie_title);
            mYear = (TextView) v.findViewById(R.id.movie_year);
            mFullPlot = (TextView) v.findViewById(R.id.movie_full_plot);
            mUserRating = (RatingBar) v.findViewById(R.id.movie_user_rating);
        }
    }

    /**
     * Background task to download an image.
     */
    abstract class DownloadBitmapTask extends AsyncTask<Void, Integer, Bitmap> {
        private URL url;

        public DownloadBitmapTask(String uri) {
            try {
                url = new URL(uri);
            } catch (MalformedURLException e) {
                throw new RuntimeException("Invalid URL argument.", e);
            }
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = null;

            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                int response = urlConnection.getResponseCode();

                if (response == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    bitmap = BitmapFactory.decodeStream(in);
                }

                urlConnection.disconnect();
            } catch (IOException e) {
                throw new RuntimeException("Could not open connection to remote host.", e);
            }

            return bitmap;
        }
    }

    /**
     * Background task to set the movie poster image.
     */
    class PosterImageTask extends DownloadBitmapTask {
        public PosterImageTask(String uri) {
            super(uri);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mHolder.mPoster.setImageBitmap(bitmap);
        }
    }

    private class GetMoviePartyTask extends AsyncTask<String, Void, MovieParty> {
        private Movie movie;
        private Party party;

        @Override
        protected MovieParty doInBackground(String... params) {
            Movie movie = MovieModel.getInstance().getMovieByImdbID(params[0]);
            Party party = MovieModel.getInstance().getPartyByImdbID(params[0]);

            return new MovieParty(movie, party);
        }

        @Override
        protected void onPostExecute(MovieParty movieParty) {
            bindViewHolder(mHolder, movieParty);
        }
    }
}
