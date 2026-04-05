package com.planio.app.services;


import com.planio.app.dto.TaskDTO;
import com.planio.app.entity.Board;
import com.planio.app.entity.Task;
import com.planio.app.repositories.BoardRepository;
import com.planio.app.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
        Board board = boardRepository.findById(taskDTO.getBoardId()).orElseThrow();

        Task task = Task.builder()
                .title(taskDTO.getTitle())
                .description(taskDTO.getDescription())
                .dueDate(taskDTO.getDueDate())
                .status(taskDTO.getStatus())
                .board(board)
                .build();

        return mapToDTO(taskRepository.save(task));
    }
}
