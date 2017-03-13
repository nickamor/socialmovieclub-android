package com.nickamor.movieclub.model;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO for party details.
 */
public class Party {
    private String imdbID;
    private String date;
    private String venue;
    private String location;
    private List<String> invitees;
    private float userRating;

    public Party(String imdbID) {
        this(imdbID, "", "", "", new ArrayList<String>(), 0);
    }

    public Party(String imdbID, String date, String venue, String location, List<String> invitees, float userRating) {
        this.imdbID = imdbID;
        this.date = date;
        this.venue = venue;
        this.location = location;
        this.invitees = new ArrayList<>(invitees);
        this.userRating = userRating;
    }

    public String getImdbID() {
        return imdbID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getInvitees() {
        return invitees;
    }

    public int getInviteesCount() {
        return invitees.size();
    }

    public boolean addInvitee(String invitee) {
        return invitees.add(invitee);
    }

    public boolean removeInvitee(String invitee) {
        return invitees.remove(invitee);
    }

    public float getUserRating() {
        return userRating;
    }

    public void setUserRating(float userRating) {
        this.userRating = userRating;
    }
}