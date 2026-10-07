package com.planio.app.dto;

import com.planio.app.entity.TaskPriority;
import com.planio.app.entity.TaskStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TaskDTO {

    private Long id;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    private String description;

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in future")
    private LocalDate dueDate;

    private TaskStatus status;

    private Long boardId;

    private Long assignedUserId;

    private TaskPriority priority;

    private List<LabelDTO> labels;

    private String taskKey;
}
