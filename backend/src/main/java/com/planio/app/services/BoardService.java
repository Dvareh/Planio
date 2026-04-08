package com.planio.app.services;

import com.planio.app.dto.BoardDTO;
import com.planio.app.entity.Board;
import com.planio.app.entity.User;
import com.planio.app.repositories.BoardRepository;
import com.planio.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    private BoardDTO mapToDTO(Board board) {
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setId(board.getId());
        boardDTO.setName(board.getName());
        boardDTO.setOwnerId(board.getOwner().getId());
        return boardDTO;
    }

    public BoardDTO createBoard(BoardDTO boardDTO) {
        log.info("Creating board: {}", boardDTO.getName());
        User owner = userRepository.findById(boardDTO.getOwnerId()).orElseThrow();

        Board board = Board.builder()
                .name(boardDTO.getName())
                .owner(owner)
                .participants(new ArrayList<>())
                .build();

        Board saved = boardRepository.save(board);

        log.info("Board created with id: {}", saved.getId());

        return mapToDTO(saved);
    }

    public void addParticipant(Long boardId, String email) {
        log.info("Adding {} to board {}", email, boardId);
        Board board = boardRepository.findById(boardId).orElseThrow();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        board.getParticipants().add(user);
        boardRepository.save(board);

    }

    public List<BoardDTO> getAllBoards() {
        log.info("Getting all boards");
        return boardRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public BoardDTO getBoardById(Long boardId) {
        log.info("Getting board with id: {}", boardId);
        return  boardRepository.findById(boardId).map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Board not found"));
    }

    public BoardDTO update(Long id, BoardDTO boardDTO) {
        log.info("Updating board id: {}", id);

        Board board = boardRepository.findById(id).orElseThrow();
        board.setName(boardDTO.getName());
        Board updated = boardRepository.save(board);

        log.info("Board updated id: {}", updated.getId());

        return mapToDTO(updated);
    }

    public void delete(Long id) {
        log.warn("Deleting board id: {}", id);

        boardRepository.deleteById(id);
    }
}
