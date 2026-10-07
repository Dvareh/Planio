package com.planio.app.controllers;

import com.planio.app.dto.LabelDTO;
import com.planio.app.services.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards/{boardId}/labels")
@RequiredArgsConstructor
@Tag(name = "Label API", description = "Operations related to board labels")
@PreAuthorize("isAuthenticated()")
public class LabelController {

    private final LabelService labelService;

    @Operation(summary = "Get board labels")
    @GetMapping
    public List<LabelDTO> getLabels(
            @PathVariable Long boardId) {

        return labelService.getBoardLabels(boardId);
    }

    @Operation(summary = "Create board label")
    @PostMapping
    public LabelDTO createLabel(
            @PathVariable Long boardId,
            @Valid @RequestBody LabelDTO labelDTO) {

        return labelService.createLabel(boardId, labelDTO);
    }

    @Operation(summary = "Delete board label")
    @DeleteMapping("/{labelId}")
    public void deleteLabel(
            @PathVariable Long boardId,
            @PathVariable Long labelId) {

        labelService.deleteLabel(boardId, labelId);
    }
}