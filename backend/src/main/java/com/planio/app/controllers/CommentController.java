package com.planio.app.controllers;


import com.planio.app.dto.CommentDTO;
import com.planio.app.services.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentDTO create(@RequestBody @Valid CommentDTO commentDTO) {
        return commentService.create(commentDTO);
    }

    @GetMapping("/task/{taskId}")
    public List<CommentDTO> getByTask(@PathVariable Long taskId) {
        return commentService.getByTask(taskId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        commentService.delete(id);
    }

    @PutMapping("/{id}")
    public CommentDTO update(
            @PathVariable Long id,
            @RequestBody @Valid CommentDTO commentDTO) {
        return commentService.update(id, commentDTO);
    }
}