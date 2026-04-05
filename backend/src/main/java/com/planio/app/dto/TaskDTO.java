package com.planio.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskDTO {

    private Long id;

    @NotBlank
    private String title;

    private String description;

    private LocalDate dueDate;

    private String status;

    private Long boardId;
}
