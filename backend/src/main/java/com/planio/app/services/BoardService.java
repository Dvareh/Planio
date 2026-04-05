package com.planio.app.services;

import com.planio.app.dto.BoardDTO;
import com.planio.app.entity.Board;
import com.planio.app.entity.User;
import com.planio.app.repositories.BoardRepository;
import com.planio.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
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
        User owner = userRepository.findById(boardDTO.getOwnerId()).orElseThrow();

        Board board = Board.builder()
                .name(boardDTO.getName())
                .owner(owner)
                .participants(new ArrayList<>())
                .build();

        return mapToDTO(boardRepository.save(board));
    }

    public void addParticipant(Long boardId, String email) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        board.getParticipants().add(user);
        boardRepository.save(board);

    }
}
