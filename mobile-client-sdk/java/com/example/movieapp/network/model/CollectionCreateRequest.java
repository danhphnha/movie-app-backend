package com.example.movieapp.network.model;

import com.google.gson.annotations.SerializedName;

public class CollectionCreateRequest {
    @SerializedName("collectionName")
    private String collectionName;

    public CollectionCreateRequest() {}

    public CollectionCreateRequest(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() { return collectionName; }
    public void setCollectionName(String collectionName) { this.collectionName = collectionName; }
}
