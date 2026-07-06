package com.planio.app.controllers;

import com.planio.app.dto.BoardDTO;
import com.planio.app.entity.User;
import com.planio.app.repositories.BoardRepository;
import com.planio.app.services.BoardService;
import com.planio.app.services.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Tag(name = "Board API", description = "Manage boards")
public class BoardController {

    private final BoardService boardService;
    private final CurrentUserService currentUserService;

    @Operation(summary = "Create board")
    @PostMapping
    public BoardDTO create(@RequestBody @Valid BoardDTO boardDTO) {
        return boardService.createBoard(boardDTO);
    }

    @Operation(summary = "Get board by ID")
    @GetMapping("/{id}")
    public BoardDTO getById(@PathVariable Long id) {
        return boardService.getBoardById(id);
    }

    @Operation(summary = "Get all boards")
    @GetMapping
    public List<BoardDTO> getAll() {
        return boardService.getAllBoards();
    }

    @Operation(summary = "Update board")
    @PutMapping("/{id}")
    public BoardDTO update(@PathVariable Long id,
                           @RequestBody @Valid BoardDTO boardDTO) {
        return boardService.update(id, boardDTO);
    }

    @Operation(summary = "Delete board")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        boardService.delete(id);
    }

    @Operation(summary = "Add participant by email")
    @PostMapping("/{boardId}/participants")
    public void addParticipant(@PathVariable Long boardId,
                               @RequestParam String email) {
        boardService.addParticipant(boardId, email);
    }

    @Operation(summary = "Get your boards")
    @GetMapping("/my")
    public List<BoardDTO> getMyBoards() {
        return boardService.getMyBoards();
    }
}