package com.nickamor.movieclub.controller;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nickamor.movieclub.R;
import com.nickamor.movieclub.model.Movie;
import com.nickamor.movieclub.model.MovieModel;
import com.nickamor.movieclub.model.MovieParty;
import com.nickamor.movieclub.model.Party;
import com.nickamor.movieclub.view.MovieDetailActivity;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Adapter for main activity RecyclerView.
 */
public class MoviePartyAdapter extends RecyclerView.Adapter<MoviePartyAdapter.ViewHolder> {
    private final Deque<MovieParty> mModel = new ConcurrentLinkedDeque<>();
    private boolean onBind;

    private void getDataFromModel() {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                MovieModel movieModel = MovieModel.getInstance();

                mModel.clear();

                List<Party> parties = movieModel.getRecentParties();

                for (Party party :
                        parties) {
                    Movie movie = movieModel.getMovieByImdbID(party.getImdbID());
                    mModel.add(new MovieParty(movie, party));
                }

                return null;
            }
        }.execute();

    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        getDataFromModel();

        super.onViewAttachedToWindow(holder);
    }

    /**
     * Create ViewHolder
     *
     * @param parent   Parent
     * @param viewType
     * @return The ViewHolder that was created.
     */
    @Override
    public MoviePartyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_movie_card, parent, false);

        ViewHolder vh = new ViewHolder(v, this);
        return vh;
    }

    /**
     * Binds model data to view components
     *
     * @param holder   ViewHolder of Views to bind
     * @param position RecyclerView position of binding object
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        MovieParty[] movieParties = (MovieParty[]) mModel.toArray();

        final MovieParty movieParty = movieParties[position];

        onBind = true;

        holder.mMovieParty = movieParty;
        holder.mPosition = position;

        Movie movie = movieParty.getMovie();

        holder.mTitle.setText(movie.title);
        holder.mYear.setText(String.format("(%s)", movie.year));
        holder.mShortPlot.setText(movie.plot);

        Party party = movieParty.getParty();

        int invitees = party.getInviteesCount();
        if (invitees > 0) {
            holder.mInvitees.setText(String.format("%d invitees", invitees));
        } else {
            holder.mInvitees.setText("");
        }

        holder.mRating.setRating(movieParty.getParty().getUserRating());

        String moviePosterPackage = holder.mContext.getPackageName();
        int moviePosterId = holder.mContext.getResources().getIdentifier(movie.imdbID, "drawable", moviePosterPackage);
        holder.mPoster.setImageResource(moviePosterId);

        onBind = false;
    }

    /**
     * Return the number of Movies in the dataset
     *
     * @return The number of Movies.
     */
    @Override
    public int getItemCount() {
        return mModel.size();
    }

    /**
     * ViewHolder for RecyclerView items
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mRoot;
        public TextView mTitle;
        public TextView mYear;
        public TextView mShortPlot;
        public TextView mInvitees;
        public RatingBar mRating;
        public ImageView mPoster;

        public int mPosition;
        public MovieParty mMovieParty;

        public Context mContext;
        public MoviePartyAdapter mAdapter;

        /**
         * Constructor
         *
         * @param v       View to bind
         * @param adapter Parent
         */
        public ViewHolder(View v, MoviePartyAdapter adapter) {
            super(v);

            mAdapter = adapter;

            mRoot = v;
            mContext = v.getContext();

            mTitle = (TextView) v.findViewById(R.id.movie_title);
            mYear = (TextView) v.findViewById(R.id.movie_year);
            mShortPlot = (TextView) v.findViewById(R.id.movie_short_plot);
            mInvitees = (TextView) v.findViewById(R.id.party_invitees);
            mRating = (RatingBar) v.findViewById(R.id.movie_user_rating);
            mPoster = (ImageView) v.findViewById(R.id.movie_poster);

            /** listeners **/
            mRoot.setOnClickListener(new View.OnClickListener() {
                /**
                 * When clicked, open the next activity.
                 * @param v
                 */
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MovieDetailActivity.class);
                    intent.putExtra(mRoot.getResources().getString(R.string.EXTRA_KEY_IMDB_ID), mMovieParty.getMovie().imdbID);

                    mContext.startActivity(intent);
                }
            });

            mRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                /**
                 * When the rating is changed, update the rating in the model.
                 * @param ratingBar The RatingBar View that was changed.
                 * @param rating The new rating.
                 * @param fromUser Whether the rating change was initiated by the user.
                 */
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if (!mAdapter.onBind) {
                        mMovieParty.getParty().setUserRating(rating);
                        mAdapter.notifyItemChanged(mPosition);
                    }
                }
            });
        }
    }
}
