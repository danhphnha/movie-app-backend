package org.example.mobilebackendjava.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateReplyRequest {
    @NotBlank(message = "Comment text cannot be blank")
    private String comment;

    public UpdateReplyRequest() {}

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
