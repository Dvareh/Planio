package com.planio.app.services;

import com.planio.app.dto.BoardDTO;
import com.planio.app.dto.UserDTO;
import com.planio.app.entity.*;
import com.planio.app.exceptions.ObjectNotFoundException;
import com.planio.app.repositories.BoardRepository;
import com.planio.app.repositories.LabelRepository;
import com.planio.app.repositories.TaskRepository;
import com.planio.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final BoardAccessService boardAccessService;
    private final LabelRepository labelRepository;
    private final TaskRepository taskRepository;
    private final TaskActivityService activityService;
    private final TaskService taskService;

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


        boolean alreadyMember = board.getParticipants().stream()
                .anyMatch(member -> member.getId().equals(participant.getId()));

        if (board.getOwner().getId().equals(participant.getId()) || alreadyMember) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already belongs to this board");
        }

        board.getParticipants().add(participant);
        boardRepository.save(board);

    }

    @Transactional(readOnly = true)
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
        User currentUser = currentUserService.getCurrentUser();

        Board board = boardRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ObjectNotFoundException("Board", id));

        boardAccessService.checkOwner(board, currentUser);

        List<Task> tasks = taskRepository.findByBoard_Id(id);

        for (Task task : tasks) {
            taskService.delete(task.getId());
        }

        labelRepository.deleteByBoard_Id(id);
        labelRepository.flush();

        board.getParticipants().clear();
        boardRepository.delete(board);

        log.info("Board {} deleted", id);
    }

    @Transactional(readOnly = true)
    public List<BoardDTO> getMyBoards() {
        User user = currentUserService.getCurrentUser();

        return boardRepository.findAccessibleBoards(user)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
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

        if (board.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Board owner cannot be removed");
        }

        User participant = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User", userId));

        boolean removed = board.getParticipants().removeIf(user -> user.getId().equals(userId));

        if (!removed) {
            throw new ObjectNotFoundException("Participant", userId);
        }

        List<Task> tasks = taskRepository.findByBoard_IdAndAssignedUser_Id(boardId, userId);

        for (Task task : tasks) {
            activityService.record(task, currentUser, ActivityType.ASSIGNEE_CHANGED, participant.getUsername(), null);
            task.setAssignedUser(null);
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

    @Transactional
    public void leaveBoard(Long boardId) {
        User currentUser = currentUserService.getCurrentUser();

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ObjectNotFoundException("Board", boardId));

        if (board.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Board owner cannot leave the board");
        }

        boardAccessService.checkAccess(board, currentUser);

        board.getParticipants().removeIf(user -> user.getId().equals(currentUser.getId()));

        List<Task> tasks = taskRepository.findByBoard_IdAndAssignedUser_Id(boardId, currentUser.getId());

        for (Task task : tasks) {
            activityService.record(task, currentUser, ActivityType.ASSIGNEE_CHANGED, currentUser.getUsername(), null);
            task.setAssignedUser(null);
        }

        boardRepository.save(board);

        log.info("User {} left board {}", currentUser.getId(), boardId);
    }
}
