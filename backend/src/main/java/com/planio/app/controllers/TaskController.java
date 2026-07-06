package com.planio.app.controllers;


import com.planio.app.dto.TaskDTO;
import com.planio.app.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task API", description = "Manage tasks")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Create task")
    @PostMapping
    public TaskDTO create(@RequestBody @Valid TaskDTO taskDTO) {
        return taskService.create(taskDTO);
    }

    @Operation(summary = "Get task by ID")
    @GetMapping("/{id}")
    public TaskDTO getById(@PathVariable Long id) {
        return taskService.getById(id);
    }

    @Operation(summary = "Get all tasks")
    @GetMapping
    public List<TaskDTO> getAll() {
        return taskService.getAll();
    }

    @Operation(summary = "Update task")
    @PutMapping("/{id}")
    public TaskDTO update(@PathVariable Long id,
                          @RequestBody @Valid TaskDTO taskDTO) {
        return taskService.update(id, taskDTO);
    }

    @Operation(summary = "Delete task")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }

    @Operation(summary = "Get your tasks")
    @GetMapping("/my")
    public List<TaskDTO> getMyTasks() {
        return taskService.getMyTasks();
    }
}