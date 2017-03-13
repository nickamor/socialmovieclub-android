package com.nickamor.movieclub.view;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nickamor.movieclub.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Detail view activity for MovieParties.
 */
public class MovieDetailActivity extends AppCompatActivity {
    private MovieDetailFragment mMovieFragment;
    private PartyDetailFragment mPartyFragment;

    /**
     * Inflate and bind the view.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Get model.
        String intentExtraKey = getString(R.string.EXTRA_KEY_IMDB_ID);
        String intentExtraValue = getIntent().getExtras().getString(intentExtraKey);

        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.EXTRA_KEY_IMDB_ID), intentExtraValue);

        mMovieFragment = new MovieDetailFragment();
        mMovieFragment.setArguments(bundle);

        mPartyFragment = new PartyDetailFragment();
        mPartyFragment.setArguments(bundle);

        // Set view.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager, intentExtraValue);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager, String imdbID) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(mMovieFragment, "Movie");
        adapter.addFrag(mPartyFragment, "Party");
        viewPager.setAdapter(adapter);
    }

    /**
     * Adapter for ViewPager to Fragments.
     */
    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        /**
         * Constructor.
         *
         * @param manager
         */
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        /**
         * Get the Fragment at the give pager position.
         *
         * @param position Index of the Fragment.
         * @return The Fragment at that position.
         */
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        /**
         * Get the number of Fragments in the ViewPager.
         *
         * @return The number of Fragments.
         */
        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        /**
         * Add a Fragment to the ViewPager
         *
         * @param fragment The Fragment to add.
         * @param title    The title to display on the ViewPager.
         */
        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        /**
         * Get the title of the Fragment at the given position.
         *
         * @param position The position in the ViewPager to get the title of.
         * @return The title for the specified ViewPager tab.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
