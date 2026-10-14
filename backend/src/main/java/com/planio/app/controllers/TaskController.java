package com.planio.app.controllers;


import com.planio.app.dto.TaskDTO;
import com.planio.app.dto.UpdateTaskDTO;
import com.planio.app.entity.TaskPriority;
import com.planio.app.entity.TaskStatus;
import com.planio.app.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public TaskDTO create(@RequestBody @Valid TaskDTO taskDTO) {
        return taskService.create(taskDTO);
    }

    @GetMapping("/{id}")
    public TaskDTO getById(@PathVariable Long id) {
        return taskService.getById(id);
    }

    @GetMapping
    public Page<TaskDTO> getTasks(

            @RequestParam(required = false)
            Long boardId,

            @RequestParam(required = false)
            TaskStatus status,

            @RequestParam(required = false)
            TaskPriority priority,

            @RequestParam(required = false)
            Long assignedUserId,

            @RequestParam(required = false)
            Long labelId,

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            LocalDate dueDateFrom,

            @RequestParam(required = false)
            LocalDate dueDateTo,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "dueDate")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction) {

        return taskService.getTasks(
                boardId,
                status,
                priority,
                assignedUserId,
                labelId,
                search,
                dueDateFrom,
                dueDateTo,
                page,
                size,
                sortBy,
                direction
        );
    }

    @PutMapping("/{id}")
    public TaskDTO update(@PathVariable Long id, @RequestBody @Valid UpdateTaskDTO updateTaskDTO) {
        return taskService.update(id, updateTaskDTO);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }

    @GetMapping("/my")
    public List<TaskDTO> getMyTasks() {
        return taskService.getMyTasks();
    }

    @PutMapping("/{taskId}/assign/{userId}")
    public TaskDTO assignTask(@PathVariable Long taskId, @PathVariable Long userId) {
        return taskService.assignTask(taskId, userId);
    }

    @DeleteMapping("/{taskId}/assign")
    public TaskDTO unassignTask(@PathVariable Long taskId) {
        return taskService.unassignTask(taskId);
    }

    @PostMapping("/{taskId}/labels/{labelId}")
    public TaskDTO addLabel(@PathVariable Long taskId, @PathVariable Long labelId) {
        return taskService.addLabel(taskId, labelId);
    }

    @DeleteMapping("/{taskId}/labels/{labelId}")
    public TaskDTO removeLabel(@PathVariable Long taskId, @PathVariable Long labelId) {
        return taskService.removeLabel(taskId, labelId);
    }

    @PutMapping("/{taskId}/status")
    public TaskDTO updateStatus(@PathVariable Long taskId, @RequestParam TaskStatus status) {
        return taskService.updateStatus(taskId, status);
    }

    @PutMapping("/{taskId}/priority")
    public TaskDTO updatePriority(@PathVariable Long taskId, @RequestParam TaskPriority priority) {
        return taskService.updatePriority(taskId, priority);
    }
}