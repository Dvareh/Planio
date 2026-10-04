package com.planio.app.services;

import com.planio.app.dto.NotificationDTO;
import com.planio.app.entity.Notification;
import com.planio.app.entity.User;
import com.planio.app.exceptions.AccessDeniedException;
import com.planio.app.exceptions.ObjectNotFoundException;
import com.planio.app.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CurrentUserService currentUserService;

    public List<NotificationDTO> getMyNotifications() {

        User user = currentUserService.getCurrentUser();

        return notificationRepository
                .findByUserOrderBySentAtDesc(user)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<NotificationDTO> getMyUnreadNotifications() {

        User user = currentUserService.getCurrentUser();

        return notificationRepository
                .findByUserAndReadFalseOrderBySentAtDesc(user)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public void markAsRead(Long id) {

        User user = currentUserService.getCurrentUser();

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() ->
                        new ObjectNotFoundException("Notification", id));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException(
                    "You can only update your own notifications"
            );
        }

        notification.setRead(true);

        notificationRepository.save(notification);

        log.info("Notification {} marked as read", id);
    }

    @Transactional
    public void markAllAsRead() {

        User user = currentUserService.getCurrentUser();

        List<Notification> notifications =
                notificationRepository.findByUserAndReadFalseOrderBySentAtDesc(user);

        for (Notification notification : notifications) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(notifications);

        log.info(
                "Marked {} notifications as read for user {}",
                notifications.size(),
                user.getId()
        );
    }

    private NotificationDTO mapToDTO(Notification notification) {

        NotificationDTO dto = new NotificationDTO();

        dto.setId(notification.getId());
        dto.setTaskId(notification.getTask().getId());
        dto.setTaskTitle(notification.getTask().getTitle());
        dto.setType(notification.getType());
        dto.setSentAt(notification.getSentAt());
        dto.setRead(notification.isRead());

        return dto;
    }
}
