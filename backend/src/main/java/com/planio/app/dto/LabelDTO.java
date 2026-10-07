package com.planio.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LabelDTO {

    private Long id;

    @NotBlank(message = "Label name cannot be empty")
    @Size(max = 30, message = "Label name cannot be longer than 30 characters")
    private String name;

    private Long boardId;
}
