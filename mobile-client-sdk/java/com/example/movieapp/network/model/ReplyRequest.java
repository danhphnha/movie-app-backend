package com.example.movieapp.network.model;

import com.google.gson.annotations.SerializedName;

public class ReplyRequest {
    @SerializedName("parentId")
    private String parentId;

    @SerializedName("comment")
    private String comment;

    @SerializedName("slug")
    private String slug;

    @SerializedName("movieTitle")
    private String movieTitle;

    public ReplyRequest() {}

    public ReplyRequest(String parentId, String comment, String slug, String movieTitle) {
        this.parentId = parentId;
        this.comment = comment;
        this.slug = slug;
        this.movieTitle = movieTitle;
    }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
}
