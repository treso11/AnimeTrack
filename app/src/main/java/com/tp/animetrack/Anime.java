package com.tp.animetrack;

public class Anime {
    private String title;
    private String status;
    private float rating;
    private String posterUrl;
    private int tmdbId;
    private String personalNote;

    public String getViewingDate() {
        return viewingDate;
    }

    public void setViewingDate(String viewingDate) {
        this.viewingDate = viewingDate;
    }

    private String viewingDate;

    public String getPersonalNote() {
        return personalNote;
    }

    public void setPersonalNote(String personalNote) {
        this.personalNote = personalNote;
    }


    public Anime(String title, String status, float rating, String posterUrl, int tmdbId) {
        this.title = title;
        this.status = status;
        this.rating = rating;
        this.posterUrl = posterUrl;
        this.tmdbId = tmdbId;
    }

    // Getters et Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public int getTmdbId() { return tmdbId; }
    public void setTmdbId(int tmdbId) { this.tmdbId = tmdbId; }
}
