package com.planio.app.services;


import com.planio.app.dto.LabelDTO;
import com.planio.app.dto.TaskDTO;
import com.planio.app.entity.*;
import com.planio.app.exceptions.ObjectNotFoundException;
import com.planio.app.repositories.BoardRepository;
import com.planio.app.repositories.LabelRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
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
    private static final Set<String> ALLOWED_SORTS = Set.of(
            "dueDate",
            "title",
            "status"
    );
    private final LabelRepository labelRepository;

    private TaskDTO mapToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setDueDate(task.getDueDate());
        dto.setStatus(task.getStatus());
        dto.setBoardId(task.getBoard().getId());
        dto.setPriority(task.getPriority());
        dto.setTaskKey(task.getBoard().getKey() + "-" + task.getTaskNumber());
        if (task.getAssignedUser() != null) {
            dto.setAssignedUserId(task.getAssignedUser().getId());
        }

        List<LabelDTO> labels = new ArrayList<>();

        for (Label label : task.getLabels()) {

            LabelDTO labelDTO = new LabelDTO();

            labelDTO.setId(label.getId());
            labelDTO.setName(label.getName());
            labelDTO.setBoardId(label.getBoard().getId());
            labels.add(labelDTO);
        }

        labels.sort(Comparator.comparing(LabelDTO::getName));

        dto.setLabels(labels);

        return dto;
    }

    @Transactional
    public TaskDTO create(TaskDTO taskDTO) {
        log.info("Creating task: {}", taskDTO.getTitle());

        User user = currentUserService.getCurrentUser();

        Board board = boardRepository.findByIdForUpdate(taskDTO.getBoardId())
                .orElseThrow(() -> new ObjectNotFoundException("Board", taskDTO.getBoardId()));

        User assignedUser = null;

        if (taskDTO.getAssignedUserId() != null) {
            assignedUser = userRepository.findById(taskDTO.getAssignedUserId())
                    .orElseThrow(() -> new ObjectNotFoundException("User", taskDTO.getAssignedUserId()));
        }

        boardAccessService.checkAccess(board, user);

        Long taskNumber = board.getNextTaskNumber();

        board.setNextTaskNumber(taskNumber + 1);

        Task task = Task.builder()
                .title(taskDTO.getTitle())
                .description(taskDTO.getDescription())
                .dueDate(taskDTO.getDueDate())
                .status(taskDTO.getStatus() != null ? taskDTO.getStatus() : TaskStatus.TODO)
                .board(board)
                .assignedUser(assignedUser)
                .priority(taskDTO.getPriority() != null ? taskDTO.getPriority() : TaskPriority.MEDIUM)
                .taskNumber(taskNumber)
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

    @Transactional(readOnly = true)
    public Page<TaskDTO> getTasks(
            Long boardId,
            TaskStatus status,
            TaskPriority priority,
            Long assignedUserId,
            Long labelId,
            String search,
            LocalDate dueDateFrom,
            LocalDate dueDateTo,
            int page,
            int size,
            String sortBy,
            String direction) {

        User user = currentUserService.getCurrentUser();

        if (!ALLOWED_SORTS.contains(sortBy)) {
            sortBy = "dueDate";
        }

        Sort sort = Sort.by(direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        if (search == null) {
            search = "";
        }

        log.info(
                "Fetching tasks: boardId={}, status={}, priority={}, assignedUserId={}, labelId={}, search={}",
                boardId,
                status,
                priority,
                assignedUserId,
                labelId,
                search
        );

        return taskRepository.searchTasks(
                        user,
                        boardId,
                        search,
                        status,
                        priority,
                        assignedUserId,
                        labelId,
                        dueDateFrom,
                        dueDateTo,
                        pageable
                )
                .map(this::mapToDTO);
    }


    @Transactional
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
        task.setPriority(taskDTO.getPriority());


        log.info("Task updated id: {}", task.getId());

        return mapToDTO(taskRepository.save(task));
    }

    @Transactional
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

    @Transactional
    public TaskDTO assignTask(Long taskId, Long userId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ObjectNotFoundException("Task", taskId));

        User currentUser = currentUserService.getCurrentUser();

        boardAccessService.checkAccess(task.getBoard(), currentUser);

        User assignee = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User", userId));

        boolean isOwner = task.getBoard().getOwner().getId().equals(assignee.getId());

        boolean isParticipant = task.getBoard().getParticipants()
                .stream()
                .anyMatch(participant -> participant.getId().equals(assignee.getId()));

        if (!isOwner && !isParticipant) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a participant of this board");
        }

        task.setAssignedUser(assignee);


        log.info("Task {} assigned to user {}", taskId, userId);

        return mapToDTO(taskRepository.save(task));
    }

    @Transactional
    public TaskDTO unassignTask(Long taskId) {
        log.info("Unassigning task {}", taskId);

        User currentUser = currentUserService.getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ObjectNotFoundException("Task", taskId));

        boardAccessService.checkAccess(task.getBoard(), currentUser);

        task.setAssignedUser(null);

        log.info("Task {} unassigned", taskId);

        return mapToDTO(taskRepository.save(task));
    }

    @Transactional
    public TaskDTO addLabel(Long taskId, Long labelId) {

        User currentUser = currentUserService.getCurrentUser();

        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ObjectNotFoundException("Task", taskId));

        boardAccessService.checkAccess(task.getBoard(), currentUser);

        Label label = labelRepository.findById(labelId).orElseThrow(() -> new ObjectNotFoundException("Label", labelId));

        if (label.getBoard().getId() != (task.getBoard().getId())) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Label does not belong to this board");
        }

        boolean alreadyAdded = false;

        for (Label taskLabel : task.getLabels()) {

            if (taskLabel.getId().equals(labelId)) {
                alreadyAdded = true;
                break;
            }
        }

        if (!alreadyAdded) {
            task.getLabels().add(label);
        }

        return mapToDTO(taskRepository.save(task));
    }

    @Transactional
    public TaskDTO removeLabel(Long taskId, Long labelId) {

        User currentUser = currentUserService.getCurrentUser();

        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ObjectNotFoundException("Task", taskId));

        boardAccessService.checkAccess(task.getBoard(), currentUser);

        Label labelToRemove = null;

        for (Label label : task.getLabels()) {

            if (label.getId().equals(labelId)) {
                labelToRemove = label;
                break;
            }
        }

        if (labelToRemove != null) {
            task.getLabels().remove(labelToRemove);
        }

        return mapToDTO(taskRepository.save(task));
    }

    @Transactional
    public TaskDTO updateStatus(Long taskId, TaskStatus status) {

        User currentUser = currentUserService.getCurrentUser();

        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ObjectNotFoundException("Task", taskId));

        boardAccessService.checkAccess(task.getBoard(), currentUser);

        task.setStatus(status);

        log.info("Task {} status changed to {}", taskId, status);

        return mapToDTO(taskRepository.save(task));
    }
}
