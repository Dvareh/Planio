package com.planio.app.services;


import com.planio.app.dto.TaskDTO;
import com.planio.app.entity.Board;
import com.planio.app.entity.Task;
import com.planio.app.entity.TaskStatus;
import com.planio.app.entity.User;
import com.planio.app.exceptions.ObjectNotFoundException;
import com.planio.app.repositories.BoardRepository;
import com.planio.app.repositories.TaskRepository;
import com.planio.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final BoardAccessService boardAccessService;

    private TaskDTO mapToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setDueDate(task.getDueDate());
        dto.setStatus(task.getStatus());
        dto.setBoardId(task.getBoard().getId());
        if (task.getAssignedUser() != null) {
            dto.setAssignedUserId(task.getAssignedUser().getId());
        }
        return dto;
    }

    public TaskDTO create(TaskDTO taskDTO) {
        log.info("Creating task: {}", taskDTO.getTitle());

        User user = currentUserService.getCurrentUser();

        Board board = boardRepository.findById(taskDTO.getBoardId())
                .orElseThrow(() -> new ObjectNotFoundException("Board", taskDTO.getBoardId()));

        User assignedUser = null;

        if (taskDTO.getAssignedUserId() != null) {
            assignedUser = userRepository.findById(taskDTO.getAssignedUserId())
                    .orElseThrow(() -> new ObjectNotFoundException("User", taskDTO.getAssignedUserId()));
        }

        boardAccessService.checkAccess(board, user);

        Task task = Task.builder()
                .title(taskDTO.getTitle())
                .description(taskDTO.getDescription())
                .dueDate(taskDTO.getDueDate())
                .status(taskDTO.getStatus() != null ? taskDTO.getStatus() : TaskStatus.TODO)
                .board(board)
                .assignedUser(assignedUser)
                .build();


        log.info("Task created with id: {}", task.getId());

        return mapToDTO(taskRepository.save(task));
    }

    public TaskDTO getById(Long id) {
        log.info("Fetching task by id: {}", id);

        User user = currentUserService.getCurrentUser();


        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Task", id));

        boardAccessService.checkAccess(task.getBoard(), user);

        return mapToDTO(task);
    }

    public Page<TaskDTO> getTasks(TaskStatus status,
                                  String search,
                                  int page,
                                  int size,
                                  String sortBy,
                                  String direction) {

        log.info("Fetching tasks: status={}, search={}, page={}, size={}, sortBy={}, direction={}",
                status, search, page, size, sortBy, direction);

        Sort sort = Sort.by(
                direction.equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                sortBy
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> tasks;

        if (search != null && !search.isEmpty()) {
            tasks = taskRepository.findByTitleContainingIgnoreCase(search, pageable);
        } else if (status != null) {
            tasks = taskRepository.findByStatus(status, pageable);
        } else {
            tasks = taskRepository.findAll(pageable);
        }

        return tasks.map(this::mapToDTO);
    }

    public TaskDTO update(Long id, TaskDTO taskDTO) {
        log.info("Updating task id: {}", id);

        User user = currentUserService.getCurrentUser();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Task", id));

        boardAccessService.checkAccess(task.getBoard(), user);

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setDueDate(taskDTO.getDueDate());


        log.info("Task updated id: {}", task.getId());

        return mapToDTO(taskRepository.save(task));
    }

    public void delete(Long id) {
        log.warn("Deleting task id: {}", id);

        User user = currentUserService.getCurrentUser();

        Task task = taskRepository.findById(id)
                        .orElseThrow(() -> new ObjectNotFoundException("Task", id));

        boardAccessService.checkAccess(task.getBoard(), user);

        taskRepository.deleteById(id);
    }

    public List<TaskDTO> getMyTasks() {

        User user = currentUserService.getCurrentUser();

        log.info("Fetching all tasks for user: {}", user.getEmail());

        return taskRepository.findByAssignedUser(user)
                .stream()
                .map(this::mapToDTO)
                .toList();


    }

    public TaskDTO assignTask(Long taskId, Long userId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ObjectNotFoundException("Task", taskId));

        User currentUser = currentUserService.getCurrentUser();

        boardAccessService.checkAccess(task.getBoard(), currentUser);

        User asignee = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User", userId));

        boolean isOwner = task.getBoard().getOwner().getId().equals(asignee.getId());

        boolean isParticipant = task.getBoard().getParticipants()
                .stream()
                .anyMatch(participant -> participant.getId().equals(asignee.getId()));

        if (!isOwner && !isParticipant) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "User is not a participant of this board"
            );
        }

        task.setAssignedUser(asignee);


        log.info("Task {} assigned to user {}", taskId, userId);

        return mapToDTO(taskRepository.save(task));
    }
}
