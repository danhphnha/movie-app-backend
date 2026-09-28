package com.example.movieapp.data.remote.request;

import com.google.gson.annotations.SerializedName;

public class CreateCollectionRequest {

    @SerializedName("collectionName")
    private String collectionName;

    public CreateCollectionRequest() {}

    public CreateCollectionRequest(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
}
