package com.planio.app.services;

import com.planio.app.dto.BoardDTO;
import com.planio.app.entity.Board;
import com.planio.app.entity.User;
import com.planio.app.exceptions.ObjectNotFoundException;
import com.planio.app.repositories.BoardRepository;
import com.planio.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final BoardAccessService boardAccessService;

    private BoardDTO mapToDTO(Board board) {
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setId(board.getId());
        boardDTO.setName(board.getName());
        boardDTO.setOwnerId(board.getOwner().getId());
        return boardDTO;
    }

    public BoardDTO createBoard(BoardDTO boardDTO) {
        log.info("Creating board: {}", boardDTO.getName());
        User owner = userRepository.findById(boardDTO.getOwnerId()).
                orElseThrow(() -> new ObjectNotFoundException("User", boardDTO.getOwnerId()));

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

        User user = currentUserService.getCurrentUser();

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ObjectNotFoundException("Board", boardId));

        boardAccessService.checkAccess(board, user);

        User participant = userRepository.findByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundException("User", email));

        board.getParticipants().add(participant);
        boardRepository.save(board);

    }

    public List<BoardDTO> getAllBoards() {
        log.info("Getting all boards");

        User user = currentUserService.getCurrentUser();

        return boardRepository.findAll()
                .stream()
                .filter(board -> boardAccessService.hasAccess(board, user))
                .map(this::mapToDTO)
                .toList();
    }

    public BoardDTO getBoardById(Long boardId) {
        log.info("Getting board with id: {}", boardId);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ObjectNotFoundException("Board", boardId));

        User user = currentUserService.getCurrentUser();

        boardAccessService.checkAccess(board, user);

        return mapToDTO(board);
    }

    public BoardDTO update(Long id, BoardDTO boardDTO) {
        log.info("Updating board id: {}", id);
        User user = currentUserService.getCurrentUser();

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Board", id));

        boardAccessService.checkAccess(board, user);

        board.setName(boardDTO.getName());

        log.info("Board updated id: {}", id);

        return mapToDTO(boardRepository.save(board));
    }

    public void delete(Long id) {
        log.warn("Deleting board id: {}", id);

        User user = currentUserService.getCurrentUser();

        Board board = boardRepository.findById(id)
                        .orElseThrow(() -> new ObjectNotFoundException("Board", id));

        boardAccessService.checkAccess(board, user);

        boardRepository.deleteById(id);
    }

    public List<BoardDTO> getMyBoards() {

        User user = currentUserService.getCurrentUser();

        List<Board> owned = boardRepository.findByOwner(user);
        List<Board> participant = boardRepository.findByParticipants_Id(user.getId());

        return Stream.concat(owned.stream(), participant.stream())
                .distinct()
                .map(this::mapToDTO)
                .toList();
    }
}
