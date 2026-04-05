package com.planio.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentDTO {

    private Long id;

    @NotBlank
    private String text;

    private Long taskId;

    private Long userId;
}
