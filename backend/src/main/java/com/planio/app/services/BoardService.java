package com.planio.app.services;

import com.planio.app.dto.BoardDTO;
import com.planio.app.dto.UserDTO;
import com.planio.app.entity.Board;
import com.planio.app.entity.Label;
import com.planio.app.entity.User;
import com.planio.app.exceptions.ObjectNotFoundException;
import com.planio.app.repositories.BoardRepository;
import com.planio.app.repositories.LabelRepository;
import com.planio.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


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
    private final LabelRepository labelRepository;

    private BoardDTO mapToDTO(Board board) {
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setId(board.getId());
        boardDTO.setName(board.getName());
        boardDTO.setOwnerId(board.getOwner().getId());
        boardDTO.setDescription(board.getDescription());
        boardDTO.setKey(board.getKey());
        return boardDTO;
    }

    private UserDTO mapUserToDTO(User user) {

        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());

        return userDTO;
    }

    @Transactional
    public BoardDTO createBoard(BoardDTO boardDTO) {

        User user = currentUserService.getCurrentUser();

        String boardKey = prepareBoardKey(
                boardDTO.getKey(),
                boardDTO.getName()
        );

        Board board = Board.builder()
                .name(boardDTO.getName())
                .owner(user)
                .participants(new ArrayList<>())
                .description(boardDTO.getDescription())
                .key(boardKey)
                .nextTaskNumber(1L)
                .build();

        Board savedBoard = boardRepository.save(board);

        createStartLabels(savedBoard);

        return mapToDTO(savedBoard);
    }

    @Transactional
    public void addParticipant(Long boardId, String email) {
        log.info("Adding {} to board {}", email, boardId);

        User user = currentUserService.getCurrentUser();

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ObjectNotFoundException("Board", boardId));

        boardAccessService.checkOwner(board, user);

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

    @Transactional
    public BoardDTO update(Long id, BoardDTO boardDTO) {
        log.info("Updating board id: {}", id);
        User user = currentUserService.getCurrentUser();

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Board", id));

        boardAccessService.checkOwner(board, user);

        board.setName(boardDTO.getName());
        board.setDescription(boardDTO.getDescription());

        log.info("Board updated id: {}", id);

        return mapToDTO(boardRepository.save(board));
    }

    @Transactional
    public void delete(Long id) {
        log.warn("Deleting board id: {}", id);

        User user = currentUserService.getCurrentUser();

        Board board = boardRepository.findById(id)
                        .orElseThrow(() -> new ObjectNotFoundException("Board", id));

        boardAccessService.checkOwner(board, user);

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

    public List<UserDTO> getParticipants(Long boardId) {
        User user = currentUserService.getCurrentUser();

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ObjectNotFoundException("Board", boardId));

        boardAccessService.checkAccess(board, user);

        List<User> users = new ArrayList<>();
        users.add(board.getOwner());
        users.addAll(board.getParticipants());

        return users.stream()
                .map(this::mapUserToDTO)
                .toList();
    }

    @Transactional
    public void removeParticipant(Long boardId, Long userId) {
        log.info("Removing user {} from board {}", userId, boardId);

        User currentUser = currentUserService.getCurrentUser();

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ObjectNotFoundException("Board", boardId));

        boardAccessService.checkOwner(board, currentUser);

        User participant = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User", userId));

        if (!board.getParticipants().remove(participant)) {
            throw new ObjectNotFoundException("Participant", userId);
        }

        boardRepository.save(board);

        log.info("User {} removed from board {}", userId, boardId);
    }

    private static final List<String> START_LABELS = List.of(
            "Bug",
            "Feature",
            "Backend",
            "Frontend",
            "Documentation",
            "Testing"
    );

    private void createStartLabels(Board board) {

        List<Label> labels = new ArrayList<>();

        for (String name : START_LABELS) {

            Label label = Label.builder()
                    .name(name)
                    .board(board)
                    .build();

            labels.add(label);
        }

        labelRepository.saveAll(labels);
    }

    private String generateBoardKey(String boardName) {

        String cleanedName = boardName
                .trim()
                .toUpperCase()
                .replaceAll("[^A-Z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        if (cleanedName.isEmpty()) {
            return "DTK";
        }

        String[] words = cleanedName.split(" ");

        String key;

        if (words.length > 1) {

            StringBuilder builder = new StringBuilder();

            for (String word : words) {
                if (!word.isEmpty()) {
                    builder.append(word.charAt(0));
                }
                if (builder.length() == 5) {
                    break;
                }
            }
            key = builder.toString();
        } else {
            String word = words[0];

            key = word.substring(0, Math.min(4, word.length()));
        }

        if (key.length() < 2 || !Character.isLetter(key.charAt(0))) {
            return "DTK";
        }

        return key;
    }

    private String prepareBoardKey(String requestedKey, String boardName) {
        if (requestedKey != null && !requestedKey.isBlank()) {
            String key = requestedKey.trim().toUpperCase();

            if (!key.matches("[A-Z][A-Z0-9]{1,9}")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Board key must contain 2-10 letters or numbers and start with a letter");
            }

            return key;
        }
        return generateBoardKey(boardName);
    }

}
