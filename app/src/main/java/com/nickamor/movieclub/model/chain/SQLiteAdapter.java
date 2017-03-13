package com.nickamor.movieclub.model.chain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nickamor.movieclub.model.Movie;
import com.nickamor.movieclub.model.Party;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite database frontend for model storage.
 */
public class SQLiteAdapter {
    // Columns
    public static final String MOVIE_ID = "_id";
    public static final String MOVIE_IMDBID = "imdbID";
    public static final String MOVIE_TITLE = "Title";
    public static final String MOVIE_YEAR = "Year";
    public static final String MOVIE_PLOT = "Plot";
    public static final String MOVIE_FULLPLOT = "FullPlot";
    public static final String MOVIE_POSTER = "Poster";
    public static final String MOVIE_LASTUSED = "LastUsed";
    private static final int DATABASE_VERSION = 20;
    private static final String DATABASE_NAME = "Movies.db";
    private static final String MOVIES_TABLE = "Movies";
    private static final String[] MOVIE_COLUMNS = {
            MOVIE_IMDBID,
            MOVIE_TITLE,
            MOVIE_YEAR,
            MOVIE_PLOT,
            MOVIE_FULLPLOT,
            MOVIE_POSTER
    };

    private static final String PARTIES_TABLE = "Parties";

    private static final String PARTY_ID = "_id";
    private static final String PARTY_IMDBID = "imdbID";
    private static final String PARTY_DATE = "Date";
    private static final String PARTY_VENUE = "Venue";
    private static final String PARTY_LOCATION = "Location";
    private static final String PARTY_INVITEES = "Invitees";
    private static final String PARTY_USERRATING = "UserRating";
    private static final String PARTY_LASTUSED = "LastUsed";

    private static final String[] PARTY_COLUMNS = {
            PARTY_IMDBID,
            PARTY_DATE,
            PARTY_VENUE,
            PARTY_LOCATION,
            PARTY_INVITEES,
            PARTY_USERRATING
    };

    private static final String MOVIES_CREATE = "CREATE TABLE " + MOVIES_TABLE + " (" +
            MOVIE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // _id
            MOVIE_IMDBID + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, " + // imdbID
            MOVIE_TITLE + " TEXT NOT NULL, " + // Title
            MOVIE_YEAR + " TEXT NOT NULL, " + // Year
            MOVIE_PLOT + " TEXT NOT NULL, " + // Plot
            MOVIE_FULLPLOT + " TEXT NOT NULL, " + // FullPlot
            MOVIE_POSTER + " TEXT NOT NULL, " + // Poster
            MOVIE_LASTUSED + " INTEGER NOT NULL" + // LastUsed
            ");";

    private static final String MOVIES_DESTROY = "DROP TABLE IF EXISTS " + MOVIES_TABLE;

    private static final String PARTIES_CREATE = "CREATE TABLE " + PARTIES_TABLE + " (" +
            PARTY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // _id
            PARTY_IMDBID + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, " + // imdbID
            PARTY_DATE + " TEXT NOT NULL, " + // Date
            PARTY_VENUE + " TEXT NOT NULL, " + // Venue
            PARTY_LOCATION + " TEXT NOT NULL, " + // Location
            PARTY_INVITEES + " TEXT NOT NULL, " + // Invitees
            PARTY_USERRATING + " REAL NOT NULL, " + // UserRating
            PARTY_LASTUSED + " INTEGER NOT NULL" + // LastUsed
            ");";

    private static final String PARTIES_DESTROY = "DROP TABLE IF EXISTS " + PARTIES_TABLE;

    private final DBHelper dbHelper;
    private SQLiteDatabase mDatabase;

    public SQLiteAdapter(Context context) {
        dbHelper = new DBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    protected String tryGetString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }

    protected void safePutString(ContentValues contentValues, String key, String value) {
        contentValues.put(key, DatabaseUtils.sqlEscapeString(value));
    }

    public void open() throws SQLiteException {
        try {
            mDatabase = dbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            Log.e("SQLITE FAIL", "Could not opening writable database.", e);
            mDatabase = dbHelper.getReadableDatabase();
        }
    }

    public void close() {
        mDatabase.close();
    }

    public ContentValues serializeMovie(Movie movie) {
        ContentValues contentValues = new ContentValues();
        long unixTime = getUnixTime();

        safePutString(contentValues, MOVIE_IMDBID, movie.imdbID);
        safePutString(contentValues, MOVIE_TITLE, movie.title);
        safePutString(contentValues, MOVIE_YEAR, movie.year);
        safePutString(contentValues, MOVIE_PLOT, movie.plot);
        safePutString(contentValues, MOVIE_FULLPLOT, movie.fullPlot);
        safePutString(contentValues, MOVIE_POSTER, movie.poster);
        contentValues.put(MOVIE_LASTUSED, unixTime);
        return contentValues;
    }

    public Movie deserializeMovie(Cursor cursor) {
        String imdbID, title, year, plot, fullPlot, poster;

        imdbID = tryGetString(cursor, MOVIE_IMDBID);
        title = tryGetString(cursor, MOVIE_TITLE);
        year = tryGetString(cursor, MOVIE_YEAR);
        plot = tryGetString(cursor, MOVIE_PLOT);
        fullPlot = tryGetString(cursor, MOVIE_FULLPLOT);
        poster = tryGetString(cursor, MOVIE_POSTER);

        return new Movie(imdbID, title, year, plot, fullPlot, poster);
    }

    public void insertMovie(Movie movie) {
        ContentValues contentValues = serializeMovie(movie);

        mDatabase.insert(MOVIES_TABLE, null, contentValues);
    }

    public void updateMovie(Movie movie) {
        ContentValues contentValues = serializeMovie(movie);

        mDatabase.update(MOVIES_TABLE, contentValues, "? = ?", new String[]{MOVIE_IMDBID, movie.imdbID});
    }

    public Movie selectOneMovie(String imdbID) {
        Movie movie = null;

        Cursor cursor = mDatabase.query(MOVIES_TABLE, MOVIE_COLUMNS, "? = ?", new String[]{MOVIE_IMDBID, imdbID}, null, null, null);

        if (cursor.moveToFirst()) {
            movie = deserializeMovie(cursor);
        }

        cursor.close();

        return movie;
    }

    public List<Movie> selectMovieByTitle(String query) {
        List<Movie> movies = new ArrayList<>();

        Cursor cursor = mDatabase.query(MOVIES_TABLE, MOVIE_COLUMNS, "? LIKE ?", new String[]{MOVIE_TITLE, "%" + query + "%"}, null, null, null);

        while (!cursor.isAfterLast()) {
            movies.add(deserializeMovie(cursor));

            cursor.moveToNext();
        }

        return movies;
    }

    public List<Movie> selectRecentMovies() {
        List<Movie> movies = new ArrayList<>();

        Cursor cursor = mDatabase.query(MOVIES_TABLE, MOVIE_COLUMNS, null, null, null, null, MOVIE_LASTUSED, "LIMIT 10");

        while (!cursor.isAfterLast()) {
            movies.add(deserializeMovie(cursor));

            cursor.moveToNext();
        }

        return movies;
    }

    public void deleteMovie(String imdbID) {
        mDatabase.delete(MOVIES_TABLE, "? = ?", new String[]{MOVIE_IMDBID, imdbID});
    }

    public void updateMovieLastUsed(String imdbID) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MOVIE_LASTUSED, getUnixTime());

        mDatabase.update(MOVIES_TABLE, contentValues, "? = ?", new String[]{MOVIE_IMDBID, imdbID});
    }

    public ContentValues serializeParty(Party party) {
        ContentValues contentValues = new ContentValues();
        long unixTime = getUnixTime();

        safePutString(contentValues, PARTY_IMDBID, party.getImdbID());
        safePutString(contentValues, PARTY_DATE, party.getDate());
        safePutString(contentValues, PARTY_VENUE, party.getVenue());
        safePutString(contentValues, PARTY_LOCATION, party.getLocation());

        JSONArray jsonArray = new JSONArray(party.getInvitees());
        safePutString(contentValues, PARTY_INVITEES, jsonArray.toString());

        contentValues.put(PARTY_USERRATING, party.getUserRating());

        contentValues.put(PARTY_LASTUSED, unixTime);

        return contentValues;
    }

    private long getUnixTime() {
        return System.currentTimeMillis() / 1000L;
    }

    public Party deserializeParty(Cursor cursor) {
        String imdbID, date, venue, location;
        List<String> invitees;
        float userRating;

        imdbID = tryGetString(cursor, PARTY_IMDBID);
        date = tryGetString(cursor, PARTY_DATE);
        venue = tryGetString(cursor, PARTY_VENUE);
        location = tryGetString(cursor, PARTY_LOCATION);

        invitees = new ArrayList<String>();
        try {
            JSONArray array = new JSONArray(tryGetString(cursor, PARTY_INVITEES));
            for (int i = 0; i < array.length(); i++) {
                invitees.add(array.getString(i));
            }
        } catch (JSONException e) {
            invitees = new ArrayList<>();
        }

        userRating = cursor.getFloat(cursor.getColumnIndexOrThrow(PARTY_USERRATING));

        return new Party(imdbID, date, venue, location, invitees, userRating);
    }

    public void insertParty(Party party) {
        ContentValues contentValues = serializeParty(party);

        mDatabase.insert(PARTIES_TABLE, null, contentValues);
    }

    public void updateParty(Party party) {
        ContentValues contentValues = serializeParty(party);

        mDatabase.update(PARTIES_TABLE, contentValues, "? = ?", new String[]{PARTY_IMDBID, party.getImdbID()});
    }

    public Party selectOneParty(String imdbID) {
        Party party = null;

        Cursor cursor = mDatabase.query(PARTIES_TABLE, PARTY_COLUMNS, "? = ?", new String[]{PARTY_IMDBID, imdbID}, null, null, null);

        if (cursor.moveToFirst()) {
            party = deserializeParty(cursor);
        }

        cursor.close();

        return party;
    }

    public List<Party> selectRecentParties() {
        List<Party> parties = new ArrayList<>();

        Cursor cursor = mDatabase.query(PARTIES_TABLE, PARTY_COLUMNS, null, null, null, null, PARTY_LASTUSED, "LIMIT 5");

        while (!cursor.isAfterLast()) {
            parties.add(deserializeParty(cursor));

            cursor.moveToNext();
        }

        return parties;
    }

    public void deleteParty(String imdbID) {
        mDatabase.delete(PARTIES_TABLE, "? = ?", new String[]{PARTY_IMDBID, imdbID});
    }

    public void updatePartyRating(String imdbID, float rating) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PARTY_USERRATING, rating);

        mDatabase.update(PARTIES_TABLE, contentValues, "? = ?", new String[]{PARTY_IMDBID, imdbID});

        updatePartyLastUsed(imdbID);
    }

    public void updatePartyLastUsed(String imdbID) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PARTY_LASTUSED, getUnixTime());

        mDatabase.update(PARTIES_TABLE, contentValues, "? = ?", new String[]{PARTY_IMDBID, imdbID});
    }

    /**
     * Manages database creation and migration.
     */
    public static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(MOVIES_CREATE);
            db.execSQL(PARTIES_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(MOVIES_DESTROY);
            db.execSQL(PARTIES_DESTROY);

            onCreate(db);
        }
    }
}

