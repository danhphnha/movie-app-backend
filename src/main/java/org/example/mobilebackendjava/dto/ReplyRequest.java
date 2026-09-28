package org.example.mobilebackendjava.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReplyRequest {
    @NotBlank(message = "Parent comment ID is required")
    private String parentId;

    @NotBlank(message = "Comment text cannot be blank")
    private String comment;

    private String slug;
    private String movieTitle;

    public ReplyRequest() {}

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
}
