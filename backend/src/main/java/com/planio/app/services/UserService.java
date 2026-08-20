package com.planio.app.services;

import com.planio.app.dto.ChangePasswordDTO;
import com.planio.app.dto.CurrentUserDTO;
import com.planio.app.dto.UserDTO;
import com.planio.app.entity.Roles;
import com.planio.app.entity.User;
import com.planio.app.exceptions.AccessDeniedException;
import com.planio.app.exceptions.ObjectNotFoundException;
import com.planio.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;

    private UserDTO mapToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());

        return userDTO;
    }

    public UserDTO updateCurrentUser(UserDTO userDTO) {

        User currentUser = currentUserService.getCurrentUser();

        if (!currentUser.getEmail().equals(userDTO.getEmail())
                && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already exists"
            );
        }

        currentUser.setUsername(userDTO.getUsername());
        currentUser.setEmail(userDTO.getEmail());

        return mapToDTO(userRepository.save(currentUser));
    }

    public UserDTO getById(Long id) {
        log.info("Fetching user by id: {}", id);
        return mapToDTO(userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")));
    }

    public List<UserDTO> getAll() {
        log.info("Fetching all users");
        return userRepository.findAll().
                stream().
                map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public UserDTO update(Long id, UserDTO userDTO) {
        log.info("Updating user id: {}", id);

        User user = userRepository.findById(id).orElseThrow();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        return mapToDTO(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        log.warn("Deleting user id: {}", id);
        userRepository.deleteById(id);
    }

    public CurrentUserDTO getCurrentUser() {

        User user = currentUserService.getCurrentUser();

        CurrentUserDTO currentUserDTO = new CurrentUserDTO();

        currentUserDTO.setId(user.getId());
        currentUserDTO.setName(user.getUsername());
        currentUserDTO.setEmail(user.getEmail());
        currentUserDTO.setRole(user.getRole().name());

        return currentUserDTO;
    }

    @Transactional
    public void changePassword(ChangePasswordDTO changePasswordDTO) {

        User user = currentUserService.getCurrentUser();
        log.info("Changing password for user: {}", user.getEmail());

        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new AccessDeniedException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));

        userRepository.save(user);

        log.info("Password changed successfully for user: {}", user.getEmail());
    }

    public UserDTO changeRole(Long id, Roles role) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User", id));

        user.setRole(role);

        return mapToDTO(userRepository.save(user));
    }
}
