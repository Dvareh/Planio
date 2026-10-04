package com.planio.app.controllers;

import com.planio.app.dto.NotificationDTO;
import com.planio.app.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification API", description = "Manage notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "Get current user's notifications")
    @GetMapping
    public List<NotificationDTO> getMyNotifications() {
        return notificationService.getMyNotifications();
    }

    @Operation(summary = "Get current user's unread notifications")
    @GetMapping("/unread")
    public List<NotificationDTO> getMyUnreadNotifications() {
        return notificationService.getMyUnreadNotifications();
    }

    @Operation(summary = "Mark notification as read")
    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }

    @Operation(summary = "Mark all notifications as read")
    @PutMapping("/read-all")
    public void markAllAsRead() {
        notificationService.markAllAsRead();
    }
}
