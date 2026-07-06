package com.planio.app.services;

import com.planio.app.entity.Board;
import com.planio.app.entity.User;
import com.planio.app.exceptions.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class BoardAccessService {
    public void checkAccess(Board board, User user) {

        boolean isOwner = board.getOwner().getId().equals(user.getId());

        boolean isParticipant = board.getParticipants()
                .stream()
                .anyMatch(u -> u.getId().equals(user.getId()));

        if (!isOwner && !isParticipant) {
            throw new AccessDeniedException("No access to this board");
        }
    }
    public boolean hasAccess(Board board, User user) {

        return board.getOwner().getId().equals(user.getId())
                || board.getParticipants().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));
    }
}
