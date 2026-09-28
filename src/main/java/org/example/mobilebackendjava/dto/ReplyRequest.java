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
}
