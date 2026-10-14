package com.planio.app.controllers;

import com.planio.app.dto.TaskActivityDTO;
import com.planio.app.services.TaskActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/activity")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class TaskActivityController {

    private final TaskActivityService taskActivityService;

    @GetMapping
    public List<TaskActivityDTO> getActivity(@PathVariable Long taskId) {

        return taskActivityService.findByTaskId(taskId);
    }
}
