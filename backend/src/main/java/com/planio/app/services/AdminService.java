package com.planio.app.services;

import com.planio.app.entity.Roles;
import com.planio.app.entity.User;
import com.planio.app.exceptions.AccessDeniedException;
import com.planio.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public void updateRole(Long userId, Roles newRole) {

        User user = userRepository.findById(userId)
                .orElseThrow();

        User currentUser = currentUserService.getCurrentUser();

        if(currentUser.getId().equals(user.getId())
                && newRole == Roles.USER) {

            throw new AccessDeniedException(
                    "You cannot remove your own admin role"
            );
        }

        if (user.getRole() == Roles.ADMIN
                && newRole == Roles.USER) {

            long admins = userRepository.countByRole(Roles.ADMIN);

            if (admins <= 1) {
                throw new AccessDeniedException(
                        "Cannot remove the last admin");
            }
        }

        user.setRole(newRole);

        userRepository.save(user);
    }
}
