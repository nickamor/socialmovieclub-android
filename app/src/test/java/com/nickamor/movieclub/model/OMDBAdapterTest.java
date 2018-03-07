package com.nickamor.movieclub.model;

import com.nickamor.movieclub.model.chain.OMDBAdapter;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Created by nick on 7/10/2015.
 */
public class OMDBAdapterTest extends TestCase {

    @Test
    public void test() {
        System.out.println(OMDBAdapter.byImdbID("tt0371746"));
    }
}