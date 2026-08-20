package com.planio.app.services;


import com.planio.app.entity.Notification;
import com.planio.app.entity.Task;
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


    private void checkTasksForDays(int days){
        LocalDate start = LocalDate.now()
                .plusDays(days);

        LocalDate end = start.plusDays(1);

        List<Task> tasks = taskRepository.findByDueDateBetween(start, end);

        log.info(
                "Checking {} days ahead. Found tasks: {}",
                days,
                tasks.size()
        );



        for(Task task : tasks){
            if(task.getAssignedUser() == null){
                continue;
            }
            if(days == 3 && !task.getReminder3DaysSent()){
                send(task, days);
                task.setReminder3DaysSent(true);
            }
            if(days == 1 && !task.getReminder1DaySent()){
                send(task, days);
                task.setReminder1DaySent(true);
            }
            taskRepository.save(task);
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
                .build();


        notificationRepository.save(notification);
        log.info(
                "Deadline reminder sent for task {}", task.getId()
        );
    }
}
