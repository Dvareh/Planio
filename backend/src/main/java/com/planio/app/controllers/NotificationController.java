package com.planio.app.controllers;

import com.planio.app.dto.NotificationDTO;
import com.planio.app.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationDTO> getMyNotifications() {
        return notificationService.getMyNotifications();
    }

    @GetMapping("/unread")
    public List<NotificationDTO> getMyUnreadNotifications() {
        return notificationService.getMyUnreadNotifications();
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }

    @PutMapping("/read-all")
    public void markAllAsRead() {
        notificationService.markAllAsRead();
    }
}
