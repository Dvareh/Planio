package com.planio.app.services;

import com.planio.app.dto.CommentDTO;
import com.planio.app.entity.Comment;
import com.planio.app.entity.Task;
import com.planio.app.entity.User;
import com.planio.app.exceptions.ObjectNotFoundException;
import com.planio.app.repositories.CommentRepository;
import com.planio.app.repositories.TaskRepository;
import com.planio.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final BoardAccessService boardAccessService;

    public CommentDTO create(CommentDTO commentDTO) {
        log.info("Creating comment for task: {}", commentDTO.getTaskId());

        User user = currentUserService.getCurrentUser();

        Task task = taskRepository.findById(commentDTO.getTaskId())
                .orElseThrow(() -> new ObjectNotFoundException("Task", commentDTO.getTaskId()));

        boardAccessService.checkAccess(task.getBoard(), user);

        Comment comment = Comment.builder()
                .text(commentDTO.getText())
                .task(task)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();


        log.info("Comment created with id: {}", comment.getId());

        return mapToDTO(commentRepository.save(comment));
    }

    public List<CommentDTO> getByTask(Long taskId) {

        log.info("Getting comments for task: {}", taskId);

        User user = currentUserService.getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ObjectNotFoundException("Task", taskId));

        boardAccessService.checkAccess(task.getBoard(), user);

        return commentRepository.findByTaskId(taskId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public void delete(Long id) {
        log.warn("Deleting comment id: {}", id);

        User user = currentUserService.getCurrentUser();

        Comment comment = commentRepository.findById(id)
                        .orElseThrow(() -> new ObjectNotFoundException("Comment", id));

        boardAccessService.checkAccess(comment.getTask().getBoard(), user);

        commentRepository.delete(comment);
    }

    private CommentDTO mapToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setTaskId(comment.getTask().getId());
        dto.setUserId(comment.getUser().getId());
        return dto;
    }
}
