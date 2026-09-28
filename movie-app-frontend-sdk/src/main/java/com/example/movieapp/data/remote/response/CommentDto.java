package com.example.movieapp.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class CommentDto {

    @SerializedName("id")
    private String id;

    @SerializedName("username")
    private String username;

    @SerializedName("comment")
    private String comment;

    @SerializedName("rating")
    private Double rating;

    @SerializedName("userId")
    private String userId;

    @SerializedName("slug")
    private String slug;

    @SerializedName("movieTitle")
    private String movieTitle;

    @SerializedName("parentId")
    private String parentId;

    @SerializedName("hidden")
    private boolean hidden;

    public CommentDto() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isReply() {
        return parentId != null && !parentId.trim().isEmpty();
    }
}
