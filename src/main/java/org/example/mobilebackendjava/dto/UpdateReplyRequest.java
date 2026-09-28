package org.example.mobilebackendjava.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateReplyRequest {
    @NotBlank(message = "Comment text cannot be blank")
    private String comment;
}
