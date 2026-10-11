package com.planio.app.dto;

import com.planio.app.entity.TaskPriority;
import com.planio.app.entity.TaskStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TaskDTO {

    private Long id;

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in future")
    private LocalDate dueDate;

    private TaskStatus status;

    @NotNull(message = "Board is required")
    private Long boardId;

    private Long assignedUserId;

    private TaskPriority priority;

    private List<LabelDTO> labels;

    private String taskKey;
}
