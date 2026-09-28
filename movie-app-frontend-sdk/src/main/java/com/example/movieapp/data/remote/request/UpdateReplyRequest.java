package com.example.movieapp.data.remote.request;

import com.google.gson.annotations.SerializedName;

public class UpdateReplyRequest {

    @SerializedName("comment")
    private String comment;

    public UpdateReplyRequest() {}

    public UpdateReplyRequest(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
