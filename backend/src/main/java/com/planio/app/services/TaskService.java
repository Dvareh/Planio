package com.planio.app.services;


import com.planio.app.dto.TaskDTO;
import com.planio.app.entity.Board;
import com.planio.app.entity.Task;
import com.planio.app.repositories.BoardRepository;
import com.planio.app.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;

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
        Board board = boardRepository.findById(taskDTO.getBoardId()).orElseThrow();

        Task task = Task.builder()
                .title(taskDTO.getTitle())
                .description(taskDTO.getDescription())
                .dueDate(taskDTO.getDueDate())
                .status(taskDTO.getStatus())
                .board(board)
                .build();

        Task saved = taskRepository.save(task);

        log.info("Task created with id: {}", saved.getId());

        return mapToDTO(saved);
    }

    public TaskDTO getById(Long id) {
        log.info("Fetching task by id: {}", id);

        Task task = taskRepository.findById(id).orElseThrow();

        return mapToDTO(task);
    }

    public List<TaskDTO> getAll() {
        log.info("Fetching all tasks");

        return taskRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public TaskDTO update(Long id, TaskDTO taskDTO) {
        log.info("Updating task id: {}", id);

        Task task = taskRepository.findById(id).orElseThrow();

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setDueDate(taskDTO.getDueDate());

        Task updated = taskRepository.save(task);

        log.info("Task updated id: {}", updated.getId());

        return mapToDTO(updated);
    }

    public void delete(Long id) {
        log.warn("Deleting task id: {}", id);

        taskRepository.deleteById(id);
    }
}
