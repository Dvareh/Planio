package com.planio.app.controllers;

import com.planio.app.dto.BoardDTO;
import com.planio.app.dto.UserDTO;
import com.planio.app.services.BoardService;
import com.planio.app.services.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final CurrentUserService currentUserService;

    @PostMapping
    public BoardDTO create(@RequestBody @Valid BoardDTO boardDTO) {
        return boardService.createBoard(boardDTO);
    }

    @GetMapping("/{id}")
    public BoardDTO getById(@PathVariable Long id) {
        return boardService.getBoardById(id);
    }

    @PutMapping("/{id}")
    public BoardDTO update(@PathVariable Long id,
                           @RequestBody @Valid BoardDTO boardDTO) {
        return boardService.update(id, boardDTO);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        boardService.delete(id);
    }

    @PostMapping("/{boardId}/participants")
    public void addParticipant(@PathVariable Long boardId,
                               @RequestParam String email) {
        boardService.addParticipant(boardId, email);
    }

    @GetMapping("/my")
    public List<BoardDTO> getMyBoards() {
        return boardService.getMyBoards();
    }

    @GetMapping("/{boardId}/participants")
    public List<UserDTO> getParticipants(@PathVariable Long boardId) {
        return boardService.getParticipants(boardId);
    }

    @DeleteMapping("/{boardId}/participants/{userId}")
    public void removeParticipant(
            @PathVariable Long boardId,
            @PathVariable Long userId) {

        boardService.removeParticipant(boardId, userId);
    }

    @DeleteMapping("/{boardId}/participants/me")
    public void leaveBoard(@PathVariable Long boardId) {
        boardService.leaveBoard(boardId);
    }
}