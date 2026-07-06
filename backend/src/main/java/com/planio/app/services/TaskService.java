package com.planio.app.services;


import com.planio.app.dto.TaskDTO;
import com.planio.app.entity.Board;
import com.planio.app.entity.Task;
import com.planio.app.entity.User;
import com.planio.app.exceptions.ObjectNotFoundException;
import com.planio.app.repositories.BoardRepository;
import com.planio.app.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;
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
        return dto;
    }

    public TaskDTO create(TaskDTO taskDTO) {
        log.info("Creating task: {}", taskDTO.getTitle());

        User user = currentUserService.getCurrentUser();

        Board board = boardRepository.findById(taskDTO.getBoardId())
                .orElseThrow(() -> new ObjectNotFoundException("Board", taskDTO.getBoardId()));

        boardAccessService.checkAccess(board, user);

        Task task = Task.builder()
                .title(taskDTO.getTitle())
                .description(taskDTO.getDescription())
                .dueDate(taskDTO.getDueDate())
                .status(taskDTO.getStatus())
                .board(board)
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

    public List<TaskDTO> getAll() {
        log.info("Fetching all tasks");

        User user = currentUserService.getCurrentUser();

        return taskRepository.findAll()
                .stream()
                .filter(task -> boardAccessService.hasAccess(task.getBoard(), user))
                .map(this::mapToDTO)
                .toList();
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

        List<Task> ownedBoardsTasks = taskRepository.findTasksByBoard_Owner_Id(user.getId());
        List<Task> participantBoardsTasks = taskRepository.findTasksByBoard_Participants_Id(user.getId());

        return Stream.concat(ownedBoardsTasks.stream(), participantBoardsTasks.stream())
                .distinct()
                .map(this::mapToDTO)
                .toList();
    }


}
