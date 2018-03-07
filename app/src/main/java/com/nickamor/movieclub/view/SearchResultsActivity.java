package com.nickamor.movieclub.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nickamor.movieclub.R;
import com.nickamor.movieclub.model.Movie;
import com.nickamor.movieclub.model.MovieModel;

import java.util.List;

/**
 * Search result view.
 */
public class SearchResultsActivity extends AppCompatActivity {
    private ListView mResults;
    private TextView mNoResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        mResults = (ListView) findViewById(R.id.search_results);
        mNoResults = (TextView) findViewById(R.id.no_search_results);

        handleIntent(getIntent());

        mResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) mResults.getItemAtPosition(position);

                Intent intent = new Intent(SearchResultsActivity.this, MovieDetailActivity.class);
                intent.putExtra(getString(R.string.EXTRA_KEY_IMDB_ID), movie.imdbID);

                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            new SearchTask(this).execute(query);
        }
    }

    /**
     * Background task to perform search.
     */
    class SearchTask extends AsyncTask<String, Void, List<Movie>> {
        // For some reason the singleton isn't working here and I can only get an empty Model.
        // TODO: remove Context
        private final Context context;

        public SearchTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            return MovieModel.getInstance(context).getMoviesByTitle(params[0]);
        }

        @Override
        protected void onPostExecute(List<Movie> searchResults) {
            if (searchResults.size() > 0) {
                ArrayAdapter<Movie> searchResultArrayAdapter = new ArrayAdapter<>(SearchResultsActivity.this, android.R.layout.simple_list_item_1, searchResults);

                mResults.setAdapter(searchResultArrayAdapter);

                mNoResults.setVisibility(View.GONE);
            } else {
                mNoResults.setVisibility(View.VISIBLE);
            }
        }
    }
}
