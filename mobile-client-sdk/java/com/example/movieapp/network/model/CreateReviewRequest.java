package com.example.movieapp.network.model;

import com.google.gson.annotations.SerializedName;

public class CreateReviewRequest {
    @SerializedName("slug")
    private String slug;

    @SerializedName("movieTitle")
    private String movieTitle;

    @SerializedName("rating")
    private double rating;

    @SerializedName("comment")
    private String comment;

    public CreateReviewRequest() {}

    public CreateReviewRequest(String slug, String movieTitle, double rating, String comment) {
        this.slug = slug;
        this.movieTitle = movieTitle;
        this.rating = rating;
        this.comment = comment;
    }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
