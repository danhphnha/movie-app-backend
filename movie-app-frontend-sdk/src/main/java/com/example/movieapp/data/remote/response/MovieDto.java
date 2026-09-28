package com.example.movieapp.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class MovieDto {

    @SerializedName("name")
    private String name;

    @SerializedName("slug")
    private String slug;

    @SerializedName("poster_url")
    private String posterUrl;

    public MovieDto() {}

    public MovieDto(String name, String slug, String posterUrl) {
        this.name = name;
        this.slug = slug;
        this.posterUrl = posterUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
}
