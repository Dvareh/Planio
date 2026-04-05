package com.planio.app.services;

import com.planio.app.dto.CommentDTO;
import com.planio.app.entity.Comment;
import com.planio.app.repositories.CommentRepository;
import com.planio.app.repositories.TaskRepository;
import com.planio.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public CommentDTO create(CommentDTO commentDTO) {
        Comment comment = Comment.builder()
                .text(commentDTO.getText())
                .task(taskRepository.findById(commentDTO.getTaskId()).orElseThrow())
                .user(userRepository.findById(commentDTO.getUserId()).orElseThrow())
                .createdAt(LocalDateTime.now())
                .build();

        return mapToDTO(commentRepository.save(comment));
    }

    public List<CommentDTO> getByTask(Long taskId) {
        return commentRepository.findByTaskId(taskId)
                .stream()
                .map(this::mapToDTO)
                .toList();
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
