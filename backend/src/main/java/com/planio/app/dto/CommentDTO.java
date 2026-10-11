package com.planio.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentDTO {

    private Long id;

    @NotBlank(message = "Comment can't be blank")
    @Size(max = 255, message = "Comment cannot exceed 255 characters")
    private String text;

    private Long taskId;

    private Long userId;

    private String username;
}
