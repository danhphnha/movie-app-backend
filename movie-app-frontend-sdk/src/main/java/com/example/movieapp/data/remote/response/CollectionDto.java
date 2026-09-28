package com.example.movieapp.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class CollectionDto {

    @SerializedName("id")
    private String id;

    @SerializedName("collection_name")
    private String collectionName;

    @SerializedName("userId")
    private String userId;

    public CollectionDto() {}

    public CollectionDto(String id, String collectionName, String userId) {
        this.id = id;
        this.collectionName = collectionName;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
