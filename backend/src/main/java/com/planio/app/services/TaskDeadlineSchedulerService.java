package com.planio.app.services;


import com.planio.app.entity.Notification;
import com.planio.app.entity.Task;
import com.planio.app.entity.TaskStatus;
import com.planio.app.repositories.NotificationRepository;
import com.planio.app.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskDeadlineSchedulerService {

    private final TaskRepository taskRepository;
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;


    @Scheduled(cron = "0 0 9 * * *")
    public void checkDeadlines(){
        checkTasksForDays(3);

        checkTasksForDays(1);
    }


    private void checkTasksForDays(int days) {
        LocalDate dueDate = LocalDate.now().plusDays(days);
        List<Task> tasks = taskRepository.findByDueDate(dueDate);

        log.info("Checking {} days ahead. Found tasks: {}", days, tasks.size());

        for (Task task : tasks) {
            if (task.getAssignedUser() == null
                    || task.getStatus() == TaskStatus.DONE
                    || task.getStatus() == TaskStatus.CANCELLED) {
                continue;
            }

            boolean shouldSend = (days == 3 && !Boolean.TRUE.equals(task.getReminder3DaysSent()))
                    || (days == 1 && !Boolean.TRUE.equals(task.getReminder1DaySent()));

            if (!shouldSend) {
                continue;
            }

            try {
                send(task, days);
                if (days == 3) {
                    task.setReminder3DaysSent(true);
                } else {
                    task.setReminder1DaySent(true);
                }
                taskRepository.save(task);
            } catch (RuntimeException e) {
                log.error("Failed to send reminder for task {}", task.getId(), e);
            }
        }
    }

    private void send(Task task, int days){
        emailService.sendDeadlineReminder(
                task.getAssignedUser().getEmail(),
                task.getTitle(),
                days
        );

        Notification notification = Notification.builder()
                .user(task.getAssignedUser())
                .task(task)
                .type(days + "_DAYS_BEFORE_DEADLINE")
                .sentAt(LocalDateTime.now())
                .read(false)
                .build();


        notificationRepository.save(notification);
        log.info(
                "Deadline reminder sent for task {}", task.getId()
        );
    }
}
