package com.planio.app.controllers;

import com.planio.app.dto.LabelDTO;
import com.planio.app.services.LabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards/{boardId}/labels")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class LabelController {

    private final LabelService labelService;

    @GetMapping
    public List<LabelDTO> getLabels(
            @PathVariable Long boardId) {

        return labelService.getBoardLabels(boardId);
    }

    @PostMapping
    public LabelDTO createLabel(
            @PathVariable Long boardId,
            @Valid @RequestBody LabelDTO labelDTO) {

        return labelService.createLabel(boardId, labelDTO);
    }

    @DeleteMapping("/{labelId}")
    public void deleteLabel(
            @PathVariable Long boardId,
            @PathVariable Long labelId) {

        labelService.deleteLabel(boardId, labelId);
    }
}