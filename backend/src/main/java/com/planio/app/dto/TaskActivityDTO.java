package com.planio.app.dto;

import com.planio.app.entity.ActivityType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskActivityDTO {

    private Long id;

    private Long taskId;

    private Long userId;

    private String username;

    private ActivityType type;

    private String oldValue;

    private String newValue;

    private LocalDateTime createdAt;
}
