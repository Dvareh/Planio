package com.planio.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDTO {

    private Long id;
    private Long taskId;
    private String taskTitle;
    private String type;
    private LocalDateTime sentAt;
    private boolean read;
}
