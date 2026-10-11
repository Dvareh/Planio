package com.planio.app.services;

import com.planio.app.dto.TaskActivityDTO;
import com.planio.app.entity.ActivityType;
import com.planio.app.entity.Task;
import com.planio.app.entity.TaskActivity;
import com.planio.app.entity.User;
import com.planio.app.exceptions.ObjectNotFoundException;
import com.planio.app.repositories.TaskActivityRepository;
import com.planio.app.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskActivityService {

    private final TaskActivityRepository taskActivityRepository;
    private final TaskRepository taskRepository;
    private final CurrentUserService currentUserService;
    private final BoardAccessService boardAccessService;

    @Transactional
    public void record(
            Task task,
            User user,
            ActivityType type,
            Object oldValue,
            Object newValue
    ){

        if (Objects.equals(oldValue, newValue)) {
            return;
        }

        TaskActivity activity = TaskActivity.builder()
                .task(task)
                .user(user)
                .username(user.getUsername())
                .type(type)
                .oldValue(oldValue != null ? oldValue.toString() : null)
                .newValue(newValue != null ? newValue.toString() : null)
                .createdAt(LocalDateTime.now())
                .build();

        taskActivityRepository.save(activity);

        log.info("New activity: {} for task {}", type, task.getId());
    }

    @Transactional(readOnly = true)
    public List<TaskActivityDTO> findByTaskId(Long taskId) {

        User currentUser = currentUserService.getCurrentUser();

        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ObjectNotFoundException("Task", taskId));

        boardAccessService.checkAccess(task.getBoard(), currentUser);

        List<TaskActivity> activities = taskActivityRepository.findByTask_IdOrderByCreatedAtDesc(taskId);

        List<TaskActivityDTO> result = new ArrayList<>();

        for (TaskActivity ta : activities) {
            result.add(mapToDTO(ta));
        }

        return result;
    }

    private TaskActivityDTO mapToDTO(
            TaskActivity activity) {

        TaskActivityDTO activityDTO = new TaskActivityDTO();

        activityDTO.setId(activity.getId());

        activityDTO.setTaskId(activity.getTask().getId());

        if (activity.getUser() != null) {
            activityDTO.setUserId(activity.getUser().getId());
        }

        activityDTO.setUsername(activity.getUsername());

        activityDTO.setType(activity.getType());

        activityDTO.setOldValue(activity.getOldValue());

        activityDTO.setNewValue(activity.getNewValue());

        activityDTO.setCreatedAt(activity.getCreatedAt());

        return activityDTO;
    }

}
