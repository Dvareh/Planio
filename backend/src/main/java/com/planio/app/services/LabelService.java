package com.planio.app.services;

import com.planio.app.dto.LabelDTO;
import com.planio.app.entity.Board;
import com.planio.app.entity.Label;
import com.planio.app.entity.User;
import com.planio.app.exceptions.ObjectNotFoundException;
import com.planio.app.repositories.BoardRepository;
import com.planio.app.repositories.LabelRepository;
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
public class LabelService {

    private final LabelRepository labelRepository;
    private final BoardRepository boardRepository;
    private final CurrentUserService currentUserService;
    private final BoardAccessService boardAccessService;

    public List<LabelDTO> getBoardLabels(Long boardId) {

        User currentUser = currentUserService.getCurrentUser();

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ObjectNotFoundException("Board", boardId));

        boardAccessService.checkAccess(board, currentUser);

        List<Label> labels = labelRepository.findByBoard_IdOrderByNameAsc(boardId);

        List<LabelDTO> result = new ArrayList<>();

        for (Label label : labels) {
            result.add(mapToDTO(label));
        }

        return result;
    }

    @Transactional
    public LabelDTO createLabel(Long boardId, LabelDTO labelDTO) {

        User currentUser = currentUserService.getCurrentUser();

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ObjectNotFoundException("Board", boardId));

        boardAccessService.checkOwner(board, currentUser);

        String name = labelDTO.getName().trim();

        boolean exists =
                labelRepository.existsByBoard_IdAndNameIgnoreCase(boardId, name);

        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Label already exists");
        }

        Label label = Label.builder()
                .name(name)
                .board(board)
                .build();

        Label savedLabel = labelRepository.save(label);

        log.info("Label {} created for board {}", name, boardId
        );

        return mapToDTO(savedLabel);
    }

    @Transactional
    public void deleteLabel(Long boardId, Long labelId) {

        User currentUser = currentUserService.getCurrentUser();

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ObjectNotFoundException("Board", boardId));

        boardAccessService.checkOwner(board, currentUser);

        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new ObjectNotFoundException("Label", labelId));

        if (label.getBoard().getId() != boardId) {
            throw new ObjectNotFoundException("Label", labelId);
        }

        labelRepository.delete(label);

        log.info("Label {} deleted from board {}", labelId, boardId);
    }

    private LabelDTO mapToDTO(Label label) {

        LabelDTO DTO = new LabelDTO();

        DTO.setId(label.getId());
        DTO.setName(label.getName());
        DTO.setBoardId(label.getBoard().getId());

        return DTO;
    }
}