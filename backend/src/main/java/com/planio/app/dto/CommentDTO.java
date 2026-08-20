package com.planio.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentDTO {

    private Long id;

    @NotBlank(message = "Comment text cannot be empty")
    private String text;

    private Long taskId;

    private Long userId;
}
