package com.planio.app.services;

import com.planio.app.dto.CommentDTO;
import com.planio.app.entity.ActivityType;
import com.planio.app.entity.Comment;
import com.planio.app.entity.Task;
import com.planio.app.entity.User;
import com.planio.app.exceptions.AccessDeniedException;
import com.planio.app.exceptions.ObjectNotFoundException;
import com.planio.app.repositories.CommentRepository;
import com.planio.app.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final CurrentUserService currentUserService;
    private final BoardAccessService boardAccessService;
    private final TaskActivityService activityService;

    @Transactional
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

        Comment createdComment = commentRepository.save(comment);

        activityService.record(task, user, ActivityType.COMMENT_ADDED, null, comment.getText());

        return mapToDTO(createdComment);
    }

    @Transactional(readOnly = true)
    public List<CommentDTO> getByTask(Long taskId) {

        log.info("Getting comments for task: {}", taskId);

        User user = currentUserService.getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ObjectNotFoundException("Task", taskId));

        boardAccessService.checkAccess(task.getBoard(), user);

        return commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        log.warn("Deleting comment id: {}", id);

        User user = currentUserService.getCurrentUser();

        Comment comment = commentRepository.findById(id)
                        .orElseThrow(() -> new ObjectNotFoundException("Comment", id));

        boardAccessService.checkAccess(comment.getTask().getBoard(), user);

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only delete your own comments");
        }

        activityService.record(comment.getTask(), user, ActivityType.COMMENT_DELETED, comment.getText(), null);

        commentRepository.delete(comment);
    }

    private CommentDTO mapToDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setText(comment.getText());
        commentDTO.setTaskId(comment.getTask().getId());
        commentDTO.setUserId(comment.getUser().getId());
        commentDTO.setUsername(comment.getUser().getUsername());
        return commentDTO;
    }

    @Transactional
    public CommentDTO update(Long id, CommentDTO commentDTO) {
        log.info("Updating comment id: {}", id);

        User user = currentUserService.getCurrentUser();

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Comment", id));

        boardAccessService.checkAccess(comment.getTask().getBoard(), user);

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only edit your own comments");
        }

        activityService.record(comment.getTask(), user, ActivityType.COMMENT_UPDATED, comment.getText(), commentDTO.getText());

        comment.setText(commentDTO.getText());




        return mapToDTO(commentRepository.save(comment));
    }


}
