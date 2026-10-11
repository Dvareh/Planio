package com.planio.app.repositories;

import com.planio.app.entity.Notification;
import com.planio.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderBySentAtDesc(User user);

    List<Notification> findByUserAndReadFalseOrderBySentAtDesc(User user);
}
