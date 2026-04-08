package com.planio.app.controllers;


import com.planio.app.dto.CommentDTO;
import com.planio.app.services.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comment API", description = "Manage comments")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Create comment")
    @PostMapping
    public CommentDTO create(@RequestBody @Valid CommentDTO commentDTO) {
        return commentService.create(commentDTO);
    }

    @Operation(summary = "Get comments by task")
    @GetMapping("/task/{taskId}")
    public List<CommentDTO> getByTask(@PathVariable Long taskId) {
        return commentService.getByTask(taskId);
    }

    @Operation(summary = "Delete comment")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        commentService.delete(id);
    }
}